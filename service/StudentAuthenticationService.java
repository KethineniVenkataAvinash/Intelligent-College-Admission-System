package com.college.admission.service;

import com.college.admission.enums.Role;
import com.college.admission.exception.AuthenticationException;
import com.college.admission.jpa.StudentJpaRepository;
import com.college.admission.model.Student;
import com.college.admission.security.SecurityManager;
import com.college.admission.security.SecuritySession;
import com.college.admission.util.PasswordUtil;

public class StudentAuthenticationService {

    private final StudentJpaRepository studentRepository;

    private final SecurityManager securityManager;

    public StudentAuthenticationService() {

        this.studentRepository =
                new StudentJpaRepository();

        this.securityManager =
                SecurityManager.getInstance();
    }

    /**
     * Authenticate a student using application number
     * and password stored in MySQL.
     *
     * On successful authentication:
     * 1. Student is marked authenticated.
     * 2. STUDENT SecuritySession is created.
     * 3. SecurityContext is populated.
     */
    public Student login(
            String applicationNumber,
            String password) {

        // ========================================================
        // VALIDATE APPLICATION NUMBER
        // ========================================================

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new AuthenticationException(
                    "Application number cannot be empty."
            );
        }

        // ========================================================
        // VALIDATE PASSWORD
        // ========================================================

        if (password == null
                || password.isBlank()) {

            throw new AuthenticationException(
                    "Password cannot be empty."
            );
        }

        // Remove accidental spaces
        applicationNumber =
                applicationNumber.trim();

        // ========================================================
        // FIND STUDENT FROM DATABASE
        // ========================================================

        Student student =
                studentRepository
                        .findByApplicationNumberForLogin(
                                applicationNumber
                        );

        if (student == null) {

            throw new AuthenticationException(
                    "Invalid application number or password."
            );
        }

        // ========================================================
        // GET STORED PASSWORD HASH
        // ========================================================

        String storedPasswordHash =
                student.getPasswordHash();

        if (storedPasswordHash == null
                || storedPasswordHash.isBlank()) {

            throw new AuthenticationException(
                    "Password information is unavailable."
            );
        }

        // ========================================================
        // VERIFY PASSWORD
        // ========================================================

        boolean passwordMatches =
                PasswordUtil.verifyPassword(
                        password,
                        storedPasswordHash
                );

        if (!passwordMatches) {

            throw new AuthenticationException(
                    "Invalid application number or password."
            );
        }

        if (PasswordUtil.isLegacyHash(
                storedPasswordHash
        )) {
            studentRepository.updatePasswordHash(
                    applicationNumber,
                    PasswordUtil.hashPassword(password)
            );
        }

        // ========================================================
        // STUDENT AUTHENTICATION SUCCESSFUL
        // ========================================================

        student.setAuthenticated(true);

        // ========================================================
        // CREATE STUDENT SECURITY SESSION
        // ========================================================

        SecuritySession session =
                securityManager.createStudentSession(
                        student.getUserId(),
                        student.getApplicationNumber(),
                        student.getName()
                );

        // ========================================================
        // STORE SESSION IN SECURITY CONTEXT
        // ========================================================

        securityManager.login(session);

        return student;
    }

    /**
     * Logout the currently authenticated student.
     */
    public void logout(Student student) {

        if (student != null) {

            student.setAuthenticated(false);
        }

        /*
         * Clear ThreadLocal security session.
         */
        SecuritySession session =
                securityManager.getCurrentSession();

        if (session != null
                && session.hasRole(Role.STUDENT)) {

            securityManager.logout();
        }
    }

    /**
     * Return the currently authenticated student session.
     */
    public SecuritySession getCurrentStudentSession() {

        SecuritySession session =
                securityManager.getCurrentSession();

        if (session == null
                || !session.hasRole(Role.STUDENT)) {

            throw new AuthenticationException(
                    "No authenticated student session."
            );
        }

        return session;
    }
}