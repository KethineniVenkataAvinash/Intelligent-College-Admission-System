package com.college.admission.security;

import com.college.admission.jpa.AdminJpaEntity;
import com.college.admission.jpa.EntityManagerFactoryProvider;

public class AdminAuthenticationTest {

    public static void main(String[] args) {

        AdminAuthenticationService
                authenticationService =
                new AdminAuthenticationService();

        String uniqueId =
                String.valueOf(
                        System.currentTimeMillis()
                );

        String employeeId =
                "ADMIN" + uniqueId;

        String email =
                "admin" + uniqueId + "@test.com";

        try {

            // ====================================================
            // REGISTER
            // ====================================================

            System.out.println();
            System.out.println(
                    "======================================"
            );
            System.out.println(
                    "       ADMIN JPA AUTHENTICATION TEST"
            );
            System.out.println(
                    "======================================"
            );

            System.out.println();
            System.out.println(
                    "ADMIN REGISTRATION TEST"
            );

            AdminJpaEntity admin =
                    authenticationService.registerAdmin(
                            employeeId,
                            "Test Administrator",
                            email,
                            "9876543210",
                            "admin123"
                    );

            System.out.println(
                    "Admin created successfully."
            );

            System.out.println(
                    "Admin ID    : "
                            + admin.getAdminId()
            );

            System.out.println(
                    "Employee ID : "
                            + admin.getEmployeeId()
            );

            System.out.println(
                    "Role        : "
                            + admin.getRole()
            );


            // ====================================================
            // LOGIN
            // ====================================================

            System.out.println();
            System.out.println(
                    "ADMIN LOGIN TEST"
            );

            SecuritySession session =
                    authenticationService.login(
                            employeeId,
                            "admin123"
                    );

            System.out.println(
                    "Login successful."
            );

            System.out.println(
                    "Username : "
                            + session.getUsername()
            );

            System.out.println(
                    "Name     : "
                            + session.getDisplayName()
            );

            System.out.println(
                    "Role     : "
                            + session.getRole()
            );

            System.out.println(
                    "Active   : "
                            + session.isActive()
            );


            // ====================================================
            // AUTHORIZATION
            // ====================================================

            System.out.println();
            System.out.println(
                    "ADMIN AUTHORIZATION TEST"
            );

            SecurityManager
                    .getInstance()
                    .getAuthorizationService()
                    .requireAdmin();

            System.out.println(
                    "Admin authorization successful."
            );


            // ====================================================
            // LOGOUT
            // ====================================================

            System.out.println();
            System.out.println(
                    "ADMIN LOGOUT TEST"
            );

            authenticationService.logout();

            System.out.println(
                    "Authenticated after logout : "
                            + SecurityContext
                            .isAuthenticated()
            );

            System.out.println();
            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "       ADMIN SECURITY TEST PASSED"
            );

            System.out.println(
                    "======================================"
            );

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "ADMIN SECURITY TEST FAILED"
            );

            e.printStackTrace();

        } finally {

            EntityManagerFactoryProvider.close();
        }
    }
}