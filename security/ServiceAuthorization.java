package com.college.admission.security;

import com.college.admission.enums.Role;
import com.college.admission.exception.AuthorizationException;
import com.college.admission.model.Student;
import com.college.admission.jpa.StudentJpaRepository;

/**
 * Central guards for service-boundary authorization.
 */
public final class ServiceAuthorization {

    private static final AuthorizationService
            AUTHORIZATION_SERVICE =
                    new AuthorizationService();

    private ServiceAuthorization() {
    }

    public static void requireAdmin() {
        AUTHORIZATION_SERVICE.requireAdmin();
    }

    public static void requireAuthenticated() {
        AUTHORIZATION_SERVICE.requireAuthenticated();
    }

    public static void requireStudent() {
        AUTHORIZATION_SERVICE.requireRole(Role.STUDENT);
    }

    public static void requireStudentOwnership(Student student) {
        requireStudent();

        if (student == null
                || student.getUserId() == null
                || !student.getUserId().equals(
                        SecurityContext.getSession().getUserId())) {

            throw new AuthorizationException(
                    "Access denied for the requested student resource."
            );
        }
    }

    public static void requireStudentOrAdminOwnership(Student student) {
        AUTHORIZATION_SERVICE.requireAuthenticated();

        if (SecurityContext.getSession().getRole() == Role.ADMIN) {
            return;
        }

        requireStudentOwnership(student);
    }

    public static void requireApplicationOwnership(
            String applicationNumber
    ) {
        AUTHORIZATION_SERVICE.requireAuthenticated();

        if (SecurityContext.getSession().getRole() == Role.ADMIN) {
            return;
        }

        if (applicationNumber == null
                || applicationNumber.isBlank()) {
            throw new AuthorizationException(
                    "Access denied for the requested resource."
            );
        }

        Student owner =
                new StudentJpaRepository()
                        .findByApplicationNumber(
                                applicationNumber.trim()
                        );

        requireStudentOwnership(owner);
    }
}
