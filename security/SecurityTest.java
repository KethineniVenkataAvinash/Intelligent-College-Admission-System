package com.college.admission.security;

import com.college.admission.annotation.AdminOnly;

public class SecurityTest {


    @AdminOnly
    public void adminOperation() {

        System.out.println(
                "ADMIN OPERATION EXECUTED SUCCESSFULLY."
        );
    }


    public void publicOperation() {

        System.out.println(
                "PUBLIC OPERATION EXECUTED."
        );
    }


    public static void main(String[] args) {

        SecurityManager securityManager =
                SecurityManager.getInstance();

        SecurityTest test =
                new SecurityTest();


        // ========================================================
        // PUBLIC METHOD
        // ========================================================

        try {

            SecurityMethodInvoker.invoke(
                    test,
                    "publicOperation",
                    new Class<?>[]{},
                    new Object[]{}
            );

        } catch (Exception e) {

            System.out.println(
                    "Public operation failed: "
                            + e.getMessage()
            );
        }


        // ========================================================
        // ADMIN METHOD WITHOUT LOGIN
        // ========================================================

        System.out.println();
        System.out.println(
                "ADMIN ACCESS TEST WITHOUT LOGIN"
        );

        try {

            SecurityMethodInvoker.invoke(
                    test,
                    "adminOperation",
                    new Class<?>[]{},
                    new Object[]{}
            );

        } catch (Exception e) {

            System.out.println(
                    "Access correctly denied:"
            );

            System.out.println(
                    e.getMessage()
            );
        }


        // ========================================================
        // ADMIN LOGIN
        // ========================================================

        System.out.println();
        System.out.println(
                "ADMIN LOGIN TEST"
        );

        SecuritySession adminSession =
                securityManager.createAdminSession(
                        1L,
                        "admin",
                        "System Administrator"
                );

        securityManager.login(
                adminSession
        );


        System.out.println(
                "Logged in as : "
                        + SecurityContext
                        .getSession()
                        .getDisplayName()
        );

        System.out.println(
                "Role         : "
                        + SecurityContext
                        .getSession()
                        .getRole()
        );


        // ========================================================
        // ADMIN METHOD AFTER LOGIN
        // ========================================================

        System.out.println();
        System.out.println(
                "ADMIN ACCESS TEST AFTER LOGIN"
        );

        try {

            SecurityMethodInvoker.invoke(
                    test,
                    "adminOperation",
                    new Class<?>[]{},
                    new Object[]{}
            );

        } catch (Exception e) {

            System.out.println(
                    "Admin operation failed:"
            );

            e.printStackTrace();
        }


        // ========================================================
        // LOGOUT
        // ========================================================

        System.out.println();
        System.out.println(
                "LOGOUT TEST"
        );

        securityManager.logout();

        System.out.println(
                "Authenticated : "
                        + SecurityContext
                        .isAuthenticated()
        );
    }
}