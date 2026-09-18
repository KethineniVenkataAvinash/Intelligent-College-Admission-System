package com.college.admission.service;

import com.college.admission.enums.ExamSession;
import com.college.admission.enums.ExamStatus;

import com.college.admission.jpa.ExamJpaRepository;
import com.college.admission.jpa.ExamAllocationJpaRepository;

import com.college.admission.model.Exam;
import com.college.admission.model.ExamAllocation;
import com.college.admission.model.ExamCentre;
import com.college.admission.model.ExamRegistration;
import com.college.admission.model.ExamRoom;
import com.college.admission.model.ExamSchedule;
import com.college.admission.model.ExamSeat;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * ================================================================
 * EXAM SERVICE
 * ================================================================
 *
 * Responsibilities:
 *
 * 1. Exam management
 * 2. Exam schedule management
 * 3. Exam registration
 * 4. Examination centre management
 * 5. Examination room management
 * 6. Examination seat allocation
 * 7. Student examination details
 * 8. Admin examination reports
 *
 * DATABASE-BACKED:
 *
 * Exam
 * ExamSchedule
 * ExamRegistration
 * ExamAllocation
 *
 * IN-MEMORY:
 *
 * ExamCentre
 * ExamRoom
 * ExamSeat
 *
 * The database is the source of truth for exams,
 * schedules, registrations and allocations.
 *
 * The existing ExamAllocation map is retained only
 * as a compatibility/cache layer for older code.
 *
 * Existing centre/room/seat logic is preserved so
 * the current Main.java continues to work.
 */
public class ExamService {

    // ============================================================
    // DATABASE REPOSITORY
    // ============================================================

    private final ExamJpaRepository examRepository;

    /*
     * Persistent examination allocation repository.
     *
     * Exam allocations are stored in MySQL through JPA.
     */
    private final ExamAllocationJpaRepository examAllocationRepository;


    // ============================================================
    // EXISTING EXAM-CENTRE SYSTEM
    // ============================================================

    private final List<ExamCentre> examCentres;


    /*
     * Existing exam allocation system.
     *
     * Key:
     *
     * Application Number
     * +
     * Exam Code
     * +
     * Exam Date
     * +
     * Session
     */
    private final Map<String, ExamAllocation> examAllocations;


    // ============================================================
    // DATE FORMATTER
    // ============================================================

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ExamService() {

        examRepository =
                new ExamJpaRepository();

        examAllocationRepository =
                new ExamAllocationJpaRepository();

        examCentres =
                new ArrayList<>();

        examAllocations =
                new HashMap<>();

        initializeExamCentres();
    }


    // ============================================================
    // EXAM MANAGEMENT
    // ============================================================

    /**
     * Create a new examination.
     *
     * Data is now stored in MySQL through JPA.
     */
    public void createExam(
            String examCode,
            String examName,
            String description
    ) {

        ServiceAuthorization.requireAdmin();

        if (examCode == null
                || examCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Exam code cannot be empty."
            );
        }

        if (examName == null
                || examName.isBlank()) {

            throw new IllegalArgumentException(
                    "Exam name cannot be empty."
            );
        }

        if (description == null
                || description.isBlank()) {

            throw new IllegalArgumentException(
                    "Exam description cannot be empty."
            );
        }

        String normalizedCode =
                examCode.trim().toUpperCase();

        /*
         * Check database.
         */
        if (examRepository.findExamByCode(
                normalizedCode
        ) != null) {

            throw new IllegalArgumentException(
                    "An exam with this code already exists."
            );
        }

        Exam exam =
                new Exam(
                        normalizedCode,
                        examName.trim(),
                        description.trim()
                );

        /*
         * Persist to MySQL.
         */
        examRepository.saveExam(exam);
    }


    /**
     * Find an examination by code.
     *
     * Previously this searched an ArrayList.
     * Now it searches MySQL through JPA.
     */
    public Exam findExam(
            String examCode
    ) {

        if (examCode == null
                || examCode.isBlank()) {

            return null;
        }

        return examRepository.findExamByCode(
                examCode.trim().toUpperCase()
        );
    }


    /**
     * Return all exams.
     */
    public List<Exam> getExams() {

        return examRepository.findAllExams();
    }


    /**
     * Display all exams.
     */
    public void displayExams() {

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                            EXAMS"
        );

        System.out.println(
                "=========================================================================="
        );

        List<Exam> exams =
                examRepository.findAllExams();

        if (exams.isEmpty()) {

            System.out.println(
                    "No exams have been created."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        System.out.printf(
                "%-15s %-35s %-40s%n",
                "Exam Code",
                "Exam Name",
                "Description"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (Exam exam : exams) {

            System.out.printf(
                    "%-15s %-35s %-40s%n",
                    exam.getExamCode(),
                    exam.getExamName(),
                    exam.getDescription()
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }


    // ============================================================
    // EXAM CENTRE INITIALIZATION
    // ============================================================

    private void initializeExamCentres() {

        /*
         * --------------------------------------------------------
         * CENTRE 1
         * --------------------------------------------------------
         */

        ExamCentre centre1 =
                new ExamCentre(
                        "EXC001",
                        "Main Examination Centre",
                        "College Campus - Block A",
                        100
                );

        centre1.addRoom(
                new ExamRoom(
                        "A101",
                        10,
                        5
                )
        );

        centre1.addRoom(
                new ExamRoom(
                        "A102",
                        10,
                        5
                )
        );


        /*
         * --------------------------------------------------------
         * CENTRE 2
         * --------------------------------------------------------
         */

        ExamCentre centre2 =
                new ExamCentre(
                        "EXC002",
                        "North Examination Centre",
                        "College Campus - Block B",
                        100
                );

        centre2.addRoom(
                new ExamRoom(
                        "B101",
                        10,
                        5
                )
        );

        centre2.addRoom(
                new ExamRoom(
                        "B102",
                        10,
                        5
                )
        );


        /*
         * --------------------------------------------------------
         * CENTRE 3
         * --------------------------------------------------------
         */

        ExamCentre centre3 =
                new ExamCentre(
                        "EXC003",
                        "South Examination Centre",
                        "College Campus - Block C",
                        100
                );

        centre3.addRoom(
                new ExamRoom(
                        "C101",
                        10,
                        5
                )
        );

        centre3.addRoom(
                new ExamRoom(
                        "C102",
                        10,
                        5
                )
        );


        examCentres.add(centre1);
        examCentres.add(centre2);
        examCentres.add(centre3);
    }


    // ============================================================
    // EXAM SCHEDULE MANAGEMENT
    // ============================================================

    /**
     * Create an examination schedule.
     *
     * Schedule is persisted in MySQL.
     */
    public void createExamSchedule(
            String examCode,
            LocalDate examDate,
            ExamSession session
    ) {

        ServiceAuthorization.requireAdmin();

        if (examCode == null
                || examCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Exam code cannot be empty."
            );
        }

        if (examDate == null) {

            throw new IllegalArgumentException(
                    "Exam date cannot be null."
            );
        }

        if (session == null) {

            throw new IllegalArgumentException(
                    "Exam session cannot be null."
            );
        }

        Exam exam =
                findExam(examCode);

        if (exam == null) {

            throw new IllegalArgumentException(
                    "Exam does not exist. Create the exam first."
            );
        }


        /*
         * Check duplicate schedule in database.
         */
        ExamSchedule existing =
                examRepository.findSchedule(
                        exam.getExamCode(),
                        examDate,
                        session
                );

        if (existing != null) {

            throw new IllegalArgumentException(
                    "This examination schedule already exists."
            );
        }


        ExamSchedule schedule =
                new ExamSchedule(
                        exam,
                        examDate,
                        session
                );


        /*
         * Persist.
         */
        examRepository.saveSchedule(
                schedule
        );
    }


    /**
     * Return every exam schedule.
     */
    public List<ExamSchedule> getExamSchedules() {

        return examRepository.findAllSchedules();
    }


    /**
     * Return schedules belonging to one examination.
     */
    public List<ExamSchedule> getSchedulesForExam(
            String examCode
    ) {

        if (examCode == null
                || examCode.isBlank()) {

            return new ArrayList<>();
        }

        return examRepository.findSchedulesByExam(
                examCode.trim().toUpperCase()
        );
    }


    /**
     * Return the latest schedule.
     *
     * Existing Main.java uses this method,
     * so it is preserved.
     */
    public ExamSchedule getLatestSchedule() {

        List<ExamSchedule> schedules =
                examRepository.findAllSchedules();

        if (schedules.isEmpty()) {
            return null;
        }

        return schedules.get(
                schedules.size() - 1
        );
    }


    /**
     * Find exact schedule.
     */
    public ExamSchedule findSchedule(
            String examCode,
            LocalDate examDate,
            ExamSession session
    ) {

        if (examCode == null
                || examCode.isBlank()
                || examDate == null
                || session == null) {

            return null;
        }

        return examRepository.findSchedule(
                examCode.trim().toUpperCase(),
                examDate,
                session
        );
    }


    /**
     * Display all schedules.
     */
    public void displayExamSchedules() {

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                       EXAMINATION SCHEDULES"
        );

        System.out.println(
                "=========================================================================="
        );

        List<ExamSchedule> schedules =
                examRepository.findAllSchedules();

        if (schedules.isEmpty()) {

            System.out.println(
                    "No examination schedules have been created."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        System.out.printf(
                "%-10s %-15s %-32s %-15s %-12s%n",
                "ID",
                "Exam Code",
                "Exam Name",
                "Exam Date",
                "Session"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (ExamSchedule schedule :
                schedules) {

            System.out.printf(
                    "%-10s %-15s %-32s %-15s %-12s%n",
                    String.valueOf(
                            schedule.getScheduleId()
                    ),
                    schedule.getExamCode(),
                    schedule.getExamName(),
                    schedule.getExamDate()
                            .format(dateFormatter),
                    schedule.getSession()
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }


    // ============================================================
    // EXAM REGISTRATION
    // ============================================================

    /**
     * Register a student for an examination.
     *
     * Existing Main.java calls this method with:
     *
     * registerStudentForExam(student, examCode)
     *
     * Therefore the method is preserved.
     *
     * The student's registration is attached to the latest
     * available schedule for that examination.
     */
    public ExamRegistration registerStudentForExam(
            Student student,
            String examCode
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (examCode == null
                || examCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Exam code cannot be empty."
            );
        }

        Exam exam =
                findExam(examCode);

        if (exam == null) {

            throw new IllegalArgumentException(
                    "Exam does not exist."
            );
        }


        /*
         * Get schedules for this examination.
         */
        List<ExamSchedule> schedules =
                examRepository.findSchedulesByExam(
                        exam.getExamCode()
                );

        if (schedules.isEmpty()) {

            throw new IllegalStateException(
                    "No schedule has been created for this examination."
            );
        }


        /*
         * Existing UI only asks for Exam Code.
         *
         * Therefore select the latest schedule.
         *
         * A schedule-specific registration method is
         * provided below for future Main.java updates.
         */
        ExamSchedule schedule =
                schedules.get(
                        schedules.size() - 1
                );

        return registerStudentForExam(
                student,
                schedule
        );
    }


    /**
     * Schedule-specific registration.
     *
     * This is the preferred method for the new system.
     */
    public ExamRegistration registerStudentForExam(
            Student student,
            ExamSchedule schedule
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (schedule == null) {

            throw new IllegalArgumentException(
                    "Exam schedule cannot be null."
            );
        }


        /*
         * Check whether this exact student + schedule
         * registration already exists.
         */
        ExamRegistration existing =
                examRepository.findRegistration(
                        student.getUserId(),
                        schedule.getScheduleId()
                );

        if (existing != null) {

            return existing;
        }


        /*
         * Generate registration ID.
         *
         * System-generated.
         */
        String registrationId =
                generateRegistrationId();


        ExamRegistration registration =
                new ExamRegistration(
                        registrationId,
                        student,
                        schedule
                );


        /*
         * Persist registration.
         */
        examRepository.saveRegistration(
                registration
        );


        return registration;
    }


    /**
     * Generate a unique examination registration ID.
     */
    private String generateRegistrationId() {

        return "EXREG"
                + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 20)
                        .toUpperCase();
    }


    /**
     * Find a student's registration for an exam.
     */
    public ExamRegistration getExamRegistration(
            Student student,
            String examCode
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null
                || examCode == null
                || examCode.isBlank()) {

            return null;
        }


        List<ExamRegistration> registrations =
                examRepository
                        .findRegistrationsByApplicationNumber(
                                student.getApplicationNumber()
                        );


        for (ExamRegistration registration :
                registrations) {

            if (registration.getExamCode()
                    .equalsIgnoreCase(
                            examCode.trim()
                    )) {

                return registration;
            }
        }

        return null;
    }


    /**
     * Check whether student is registered.
     */
    public boolean isRegisteredForExam(
            Student student,
            String examCode
    ) {

        return getExamRegistration(
                student,
                examCode
        ) != null;
    }


    /**
     * Display one student's registration.
     */
    public void displayStudentExamRegistration(
            Student student,
            String examCode
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        ExamRegistration registration =
                getExamRegistration(
                        student,
                        examCode
                );

        System.out.println();

        System.out.println(
                "=============================================================="
        );

        System.out.println(
                "                  EXAM REGISTRATION"
        );

        System.out.println(
                "=============================================================="
        );

        if (registration == null) {

            System.out.println(
                    "Student is not registered for this examination."
            );

            System.out.println(
                    "=============================================================="
            );

            return;
        }

        registration.displayRegistration();
    }


    /**
     * Display every registration.
     */
    public void displayAllExamRegistrations() {

        ServiceAuthorization.requireAdmin();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                       EXAM REGISTRATIONS"
        );

        System.out.println(
                "=========================================================================="
        );

        List<ExamRegistration> registrations =
                examRepository.findAllRegistrations();

        if (registrations.isEmpty()) {

            System.out.println(
                    "No exam registrations found."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        System.out.printf(
                "%-20s %-20s %-15s %-30s %-15s%n",
                "Registration ID",
                "Application",
                "Exam Code",
                "Student",
                "Status"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (ExamRegistration registration :
                registrations) {

            System.out.printf(
                    "%-20s %-20s %-15s %-30s %-15s%n",
                    registration.getRegistrationId(),
                    registration.getApplicationNumber(),
                    registration.getExamCode(),
                    registration.getStudentName(),
                    registration.getStatus()
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }


    // ============================================================
    // EXAM ALLOCATION
    // ============================================================

    /**
     * Build unique allocation key.
     */
    private String buildAllocationKey(
            String applicationNumber,
            String examCode,
            LocalDate examDate,
            ExamSession session
    ) {

        return applicationNumber
                + "_"
                + examCode.toUpperCase()
                + "_"
                + examDate
                + "_"
                + session.name();
    }


    /**
     * Allocate an examination seat for a student
     * for a specific schedule.
     *
     * Existing ExamCentre / ExamRoom / ExamSeat
     * architecture is preserved.
     */
    public ExamAllocation allocateExam(
            Student student,
            ExamSchedule schedule
    ) {

        ServiceAuthorization.requireAdmin();

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (schedule == null) {

            throw new IllegalArgumentException(
                    "Exam schedule cannot be null."
            );
        }


        String applicationNumber =
                student.getApplicationNumber();

        String examCode =
                schedule.getExamCode();


        /*
         * Student must have a database registration.
         */
        ExamRegistration registration =
                getExamRegistration(
                        student,
                        examCode
                );

        if (registration == null) {

            throw new IllegalStateException(
                    "Student is not registered for "
                            + schedule.getExamName()
                            + "."
            );
        }


        /*
         * Exact allocation key.
         */
        String allocationKey =
                buildAllocationKey(
                        applicationNumber,
                        examCode,
                        schedule.getExamDate(),
                        schedule.getSession()
                );


        /*
         * Prevent duplicate allocation.
         */
        if (examAllocations.containsKey(
                allocationKey
        )) {

            return examAllocations.get(
                    allocationKey
            );
        }


        /*
         * Search centre → room → seat.
         */
        for (ExamCentre centre :
                examCentres) {

            for (ExamRoom room :
                    centre.getRooms()) {

                ExamSeat seat =
                        room.findAvailableSeat();

                if (seat == null) {
                    continue;
                }


                /*
                 * Create allocation object.
                 */
                ExamAllocation allocation =
                        new ExamAllocation(
                                applicationNumber,
                                student.getName(),
                                centre.getCentreCode(),
                                centre.getCentreName(),
                                room.getRoomNumber(),
                                seat.getSeatNumber(),
                                schedule.getExamDate(),
                                schedule.getSession(),
                                ExamStatus.ALLOCATED
                        );


                /*
                 * Persist allocation in MySQL.
                 *
                 * The database is now the source of truth.
                 */
                examAllocationRepository.save(
                        allocation
                );

                /*
                 * Update the in-memory seat only after the
                 * database has accepted the allocation.
                 */
                seat.allocate(
                        applicationNumber
                );


                /*
                 * Keep the existing in-memory map as a
                 * compatibility/cache layer so older Main.java
                 * code continues to work.
                 */
                examAllocations.put(
                        allocationKey,
                        allocation
                );


                /*
                 * Update persistent registration status.
                 */
                examRepository.updateRegistrationStatus(
                        registration.getRegistrationId(),
                        ExamStatus.ALLOCATED
                );


                return allocation;
            }
        }


        throw new IllegalStateException(
                "No examination seat is available for this schedule."
        );
    }


    /**
     * Compatibility method.
     *
     * Existing Main.java can call:
     *
     * allocateExam(student)
     */
    public ExamAllocation allocateExam(
            Student student
    ) {

        ServiceAuthorization.requireAdmin();

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }


        ExamSchedule schedule =
                getLatestSchedule();

        if (schedule == null) {

            throw new IllegalStateException(
                    "No examination schedule has been created."
            );
        }


        return allocateExam(
                student,
                schedule
        );
    }


    // ============================================================
    // EXAM ALLOCATION SEARCH
    // ============================================================

    /**
     * Compatibility method.
     *
     * Returns the first allocation belonging to
     * the application number.
     */
    public ExamAllocation getExamAllocation(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }


        /*
         * Database is the source of truth.
         */
        return examAllocationRepository
                .findByApplicationNumber(
                        applicationNumber.trim()
                );
    }


    /**
     * Exact allocation lookup.
     */
    public ExamAllocation getExamAllocation(
            String applicationNumber,
            String examCode,
            LocalDate examDate,
            ExamSession session
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()
                || examCode == null
                || examCode.isBlank()
                || examDate == null
                || session == null) {

            return null;
        }


        /*
         * Database is the source of truth.
         *
         * examCode is intentionally not used in the
         * allocation table lookup because ExamAllocation
         * currently stores the schedule date/session,
         * while the exam code is obtained from ExamSchedule.
         */
        return examAllocationRepository.findAllocation(
                applicationNumber.trim(),
                examDate,
                session
        );
    }


    /**
     * Exact allocation lookup using Student
     * and ExamSchedule.
     */
    public ExamAllocation getExamAllocation(
            Student student,
            ExamSchedule schedule
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null
                || schedule == null) {

            return null;
        }


        return getExamAllocation(
                student.getApplicationNumber(),
                schedule.getExamCode(),
                schedule.getExamDate(),
                schedule.getSession()
        );
    }


    /**
     * Return all allocations belonging to a student.
     */
    public List<ExamAllocation>
    getAllocationsForStudent(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return new ArrayList<>();
        }


        /*
         * Read directly from MySQL.
         */
        return examAllocationRepository
                .findAllByApplicationNumber(
                        applicationNumber.trim()
                );
    }


    /**
     * Check whether application has
     * an examination allocation.
     */
    public boolean hasExamAllocation(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        return getExamAllocation(
                applicationNumber
        ) != null;
    }


    /**
     * Check exact student/schedule allocation.
     */
    public boolean hasExamAllocation(
            Student student,
            ExamSchedule schedule
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        return getExamAllocation(
                student,
                schedule
        ) != null;
    }


    // ============================================================
    // STUDENT EXAM DETAILS
    // ============================================================

    /**
     * Display all examination allocations
     * belonging to a student.
     */
    public void displayStudentExamAllocation(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            System.out.println(
                    "Student information is not available."
            );

            return;
        }


        List<ExamAllocation> allocations =
                getAllocationsForStudent(
                        student.getApplicationNumber()
                );


        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                    MY EXAM ALLOCATIONS"
        );

        System.out.println(
                "=========================================================================="
        );


        System.out.println(
                "Student Name       : "
                        + student.getName()
        );

        System.out.println(
                "Application Number : "
                        + student.getApplicationNumber()
        );


        if (allocations.isEmpty()) {

            System.out.println(
                    "Status             : NOT ALLOCATED"
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }


        for (ExamAllocation allocation :
                allocations) {

            System.out.println();

            System.out.println(
                    "Exam Centre Code   : "
                            + allocation.getCentreCode()
            );

            System.out.println(
                    "Exam Centre Name   : "
                            + allocation.getCentreName()
            );

            System.out.println(
                    "Room Number        : "
                            + allocation.getRoomNumber()
            );

            System.out.println(
                    "Exam Seat          : "
                            + allocation.getSeatNumber()
            );

            System.out.println(
                    "Exam Date          : "
                            + allocation.getExamDate()
                                    .format(
                                            dateFormatter
                                    )
            );

            System.out.println(
                    "Session            : "
                            + allocation.getSession()
            );

            System.out.println(
                    "Status             : "
                            + allocation.getStatus()
            );

            System.out.println(
                    "--------------------------------------------------------------------------"
            );
        }


        System.out.println(
                "=========================================================================="
        );
    }


    // ============================================================
    // EXAM CENTRES
    // ============================================================

    public List<ExamCentre> getExamCentres() {

        return examCentres;
    }


    public void displayExamCentres() {

        ServiceAuthorization.requireAdmin();

        System.out.println();

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "                    EXAMINATION CENTRES"
        );

        System.out.println(
                "================================================================"
        );


        if (examCentres.isEmpty()) {

            System.out.println(
                    "No examination centres available."
            );

            return;
        }


        for (int i = 0;
             i < examCentres.size();
             i++) {

            ExamCentre centre =
                    examCentres.get(i);


            System.out.println();

            System.out.println(
                    (i + 1)
                            + ". "
                            + centre.getCentreName()
            );

            System.out.println(
                    "Centre Code      : "
                            + centre.getCentreCode()
            );

            System.out.println(
                    "Address          : "
                            + centre.getAddress()
            );

            System.out.println(
                    "Total Capacity   : "
                            + centre.getTotalCapacity()
            );

            System.out.println(
                    "Room Capacity    : "
                            + centre.getTotalRoomCapacity()
            );

            System.out.println(
                    "Available Seats  : "
                            + centre.getAvailableSeats()
            );

            System.out.println(
                    "----------------------------------------------------------------"
            );
        }


        System.out.println(
                "================================================================"
        );
    }


    // ============================================================
    // ALL EXAM ALLOCATIONS
    // ============================================================

    public void displayAllExamAllocations() {

        ServiceAuthorization.requireAdmin();

        System.out.println();

        System.out.println(
                "=========================================================================="
        );

        System.out.println(
                "                     EXAMINATION ALLOCATIONS"
        );

        System.out.println(
                "=========================================================================="
        );


        List<ExamAllocation> allocations =
                examAllocationRepository.findAll();

        if (allocations.isEmpty()) {

            System.out.println(
                    "No examination allocations found."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }


        System.out.printf(
                "%-20s %-20s %-12s %-12s %-12s %-15s %-12s%n",
                "Application No.",
                "Student",
                "Exam Code",
                "Centre",
                "Room",
                "Exam Date",
                "Session"
        );


        System.out.println(
                "--------------------------------------------------------------------------"
        );


        for (ExamAllocation allocation :
                allocations) {

            System.out.printf(
                    "%-20s %-20s %-12s %-12s %-12s %-15s %-12s%n",
                    allocation.getApplicationNumber(),
                    allocation.getStudentName(),
                    findExamCodeForAllocation(
                            allocation
                    ),
                    allocation.getCentreCode(),
                    allocation.getRoomNumber(),
                    allocation.getExamDate()
                            .format(
                                    dateFormatter
                            ),
                    allocation.getSession()
            );
        }


        System.out.println(
                "=========================================================================="
        );
    }


    /**
     * Find exam code belonging to an allocation.
     */
    private String findExamCodeForAllocation(
            ExamAllocation allocation
    ) {

        if (allocation == null) {
            return "UNKNOWN";
        }


        /*
         * First identify the matching schedule
         * from the database.
         */
        List<ExamSchedule> schedules =
                examRepository.findAllSchedules();


        for (ExamSchedule schedule :
                schedules) {

            if (schedule.getExamDate()
                    .equals(
                            allocation.getExamDate()
                    )
                    && schedule.getSession()
                    == allocation.getSession()) {

                return schedule.getExamCode();
            }
        }


        return "UNKNOWN";
    }


    // ============================================================
    // ALLOCATION ACCESS
    // ============================================================

    /**
     * Compatibility method retained for existing code.
     *
     * New allocation reads should use the database-backed
     * methods in this service. The returned map contains the
     * allocations loaded/created during the current JVM run.
     */
    public Map<String, ExamAllocation>
    getExamAllocations() {

        return examAllocations;
    }

    /**
     * Return every persisted examination allocation.
     *
     * This is the preferred method for new code.
     */
    public List<ExamAllocation>
    getAllPersistedExamAllocations() {

        return examAllocationRepository.findAll();
    }


    // ============================================================
    // EXAMINATION ROOM / SEAT LAYOUT
    // ============================================================

    /**
     * Display all centres, rooms and seats.
     *
     * Existing functionality is preserved.
     */
    public void displayExamRoomLayout() {

        ServiceAuthorization.requireAdmin();

        System.out.println();

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "                 EXAM ROOM / SEAT LAYOUT"
        );

        System.out.println(
                "================================================================"
        );


        if (examCentres.isEmpty()) {

            System.out.println(
                    "No examination centres available."
            );

            return;
        }


        for (ExamCentre centre :
                examCentres) {

            System.out.println();

            System.out.println(
                    "Centre: "
                            + centre.getCentreCode()
                            + " - "
                            + centre.getCentreName()
            );

            System.out.println(
                    "Address: "
                            + centre.getAddress()
            );


            for (ExamRoom room :
                    centre.getRooms()) {

                System.out.println();

                System.out.println(
                        "  Room: "
                                + room.getRoomNumber()
                );

                System.out.println(
                        "  Capacity: "
                                + room.getCapacity()
                );

                System.out.println(
                        "  Rows: "
                                + room.getRows()
                );

                System.out.println(
                        "  Columns: "
                                + room.getColumns()
                );


                System.out.println(
                        "  Seats:"
                );


                ExamSeat[][] seats =
                        room.getSeats();


                for (ExamSeat[] row : seats) {
                    for (ExamSeat seat : row) {

                        System.out.println(
                                "      Seat "
                                        + seat.getSeatNumber()
                                        + " : "
                                        + seat
                        );
                    }
                }
            }


            System.out.println(
                    "----------------------------------------------------------------"
            );
        }


        System.out.println(
                "================================================================"
        );
    }


    /**
     * Compatibility alias.
     *
     * If your Main.java uses another method name,
     * this keeps the service flexible.
     */
    public void displayExamRoomLayoutFromAdmin() {

        ServiceAuthorization.requireAdmin();

        displayExamRoomLayout();
    }


    /**
     * Return the JPA repository used for examination allocations.
     *
     * Kept public so advanced/admin services can perform
     * database-backed allocation operations without accessing
     * the internal map.
     */
    public ExamAllocationJpaRepository
    getExamAllocationRepository() {

        ServiceAuthorization.requireAdmin();

        return examAllocationRepository;
    }


    // ============================================================
    // EXAM REGISTRATION ACCESS
    // ============================================================

    /**
     * Return all registrations for an application.
     *
     * This replaces the old in-memory Map access.
     */
    public List<ExamRegistration>
    getExamRegistrationsForStudent(
            String applicationNumber
    ) {

        ServiceAuthorization.requireApplicationOwnership(
                applicationNumber
        );

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return new ArrayList<>();
        }


        return examRepository
                .findRegistrationsByApplicationNumber(
                        applicationNumber
                );
    }


    /**
     * Return all database registrations.
     */
    public List<ExamRegistration>
    getAllExamRegistrations() {

        ServiceAuthorization.requireAdmin();

        return examRepository.findAllRegistrations();
    }


    // ============================================================
    // DELETE / ADMIN OPERATIONS
    // ============================================================

    /**
     * Delete an exam.
     *
     * Use carefully because schedules referencing
     * the exam may prevent deletion depending on
     * database constraints.
     */
    public void deleteExam(
            String examCode
    ) {

        ServiceAuthorization.requireAdmin();

        Exam exam =
                findExam(examCode);

        if (exam == null) {

            throw new IllegalArgumentException(
                    "Exam not found."
            );
        }


        examRepository.deleteExam(
                examCode
        );


        System.out.println();

        System.out.println(
                "Exam deleted successfully."
        );
    }


    /**
     * Delete a schedule.
     */
    public void deleteExamSchedule(
            Long scheduleId
    ) {

        ServiceAuthorization.requireAdmin();

        if (scheduleId == null) {

            throw new IllegalArgumentException(
                    "Schedule ID cannot be null."
            );
        }


        ExamSchedule schedule =
                examRepository.findScheduleById(
                        scheduleId
                );


        if (schedule == null) {

            throw new IllegalArgumentException(
                    "Exam schedule not found."
            );
        }


        examRepository.deleteSchedule(
                scheduleId
        );


        System.out.println();

        System.out.println(
                "Exam schedule deleted successfully."
        );
    }


    // ============================================================
    // REGISTRATION STATUS MANAGEMENT
    // ============================================================

    public void updateRegistrationStatus(
            String registrationId,
            ExamStatus status
    ) {

        ServiceAuthorization.requireAdmin();

        examRepository.updateRegistrationStatus(
                registrationId,
                status
        );
    }


    // ============================================================
    // EXAM CENTRE / ROOM / SEAT ACCESS
    // ============================================================

    public int getTotalExamCentres() {

        return examCentres.size();
    }


    public int getTotalExamSeats() {

        int total = 0;


        for (ExamCentre centre :
                examCentres) {

            for (ExamRoom room :
                    centre.getRooms()) {

                                for (ExamSeat[] seatRow : room.getSeats()) {
                                        total += seatRow.length;
                                }
            }
        }


        return total;
    }


    public int getAvailableExamSeats() {

        int available = 0;


        for (ExamCentre centre :
                examCentres) {

            for (ExamRoom room :
                    centre.getRooms()) {

                                for (ExamSeat[] seatRow :
                                                room.getSeats()) {

                                        for (ExamSeat seat :
                                                        seatRow) {

                                                if (seat.isAvailable()) {

                                                        available++;
                                                }
                                        }
                                }
            }
        }


        return available;
    }


    // ============================================================
    // DEBUG / SUMMARY
    // ============================================================

    public void displayExamSystemSummary() {

        ServiceAuthorization.requireAdmin();

        List<Exam> exams =
                examRepository.findAllExams();

        List<ExamSchedule> schedules =
                examRepository.findAllSchedules();

        List<ExamRegistration> registrations =
                examRepository.findAllRegistrations();


        System.out.println();

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "                  EXAM SYSTEM SUMMARY"
        );

        System.out.println(
                "================================================================"
        );

        System.out.println(
                "Total Exams              : "
                        + exams.size()
        );

        System.out.println(
                "Total Schedules          : "
                        + schedules.size()
        );

        System.out.println(
                "Total Registrations      : "
                        + registrations.size()
        );

        System.out.println(
                "Total Exam Centres      : "
                        + examCentres.size()
        );

        System.out.println(
                "Total Exam Seats         : "
                        + getTotalExamSeats()
        );

        System.out.println(
                "Available Exam Seats     : "
                        + getAvailableExamSeats()
        );

        System.out.println(
                "Current Allocations      : "
                        + examAllocationRepository.count()
        );

        System.out.println(
                "================================================================"
        );
    }
}