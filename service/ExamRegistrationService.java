package com.college.admission.service;

import com.college.admission.model.Exam;
import com.college.admission.model.ExamRegistration;
import com.college.admission.model.ExamSchedule;
import com.college.admission.security.ServiceAuthorization;
import com.college.admission.model.Student;

import java.util.List;

public class ExamRegistrationService {

    private final ExamService examService;

    public ExamRegistrationService(
        ExamService examService
) {

    if (examService == null) {
        throw new IllegalArgumentException(
                "ExamService cannot be null."
        );
    }

    this.examService = examService;
}

    // =========================================================
    // REGISTER USING EXAM CODE
    // =========================================================

    public ExamRegistration registerStudent(
            Student student,
            String examCode
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        if (examCode == null || examCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Exam code cannot be empty."
            );
        }

        Exam exam =
                examService.findExam(
                        examCode.trim().toUpperCase()
                );

        if (exam == null) {
            throw new IllegalArgumentException(
                    "Exam not found: " + examCode
            );
        }

        List<ExamSchedule> schedules =
                examService.getSchedulesForExam(
                        exam.getExamCode()
                );

        if (schedules.isEmpty()) {
            throw new IllegalStateException(
                    "No schedules are available for this exam."
            );
        }

        /*
         * If exactly one schedule exists,
         * register directly.
         */
        if (schedules.size() == 1) {

            return registerStudent(
                    student,
                    schedules.get(0)
            );
        }

        /*
         * Multiple schedules require the student
         * to select one.
         */
        System.out.println();
        System.out.println(
                "=============================================================="
        );
        System.out.println(
                "              AVAILABLE EXAM SCHEDULES"
        );
        System.out.println(
                "=============================================================="
        );

        for (int i = 0; i < schedules.size(); i++) {

            ExamSchedule schedule =
                    schedules.get(i);

            System.out.printf(
                    "%d. %s | %s | %s%n",
                    i + 1,
                    schedule.getExamCode(),
                    schedule.getExamDate(),
                    schedule.getSession()
            );
        }

        System.out.println(
                "=============================================================="
        );

        /*
         * This method deliberately does not use Scanner.
         *
         * Main.java can display schedules and pass
         * the selected schedule to the next method.
         */
        throw new IllegalStateException(
                "Multiple schedules available. " +
                "Select a schedule and use registerStudent(student, schedule)."
        );
    }

    // =========================================================
    // REGISTER FOR SPECIFIC SCHEDULE
    // =========================================================

    public ExamRegistration registerStudent(
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
         * Check for existing registration.
         */
        ExamRegistration existing =
                examService.getExamRegistration(
                        student,
                        schedule.getExamCode()
                );

        if (existing != null) {

            /*
             * If the existing registration belongs to
             * this same schedule, return it.
             */
            if (schedule.getScheduleId()
                    .equals(existing.getScheduleId())) {

                return existing;
            }

            throw new IllegalStateException(
                    "Student is already registered for "
                    + schedule.getExamName()
                    + "."
            );
        }

        /*
         * Create and persist registration.
         */
        ExamRegistration registration =
                examService.registerStudentForExam(
                        student,
                        schedule
                );

        System.out.println();
        System.out.println(
                "Exam registration successful."
        );

        registration.displayRegistration();

        return registration;
    }

    // =========================================================
    // FIND REGISTRATION
    // =========================================================

    public ExamRegistration getRegistration(
            Student student,
            String examCode
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {
            return null;
        }

        if (examCode == null || examCode.isBlank()) {
            return null;
        }

        return examService.getExamRegistration(
                student,
                examCode
        );
    }

    // =========================================================
    // CHECK REGISTRATION
    // =========================================================

    public boolean isRegistered(
            Student student,
            String examCode
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        return examService.isRegisteredForExam(
                student,
                examCode
        );
    }

    // =========================================================
    // DISPLAY STUDENT REGISTRATION
    // =========================================================

    public void displayRegistration(
            Student student,
            String examCode
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        examService.displayStudentExamRegistration(
                student,
                examCode
        );
    }

    // =========================================================
    // DISPLAY ALL STUDENT REGISTRATIONS
    // =========================================================

    public void displayMyRegistrations(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            System.out.println(
                    "Student not found."
            );

            return;
        }

        List<ExamRegistration> registrations =
                examService
                        .getExamRegistrationsForStudent(
                                student.getApplicationNumber()
                        );

        System.out.println();
        System.out.println(
                "=========================================================================="
        );
        System.out.println(
                "                    MY EXAM REGISTRATIONS"
        );
        System.out.println(
                "=========================================================================="
        );

        if (registrations.isEmpty()) {

            System.out.println(
                    "No examination registrations found."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        for (ExamRegistration registration :
                registrations) {

            registration.displayRegistration();
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // =========================================================
    // DISPLAY AVAILABLE SCHEDULES
    // =========================================================

    public void displayAvailableSchedules(
            String examCode
    ) {

        List<ExamSchedule> schedules =
                examService.getSchedulesForExam(
                        examCode
                );

        System.out.println();
        System.out.println(
                "=========================================================================="
        );
        System.out.println(
                "                    AVAILABLE EXAM SCHEDULES"
        );
        System.out.println(
                "=========================================================================="
        );

        if (schedules.isEmpty()) {

            System.out.println(
                    "No schedules available."
            );

            System.out.println(
                    "=========================================================================="
            );

            return;
        }

        System.out.printf(
                "%-5s %-10s %-15s %-15s %-15s%n",
                "No.",
                "ID",
                "Exam Code",
                "Exam Date",
                "Session"
        );

        System.out.println(
                "--------------------------------------------------------------------------"
        );

        for (int i = 0; i < schedules.size(); i++) {

            ExamSchedule schedule =
                    schedules.get(i);

            System.out.printf(
                    "%-5d %-10s %-15s %-15s %-15s%n",
                    i + 1,
                    schedule.getScheduleId(),
                    schedule.getExamCode(),
                    schedule.getExamDate(),
                    schedule.getSession()
            );
        }

        System.out.println(
                "=========================================================================="
        );
    }

    // =========================================================
    // GET SCHEDULES
    // =========================================================

    public List<ExamSchedule> getSchedules(
            String examCode
    ) {

        return examService.getSchedulesForExam(
                examCode
        );
    }

    // =========================================================
    // GET EXAM SERVICE
    // =========================================================

    public ExamService getExamService() {
        return examService;
    }
}