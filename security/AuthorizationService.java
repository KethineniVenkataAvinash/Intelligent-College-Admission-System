package com.college.admission.security;

import com.college.admission.enums.Permission;
import com.college.admission.enums.Role;
import com.college.admission.exception.AuthorizationException;

public class AuthorizationService {


    // ============================================================
    // REQUIRE LOGIN
    // ============================================================

    public void requireAuthenticated() {

        if (!SecurityContext.isAuthenticated()) {

            throw new AuthorizationException(
                    "Authentication is required."
            );
        }
    }


    // ============================================================
    // REQUIRE ROLE
    // ============================================================

    public void requireRole(
            Role requiredRole
    ) {

        requireAuthenticated();

        SecuritySession session =
                SecurityContext.getSession();

        if (!session.hasRole(requiredRole)) {

            throw new AuthorizationException(
                    "Access denied. Required role: "
                            + requiredRole
            );
        }
    }


    // ============================================================
    // REQUIRE PERMISSION
    // ============================================================

    public void requirePermission(
            Permission permission
    ) {

        requireAuthenticated();

        SecuritySession session =
                SecurityContext.getSession();

        if (!session.hasPermission(permission)) {

            throw new AuthorizationException(
                    "Access denied. Required permission: "
                            + permission
            );
        }
    }


    // ============================================================
    // REQUIRE ADMIN
    // ============================================================

    public void requireAdmin() {

        requireRole(Role.ADMIN);
    }


    // ============================================================
    // CURRENT SESSION
    // ============================================================

    public SecuritySession getCurrentSession() {

        requireAuthenticated();

        return SecurityContext.getSession();
    }
}