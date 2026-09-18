package com.college.admission.service;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.enums.CounsellingStatus;
import com.college.admission.exception.ValidationException;
import com.college.admission.jpa.StudentJpaRepository;
import com.college.admission.model.Application;
import com.college.admission.model.Student;
import com.college.admission.annotation.AdminOnly;
import com.college.admission.security.ServiceAuthorization;

import java.util.List;

public class CounsellingService {

    private final StudentJpaRepository studentRepository;

    public CounsellingService() {

        this.studentRepository =
                new StudentJpaRepository();
    }

    /*
     * ============================================================
     * REGISTER STUDENT FOR COUNSELLING
     * ============================================================
     */

    public void registerForCounselling(Student student) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            throw new ValidationException(
                    "Student not found."
            );
        }

        String applicationNumber =
                student.getApplicationNumber();

        Student databaseStudent =
                studentRepository
                        .findStudentForCounselling(
                                applicationNumber
                        );

        if (databaseStudent == null) {

            throw new ValidationException(
                    "Student application not found."
            );
        }

        Application application =
                databaseStudent.getApplication();

        if (application == null) {

            throw new IllegalStateException(
                    "Student does not have an application."
            );
        }

        /*
         * Only eligible students can participate
         * in counselling.
         */
        if (application.getStatus()
                != ApplicationStatus.ELIGIBLE) {

            throw new IllegalStateException(
                    "Only eligible students can register "
                            + "for counselling."
            );
        }

        /*
         * Prevent duplicate registration.
         */
        if (application.getCounsellingStatus()
                == CounsellingStatus.REGISTERED) {

            throw new IllegalStateException(
                    "Student is already registered "
                            + "for counselling."
            );
        }

        /*
         * Update the application status.
         */
        application.setCounsellingStatus(
                CounsellingStatus.REGISTERED
        );

        /*
         * Persist to MySQL.
         */
        studentRepository.updateCounsellingStatus(
                applicationNumber,
                CounsellingStatus.REGISTERED
        );

        /*
         * Keep the current Student object consistent.
         */
        if (student.getApplication() != null) {

            student.getApplication()
                    .setCounsellingStatus(
                            CounsellingStatus.REGISTERED
                    );
        }
    }

    /*
     * ============================================================
     * DISPLAY COUNSELLING STUDENTS
     * ============================================================
     */
    @AdminOnly
    public void displayCounsellingStudents() {

        ServiceAuthorization.requireAdmin();

        System.out.println();

        System.out.println(
                "=============================================="
        );

        System.out.println(
                "        COUNSELLING REGISTERED STUDENTS"
        );

        System.out.println(
                "=============================================="
        );

        List<Student> students =
                studentRepository
                        .findCounsellingRegisteredStudents();

        if (students.isEmpty()) {

            System.out.println(
                    "No students registered for counselling."
            );

            return;
        }

        for (Student student : students) {

            Application application =
                    student.getApplication();

            System.out.println();

            System.out.println(
                    "Rank               : "
                            + student.getRank()
            );

            System.out.println(
                    "Application Number : "
                            + student.getApplicationNumber()
            );

            System.out.println(
                    "Student Name       : "
                            + student.getName()
            );

            System.out.println(
                    "Category           : "
                            + student.getCategory()
            );

            System.out.println(
                    "Percentage         : "
                            + student.getPercentage()
            );

            System.out.println(
                    "Counselling Status : "
                            + application
                            .getCounsellingStatus()
            );

            System.out.println(
                    "Course Preferences : "
                            + application
                            .getPreferences()
            );

            System.out.println(
                    "----------------------------------------------"
            );
        }

        System.out.println(
                "Total Registered Students : "
                        + students.size()
        );

        System.out.println(
                "=============================================="
        );
    }

    /*
     * ============================================================
     * UPDATE COUNSELLING STATUS
     * ============================================================
     */
    @AdminOnly
    public void updateCounsellingStatus(
            Student student,
            CounsellingStatus status
    ) {

        ServiceAuthorization.requireAdmin();

        if (student == null) {

            throw new ValidationException(
                    "Student not found."
            );
        }

        if (status == null) {

            throw new IllegalArgumentException(
                    "Counselling status cannot be null."
            );
        }

        String applicationNumber =
                student.getApplicationNumber();

        Student databaseStudent =
                studentRepository
                        .findStudentForCounselling(
                                applicationNumber
                        );

        if (databaseStudent == null) {

            throw new ValidationException(
                    "Student application not found."
            );
        }

        Application application =
                databaseStudent.getApplication();

        if (application == null) {

            throw new IllegalStateException(
                    "Student does not have an application."
            );
        }

        /*
         * Only eligible students can have a
         * counselling status.
         */
        if (application.getStatus()
                != ApplicationStatus.ELIGIBLE
                && application.getStatus()
                != ApplicationStatus.ALLOTTED) {

            throw new IllegalStateException(
                    "Student is not eligible for "
                            + "counselling status update."
            );
        }

        /*
         * Update database.
         */
        studentRepository.updateCounsellingStatus(
                applicationNumber,
                status
        );

        /*
         * Update the database-loaded entity.
         */
        application.setCounsellingStatus(status);

        /*
         * Keep the current object synchronized.
         */
        if (student.getApplication() != null) {

            student.getApplication()
                    .setCounsellingStatus(status);
        }
    }

    /*
     * ============================================================
     * FIND COUNSELLING STUDENT
     * ============================================================
     */
    @AdminOnly
    public Student findStudentForCounselling(
            String applicationNumber
    ) {

        ServiceAuthorization.requireAdmin();

        return studentRepository
                .findStudentForCounselling(
                        applicationNumber
                );
    }
}