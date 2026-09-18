package com.college.admission.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(
        name = "scholarships",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_scholarship_application",
                        columnNames = "application_number"
                )
        }
)
public class Scholarship {

    // =========================================================
    // PRIMARY KEY
    // =========================================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scholarship_id")
    private Long scholarshipId;


    // =========================================================
    // STUDENT DETAILS
    // =========================================================

    @Column(
            name = "application_number",
            nullable = false,
            length = 30
    )
    private String applicationNumber;

    @Column(
            name = "student_name",
            nullable = false,
            length = 150
    )
    private String studentName;

    @Column(
            name = "rank_number",
            nullable = false
    )
    private int rank;


    // =========================================================
    // COURSE DETAILS
    // =========================================================

    @Column(
            name = "course_code",
            nullable = false,
            length = 20
    )
    private String courseCode;

    @Column(
            name = "course_name",
            nullable = false,
            length = 150
    )
    private String courseName;


    // =========================================================
    // FINANCIAL DETAILS
    // =========================================================

    @Column(name = "course_fee", nullable = false, precision = 12, scale = 2)
    private BigDecimal courseFee;

    @Column(
            name = "scholarship_percentage",
            nullable = false
    )
    private double scholarshipPercentage;

    @Column(name = "scholarship_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal scholarshipAmount;

    @Column(name = "net_payable_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal netPayableAmount;


    // =========================================================
    // JPA CONSTRUCTOR
    // =========================================================

    /**
     * Required by JPA/Hibernate.
     */
    protected Scholarship() {
    }


    // =========================================================
    // APPLICATION CONSTRUCTOR
    // =========================================================

    public Scholarship(
            String applicationNumber,
            String studentName,
            int rank,
            String courseCode,
            String courseName,
            BigDecimal courseFee,
            double scholarshipPercentage
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        if (studentName == null
                || studentName.isBlank()) {

            throw new IllegalArgumentException(
                    "Student name cannot be empty."
            );
        }

        if (rank <= 0) {

            throw new IllegalArgumentException(
                    "Rank must be greater than zero."
            );
        }

        if (courseCode == null
                || courseCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Course code cannot be empty."
            );
        }

        if (courseName == null
                || courseName.isBlank()) {

            throw new IllegalArgumentException(
                    "Course name cannot be empty."
            );
        }

        if (courseFee == null
                || courseFee.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Course fee cannot be negative."
            );
        }

        if (scholarshipPercentage < 0
                || scholarshipPercentage > 100) {

            throw new IllegalArgumentException(
                    "Scholarship percentage must be between 0 and 100."
            );
        }

        this.applicationNumber =
                applicationNumber.trim();

        this.studentName =
                studentName.trim();

        this.rank = rank;

        this.courseCode =
                courseCode.trim();

        this.courseName =
                courseName.trim();

        this.courseFee = courseFee;

        this.scholarshipPercentage =
                scholarshipPercentage;

        /*
         * Automatically calculate scholarship amount.
         */
        this.scholarshipAmount =
                courseFee
                        .multiply(BigDecimal.valueOf(scholarshipPercentage))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        /*
         * Automatically calculate final payable amount.
         */
        this.netPayableAmount =
                courseFee.subtract(scholarshipAmount);
    }


    // =========================================================
    // GETTERS
    // =========================================================

    public Long getScholarshipId() {
        return scholarshipId;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public int getRank() {
        return rank;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public BigDecimal getCourseFee() {
        return courseFee;
    }

    public double getScholarshipPercentage() {
        return scholarshipPercentage;
    }

    public BigDecimal getScholarshipAmount() {
        return scholarshipAmount;
    }

    public BigDecimal getNetPayableAmount() {
        return netPayableAmount;
    }


    // =========================================================
    // DISPLAY
    // =========================================================

    public void displayScholarship() {

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                    SCHOLARSHIP DETAILS"
        );

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "Scholarship ID     : "
                        + scholarshipId
        );

        System.out.println(
                "Student Name       : "
                        + studentName
        );

        System.out.println(
                "Application Number : "
                        + applicationNumber
        );

        System.out.println(
                "Rank               : "
                        + rank
        );

        System.out.println(
                "Course Code        : "
                        + courseCode
        );

        System.out.println(
                "Course Name        : "
                        + courseName
        );

        System.out.printf(
                "Course Fee         : ₹%.2f%n",
                courseFee
        );

        System.out.printf(
                "Scholarship        : %.2f%%%n",
                scholarshipPercentage
        );

        System.out.printf(
                "Scholarship Amount : ₹%.2f%n",
                scholarshipAmount
        );

        System.out.printf(
                "Net Payable Amount : ₹%.2f%n",
                netPayableAmount
        );

        System.out.println(
                "=============================================================="
        );
    }


    // =========================================================
    // TOSTRING
    // =========================================================

    @Override
    public String toString() {

        return "Scholarship{" +
                "scholarshipId=" +
                scholarshipId +
                ", applicationNumber='" +
                applicationNumber + '\'' +
                ", studentName='" +
                studentName + '\'' +
                ", rank=" +
                rank +
                ", courseCode='" +
                courseCode + '\'' +
                ", courseName='" +
                courseName + '\'' +
                ", courseFee=" +
                courseFee +
                ", scholarshipPercentage=" +
                scholarshipPercentage +
                ", scholarshipAmount=" +
                scholarshipAmount +
                ", netPayableAmount=" +
                netPayableAmount +
                '}';
    }
}