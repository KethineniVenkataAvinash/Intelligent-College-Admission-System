package com.college.admission.service;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.jpa.StudentJpaRepository;
import com.college.admission.model.Application;
import com.college.admission.model.Course;
import com.college.admission.model.CoursePreference;
import com.college.admission.model.Student;
import com.college.admission.security.ServiceAuthorization;

import java.util.List;

public class ApplicationService {

    private final CourseService courseService;
    private final StudentJpaRepository studentRepository;

    public ApplicationService() {

        this.courseService = new CourseService();
        this.studentRepository = new StudentJpaRepository();
    }

    /*
     * ============================================================
     * CREATE / LOAD APPLICATION
     * ============================================================
     */

    public Application createApplication(Student student) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        /*
         * First check the Student object currently
         * loaded in memory.
         */
        if (student.getApplication() != null) {

            return student.getApplication();
        }

        /*
         * If the application is not loaded in memory,
         * search MySQL using JPA.
         */
        Application existingApplication =
                studentRepository.findApplicationByNumber(
                        student.getApplicationNumber()
                );

        if (existingApplication != null) {

            student.setApplication(
                    existingApplication
            );

            return existingApplication;
        }

        /*
         * If no application exists in MySQL,
         * create a new one.
         */
        Application application =
                new Application(
                        student.getApplicationNumber(),
                        student
                );

        student.setApplication(application);

        /*
         * Save the application into MySQL.
         */
        studentRepository.saveApplication(
                application
        );

        return application;
    }

    /*
     * ============================================================
     * GET STUDENT APPLICATION
     * ============================================================
     */

    public Application getStudentApplication(
            Student student
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        /*
         * If already loaded in memory, use it.
         */
        if (student.getApplication() != null) {

            return student.getApplication();
        }

        /*
         * Otherwise load it from MySQL.
         */
        Application application =
                studentRepository.findApplicationByNumber(
                        student.getApplicationNumber()
                );

        if (application == null) {

            throw new IllegalStateException(
                    "Application has not been created yet."
            );
        }

        /*
         * Attach the loaded application to the
         * current Student object.
         */
        student.setApplication(application);

        return application;
    }

    /*
     * ============================================================
     * DISPLAY COURSE PREFERENCES
     * ============================================================
     */

    public void displayCoursePreferences(Student student) {

        ServiceAuthorization.requireStudentOwnership(student);

        Application application =
                getStudentApplication(student);

        System.out.println();
        System.out.println("======================================");
        System.out.println("        COURSE PREFERENCES");
        System.out.println("======================================");

        if (application.getPreferences().isEmpty()) {

            System.out.println(
                    "No course preferences added."
            );

        } else {

            for (CoursePreference preference :
                    application.getPreferences()) {

                System.out.println(
                        preference
                );
            }
        }
    }

    /*
     * ============================================================
     * ADD COURSE PREFERENCE
     * ============================================================
     */

    public void addCoursePreference(
            Student student,
            String courseCode,
            int preferenceNumber
    ) {

        ServiceAuthorization.requireStudentOwnership(student);

        Application application =
                getStudentApplication(student);

        if (application.getStatus()
                != ApplicationStatus.DRAFT) {

            throw new IllegalStateException(
                    "Course preferences can only be added while "
                            + "application is in DRAFT status."
            );
        }

        if (preferenceNumber <= 0) {

            throw new IllegalArgumentException(
                    "Preference number must be greater than zero."
            );
        }

        List<CoursePreference> preferences =
                application.getPreferences();

        /*
         * Check duplicate course and preference number.
         */
        for (CoursePreference preference :
                preferences) {

            if (preference.getCourse()
                    .getCourseCode()
                    .equalsIgnoreCase(courseCode)) {

                throw new IllegalArgumentException(
                        "This course is already selected."
                );
            }

            if (preference.getPreferenceNumber()
                    == preferenceNumber) {

                throw new IllegalArgumentException(
                        "This preference number is already used."
                );
            }
        }

        /*
         * Find course from the course service.
         */
        Course course =
                courseService.findCourse(courseCode);

        if (course == null) {

            throw new IllegalArgumentException(
                    "Invalid course code."
            );
        }

        /*
         * Create preference.
         */
        CoursePreference preference =
                new CoursePreference(
                        preferenceNumber,
                        course
                );

        /*
         * Maintain both sides of the relationship.
         */
        application.addPreference(
                preference
        );

        /*
         * Persist application + preference
         * into MySQL through JPA.
         */
        studentRepository.saveApplication(
                application
        );
    }

    /*
     * ============================================================
     * SUBMIT APPLICATION
     * ============================================================
     */

    public void submitApplication(Student student) {

        ServiceAuthorization.requireStudentOwnership(student);

        Application application =
                getStudentApplication(student);

        /*
         * Application.submit() performs its own
         * validation.
         */
        application.submit();

        /*
         * Persist updated status.
         */
        studentRepository.saveApplication(
                application
        );
    }

    /*
     * ============================================================
     * DISPLAY APPLICATION
     * ============================================================
     */

    public void displayApplication(Student student) {

        ServiceAuthorization.requireStudentOwnership(student);

        Application application =
                getStudentApplication(student);

        System.out.println();
        System.out.println("======================================");
        System.out.println("        APPLICATION DETAILS");
        System.out.println("======================================");

        System.out.println(
                "Application Number : "
                        + application.getApplicationNumber()
        );

        System.out.println(
                "Student Name       : "
                        + student.getName()
        );

        System.out.println(
                "Status             : "
                        + application.getStatus()
        );

        System.out.println(
                "Counselling Status : "
                        + application.getCounsellingStatus()
        );

        System.out.println();
        System.out.println("Course Preferences:");

        if (application.getPreferences().isEmpty()) {

            System.out.println(
                    "No course preferences added."
            );

        } else {

            for (CoursePreference preference :
                    application.getPreferences()) {

                System.out.println(
                        preference
                );
            }
        }
    }
}