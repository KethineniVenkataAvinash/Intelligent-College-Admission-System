package com.college.admission.service;

import com.college.admission.jpa.ScholarshipJpaRepository;
import com.college.admission.model.Course;
import com.college.admission.model.Scholarship;
import com.college.admission.model.SeatAllocation;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;

import java.util.List;
import java.math.BigDecimal;

public class ScholarshipService {

    private final ScholarshipJpaRepository scholarshipRepository;

    public ScholarshipService() {
        this.scholarshipRepository =
                new ScholarshipJpaRepository();
    }

    // =========================================================
    // SCHOLARSHIP PERCENTAGE
    // =========================================================

    /**
     * Determines scholarship percentage from the student's rank.
     *
     * Project scholarship rules:
     *
     * Rank <= 500       -> 100%
     * Rank <= 2000      -> 50%
     * Rank <= 5000      -> 25%
     * Rank > 5000       -> 0%
     */
    public double getScholarshipPercentage(int rank) {

        if (rank <= 0) {
            throw new IllegalArgumentException(
                    "Rank must be greater than zero."
            );
        }

        if (rank <= 500) {
            return 100.0;
        }

        if (rank <= 2000) {
            return 50.0;
        }

        if (rank <= 5000) {
            return 25.0;
        }

        return 0.0;
    }

    // =========================================================
    // CALCULATE SCHOLARSHIP
    // =========================================================

    /**
     * Creates and permanently stores scholarship details
     * using the student's actual allocated course.
     *
     * Database is the source of truth.
     */
    public Scholarship calculateScholarship(
            Student student,
            SeatAllocation seatAllocation,
            Course course
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (student.getRank() == null) {
            throw new IllegalStateException(
                    "Student rank has not been generated yet."
            );
        }

        if (seatAllocation == null) {
            throw new IllegalStateException(
                    "Admission seat allocation is required "
                            + "before scholarship calculation."
            );
        }

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course cannot be null."
            );
        }

        // -----------------------------------------------------
        // Verify that the allocation belongs to the student
        // -----------------------------------------------------

        if (!student.getApplicationNumber()
                .equals(seatAllocation.getApplicationNumber())) {

            throw new IllegalArgumentException(
                    "Seat allocation does not belong "
                            + "to the given student."
            );
        }

        // -----------------------------------------------------
        // Verify that the allocated course matches
        // the course being used for scholarship calculation
        // -----------------------------------------------------

        if (!course.getCourseCode()
                .equals(seatAllocation.getCourseCode())) {

            throw new IllegalArgumentException(
                    "Course does not match the allocated course."
            );
        }

        // -----------------------------------------------------
        // Check existing database record
        // -----------------------------------------------------

        Scholarship existingScholarship =
                scholarshipRepository.findByApplicationNumber(
                        student.getApplicationNumber()
                );

        if (existingScholarship != null) {

            return existingScholarship;
        }

        // -----------------------------------------------------
        // Calculate scholarship
        // -----------------------------------------------------

        double scholarshipPercentage =
                getScholarshipPercentage(
                        student.getRank()
                );

        Scholarship scholarship =
                new Scholarship(
                        student.getApplicationNumber(),
                        student.getName(),
                        student.getRank(),
                        course.getCourseCode(),
                        course.getCourseName(),
                        course.getAnnualFee(),
                        scholarshipPercentage
                );

        // -----------------------------------------------------
        // Persist into MySQL
        // -----------------------------------------------------

        return scholarshipRepository.save(
                scholarship
        );
    }

    // =========================================================
    // GET SCHOLARSHIP
    // =========================================================

    /**
     * Returns scholarship details from the database.
     */
    public Scholarship getScholarship(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        return scholarshipRepository
                .findByApplicationNumber(
                        applicationNumber.trim()
                );
    }

    // =========================================================
    // CHECK SCHOLARSHIP
    // =========================================================

    /**
     * Checks whether scholarship details already exist
     * in the database.
     */
    public boolean hasScholarship(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return false;
        }

        return scholarshipRepository
                .existsByApplicationNumber(
                        applicationNumber.trim()
                );
    }

    // =========================================================
    // DISPLAY STUDENT SCHOLARSHIP
    // =========================================================

    /**
     * Displays scholarship details for a particular student.
     */
    public void displayStudentScholarship(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            System.out.println(
                    "Student not found."
            );

            return;
        }

        Scholarship scholarship =
                getScholarship(
                        student.getApplicationNumber()
                );

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                    SCHOLARSHIP"
        );

        System.out.println(
                "=============================================================="
        );

        if (scholarship == null) {

            System.out.println(
                    "Scholarship has not been calculated yet."
            );

            System.out.println(
                    "=============================================================="
            );

            return;
        }

        scholarship.displayScholarship();

        System.out.println(
                "=============================================================="
        );
    }

    // =========================================================
    // DISPLAY ALL SCHOLARSHIPS
    // =========================================================

    /**
     * Displays all scholarship records stored in MySQL.
     */
    public void displayAllScholarships() {

        ServiceAuthorization.requireAdmin();

        List<Scholarship> scholarships =
                scholarshipRepository.findAll();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                       SCHOLARSHIP LIST"
        );

        System.out.println(
                "=========================================================================="
        );

        if (scholarships.isEmpty()) {

            System.out.println(
                    "No scholarship records found."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        System.out.printf(
                "%-20s %-22s %-10s %-15s %-15s%n",
                "Application No.",
                "Student",
                "Rank",
                "Scholarship",
                "Net Payable"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (Scholarship scholarship :
                scholarships) {

            System.out.printf(
                    "%-20s %-22s %-10d %-15.2f%% ₹%-14.2f%n",
                    scholarship.getApplicationNumber(),
                    scholarship.getStudentName(),
                    scholarship.getRank(),
                    scholarship.getScholarshipPercentage(),
                    scholarship.getNetPayableAmount()
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // =========================================================
    // FIND BY COURSE
    // =========================================================

    /**
     * Returns all scholarship records for a course.
     */
    public List<Scholarship> getScholarshipsByCourse(
            String courseCode
    ) {

        return scholarshipRepository.findByCourse(
                courseCode
        );
    }

    // =========================================================
    // FULL SCHOLARSHIPS
    // =========================================================

    /**
     * Returns students receiving 100% scholarship.
     */
    public List<Scholarship> getFullScholarships() {

        return scholarshipRepository
                .findFullScholarships();
    }

    // =========================================================
    // PARTIAL SCHOLARSHIPS
    // =========================================================

    /**
     * Returns students receiving partial scholarships.
     */
    public List<Scholarship> getPartialScholarships() {

        return scholarshipRepository
                .findPartialScholarships();
    }

    // =========================================================
    // ZERO SCHOLARSHIPS
    // =========================================================

    /**
     * Returns scholarship records where no scholarship
     * amount is awarded.
     */
    public List<Scholarship> getZeroScholarships() {

        return scholarshipRepository
                .findZeroScholarship();
    }

    // =========================================================
    // RANK RANGE
    // =========================================================

    /**
     * Returns scholarship records within a rank range.
     */
    public List<Scholarship> getScholarshipsByRankRange(
            int minimumRank,
            int maximumRank
    ) {

        return scholarshipRepository.findByRankRange(
                minimumRank,
                maximumRank
        );
    }

    // =========================================================
    // FINANCIAL SUMMARY
    // =========================================================

    /**
     * Returns total original course fees.
     */
    public BigDecimal getTotalCourseFees() {

        ServiceAuthorization.requireAdmin();

        return scholarshipRepository
                .getTotalCourseFees();
    }

    /**
     * Returns total scholarship amount provided.
     */
    public BigDecimal getTotalScholarshipAmount() {

        ServiceAuthorization.requireAdmin();

        return scholarshipRepository
                .getTotalScholarshipAmount();
    }

    /**
     * Returns total amount students have to pay
     * after scholarship deduction.
     */
    public BigDecimal getTotalNetPayableAmount() {

        ServiceAuthorization.requireAdmin();

        return scholarshipRepository
                .getTotalNetPayableAmount();
    }

    // =========================================================
    // COUNTS
    // =========================================================

    /**
     * Returns total number of scholarship records.
     */
    public long getScholarshipCount() {

        ServiceAuthorization.requireAdmin();

        return scholarshipRepository.count();
    }

    /**
     * Returns number of scholarships for a
     * particular percentage.
     */
    public long getScholarshipCountByPercentage(
            double percentage
    ) {

        ServiceAuthorization.requireAdmin();

        return scholarshipRepository
                .countByPercentage(
                        percentage
                );
    }

    // =========================================================
    // DELETE
    // =========================================================

    /**
     * Deletes scholarship by application number.
     */
    public void deleteScholarship(
            String applicationNumber
    ) {

        ServiceAuthorization.requireAdmin();

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        scholarshipRepository
                .deleteByApplicationNumber(
                        applicationNumber.trim()
                );
    }

    // =========================================================
    // REPOSITORY ACCESS
    // =========================================================

    /**
     * Returns the repository when another service
     * needs direct scholarship database operations.
     */
    public ScholarshipJpaRepository
    getScholarshipRepository() {

        ServiceAuthorization.requireAdmin();

        return scholarshipRepository;
    }
}