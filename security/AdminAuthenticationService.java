package com.college.admission.security;

import com.college.admission.enums.Role;
import com.college.admission.exception.AccountLockedException;
import com.college.admission.exception.AuthenticationException;
import com.college.admission.jpa.AdminJpaEntity;
import com.college.admission.jpa.AdminJpaRepository;
import com.college.admission.util.PasswordUtil;

public class AdminAuthenticationService {

    private static final int MAX_LOGIN_ATTEMPTS = 5;

    private final AdminJpaRepository
            adminRepository;

    private final SecurityManager
            securityManager;


    public AdminAuthenticationService() {

        adminRepository =
                new AdminJpaRepository();

        securityManager =
                SecurityManager.getInstance();
    }


    // ============================================================
    // REGISTER ADMIN
    // ============================================================

    public AdminJpaEntity registerAdmin(
            String employeeId,
            String name,
            String email,
            String phoneNumber,
            String password
    ) {

        validateRegistrationData(
                employeeId,
                name,
                email,
                phoneNumber,
                password
        );

        if (adminRepository
                .existsByEmployeeId(employeeId)) {

            throw new AuthenticationException(
                    "Employee ID already exists."
            );
        }

        if (adminRepository
                .existsByEmail(email)) {

            throw new AuthenticationException(
                    "Admin email already exists."
            );
        }

        String passwordHash =
                PasswordUtil.hashPassword(
                        password
                );

        AdminJpaEntity admin =
                new AdminJpaEntity(
                        employeeId,
                        name,
                        email,
                        phoneNumber,
                        passwordHash
                );

        return adminRepository.save(admin);
    }


    // ============================================================
    // LOGIN
    // ============================================================

    public SecuritySession login(
            String employeeId,
            String password
    ) {

        if (employeeId == null
                || employeeId.isBlank()) {

            throw new AuthenticationException(
                    "Employee ID cannot be empty."
            );
        }

        if (password == null
                || password.isBlank()) {

            throw new AuthenticationException(
                    "Password cannot be empty."
            );
        }

        AdminJpaEntity admin =
                adminRepository
                        .findByEmployeeId(
                                employeeId
                        );

        if (admin == null) {

            throw new AuthenticationException(
                    "Invalid employee ID or password."
            );
        }

        if (admin.isAccountLocked()) {

            throw new AccountLockedException(
                    "Admin account is locked after "
                            + MAX_LOGIN_ATTEMPTS
                            + " failed login attempts."
            );
        }

        boolean passwordCorrect =
                PasswordUtil.verifyPassword(
                        password,
                        admin.getPasswordHash()
                );

        if (!passwordCorrect) {

            AdminJpaEntity updated =
                    adminRepository
                            .updateLoginState(
                                    admin.getAdminId(),
                                    false
                            );

            if (updated.isAccountLocked()) {

                throw new AccountLockedException(
                        "Account locked after "
                                + MAX_LOGIN_ATTEMPTS
                                + " failed login attempts."
                );
            }

            int remaining =
                    MAX_LOGIN_ATTEMPTS
                            - updated
                            .getFailedLoginAttempts();

            throw new AuthenticationException(
                    "Invalid employee ID or password. "
                            + remaining
                            + " attempts remaining."
            );
        }

        if (PasswordUtil.isLegacyHash(
                admin.getPasswordHash()
        )) {
            adminRepository.updatePasswordHash(
                    admin.getAdminId(),
                    PasswordUtil.hashPassword(password)
            );
        }

        /*
         * Successful login.
         */
        AdminJpaEntity updated =
                adminRepository
                        .updateLoginState(
                                admin.getAdminId(),
                                true
                        );

        SecuritySession session =
                securityManager.createAdminSession(
                        updated.getAdminId(),
                        updated.getEmployeeId(),
                        updated.getName()
                );

        securityManager.login(
                session
        );

        return session;
    }


    // ============================================================
    // LOGOUT
    // ============================================================

    public void logout() {

        securityManager.logout();
    }


    // ============================================================
    // CURRENT ADMIN
    // ============================================================

    public SecuritySession
    getCurrentAdminSession() {

        SecuritySession session =
                securityManager
                        .getCurrentSession();

        if (session == null
                || !session.hasRole(Role.ADMIN)) {

            throw new AuthenticationException(
                    "No authenticated admin session."
            );
        }

        return session;
    }


    // ============================================================
    // VALIDATION
    // ============================================================

    private void validateRegistrationData(
            String employeeId,
            String name,
            String email,
            String phoneNumber,
            String password
    ) {

        if (employeeId == null
                || employeeId.isBlank()) {

            throw new IllegalArgumentException(
                    "Employee ID cannot be empty."
            );
        }

        if (name == null
                || name.isBlank()) {

            throw new IllegalArgumentException(
                    "Admin name cannot be empty."
            );
        }

        if (email == null
                || email.isBlank()) {

            throw new IllegalArgumentException(
                    "Admin email cannot be empty."
            );
        }

        if (phoneNumber == null
                || phoneNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Phone number cannot be empty."
            );
        }

        if (password == null
                || password.length() < 6) {

            throw new IllegalArgumentException(
                    "Admin password must contain at least 6 characters."
            );
        }
    }
}