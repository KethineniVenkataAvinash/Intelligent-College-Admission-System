package com.college.admission.service;

import com.college.admission.enums.ApplicationStatus;
import com.college.admission.exception.ValidationException;
import com.college.admission.jpa.StudentJpaRepository;
import com.college.admission.model.Application;
import com.college.admission.model.Student;
import com.college.admission.annotation.AdminOnly;
import com.college.admission.jpa.ApplicationJpaRepository;
import com.college.admission.security.ServiceAuthorization;

import java.math.BigDecimal;
import java.util.List;

public class ApplicationVerificationService {

    private final StudentJpaRepository studentRepository;
    private final ApplicationJpaRepository applicationRepository;

    public ApplicationVerificationService() {
        this.studentRepository =
                new StudentJpaRepository();
        this.applicationRepository =
        new ApplicationJpaRepository();        
    }
    
    /**
 * Finds an application by application number
 * regardless of its current status.
 */
@AdminOnly
public Application findApplicationByNumber(
        String applicationNumber
) {

    ServiceAuthorization.requireAdmin();

    if (applicationNumber == null
            || applicationNumber.isBlank()) {

        return null;
    }

    return applicationRepository
            .findByApplicationNumber(
                    applicationNumber
            );
}




    @AdminOnly

    /*
     * ============================================================
     * DISPLAY SUBMITTED APPLICATIONS
     * ============================================================
     */

    public void displaySubmittedApplications() {

        ServiceAuthorization.requireAdmin();

        System.out.println();
        System.out.println("======================================");
        System.out.println("       SUBMITTED APPLICATIONS");
        System.out.println("======================================");

        List<Student> students =
                studentRepository
                        .findAllSubmittedApplications();

        if (students.isEmpty()) {

            System.out.println(
                    "No submitted applications found."
            );

            return;
        }

        for (Student student : students) {

            Application application =
                    student.getApplication();

            System.out.println();

            System.out.println(
                    "Application Number : "
                            + application
                                    .getApplicationNumber()
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
                    "12th Percentage    : "
                            + student.getPercentage()
            );

            System.out.println(
                    "Status             : "
                            + application.getStatus()
            );

            System.out.println(
                    "Course Preferences : "
                            + application
                                    .getPreferences()
            );

            System.out.println(
                    "--------------------------------------"
            );
        }
    }
    @AdminOnly
    /*
     * ============================================================
     * FIND SUBMITTED APPLICATION
     * ============================================================
     */

    public Student findSubmittedApplication(
            String applicationNumber
    ) {

        ServiceAuthorization.requireAdmin();

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        return studentRepository
                .findSubmittedApplication(
                        applicationNumber
                );
    }

    /*
     * ============================================================
     * VERIFY APPLICATION
     * ============================================================
     */
    @AdminOnly
    
    public void verifyApplication(Student student) {

        ServiceAuthorization.requireAdmin();

        if (student == null) {

            throw new ValidationException(
                    "Student not found."
            );
        }

        Application application =
                student.getApplication();

        if (application == null) {

            throw new IllegalStateException(
                    "Student does not have an application."
            );
        }

        if (application.getStatus()
                != ApplicationStatus.SUBMITTED) {

            throw new IllegalStateException(
                    "Only submitted applications can be verified."
            );
        }

        /*
         * Change status in memory.
         */
        application.verify();

        /*
         * Persist the new status to MySQL.
         */
        studentRepository.updateApplicationStatus(
                application.getApplicationNumber(),
                ApplicationStatus.VERIFIED
        );
    }

    /*
     * ============================================================
     * CHECK ELIGIBILITY
     * ============================================================
     */
    @AdminOnly
    
    public boolean checkEligibility(Student student) {

        ServiceAuthorization.requireAdmin();

        if (student == null) {

            throw new ValidationException(
                    "Student not found."
            );
        }

        /*
         * Current project eligibility rule:
         * minimum 50% in 12th.
         */
        return student.getPercentage() != null
                && student.getPercentage().compareTo(BigDecimal.valueOf(50.0)) >= 0;
    }

    /*
     * ============================================================
     * UPDATE ELIGIBILITY STATUS
     * ============================================================
     */
    @AdminOnly
    
    public void updateEligibilityStatus(
            Student student
    ) {

        ServiceAuthorization.requireAdmin();

        if (student == null) {

            throw new ValidationException(
                    "Student not found."
            );
        }

        Application application =
                student.getApplication();

        if (application == null) {

            throw new IllegalStateException(
                    "Application does not exist."
            );
        }

        if (application.getStatus()
                != ApplicationStatus.VERIFIED) {

            throw new IllegalStateException(
                    "Application must be verified before "
                            + "eligibility checking."
            );
        }

        boolean eligible =
                checkEligibility(student);

        ApplicationStatus newStatus =
                eligible
                        ? ApplicationStatus.ELIGIBLE
                        : ApplicationStatus.NOT_ELIGIBLE;

        /*
         * Update the in-memory entity.
         */
        application.setStatus(newStatus);

        /*
         * Update MySQL.
         */
        studentRepository.updateEligibilityStatus(
                application.getApplicationNumber(),
                newStatus
        );
    }
}