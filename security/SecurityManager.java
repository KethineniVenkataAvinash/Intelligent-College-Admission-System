package com.college.admission.security;

import com.college.admission.enums.Permission;
import com.college.admission.enums.Role;

import java.util.EnumSet;
import java.util.Set;

public final class SecurityManager {

    private static volatile SecurityManager instance;

    private final AuthorizationService
            authorizationService;


    // ============================================================
    // PRIVATE CONSTRUCTOR
    // ============================================================

    private SecurityManager() {

        authorizationService =
                new AuthorizationService();
    }


    // ============================================================
    // SINGLETON
    // ============================================================

    public static SecurityManager getInstance() {

        if (instance == null) {

            synchronized (SecurityManager.class) {

                if (instance == null) {

                    instance =
                            new SecurityManager();
                }
            }
        }

        return instance;
    }


    // ============================================================
    // STUDENT PERMISSIONS
    // ============================================================

    public Set<Permission>
    getStudentPermissions() {

        return EnumSet.of(

                Permission.VIEW_OWN_PROFILE,

                Permission.VIEW_OWN_APPLICATION,

                Permission.UPDATE_OWN_APPLICATION,

                Permission.VIEW_OWN_RANK,

                Permission.VIEW_OWN_ALLOCATION,

                Permission.VIEW_OWN_SCHOLARSHIP,

                Permission.VIEW_OWN_PAYMENT,

                Permission.VIEW_OWN_RECEIPT,

                Permission.VIEW_OWN_HALL_TICKET,

                Permission.VIEW_OWN_HOSTEL,

                Permission.VIEW_OWN_TRANSPORT,

                Permission.VIEW_OWN_NOTIFICATIONS
        );
    }


    // ============================================================
    // ADMIN PERMISSIONS
    // ============================================================

    public Set<Permission>
    getAdminPermissions() {

        return EnumSet.allOf(
                Permission.class
        );
    }


    // ============================================================
    // CREATE STUDENT SESSION
    // ============================================================

    public SecuritySession
    createStudentSession(
            Long userId,
            String username,
            String displayName
    ) {

        return new SecuritySession(
                userId,
                username,
                displayName,
                Role.STUDENT,
                getStudentPermissions()
        );
    }


    // ============================================================
    // CREATE ADMIN SESSION
    // ============================================================

    public SecuritySession
    createAdminSession(
            Long userId,
            String username,
            String displayName
    ) {

        return new SecuritySession(
                userId,
                username,
                displayName,
                Role.ADMIN,
                getAdminPermissions()
        );
    }


    // ============================================================
    // AUTHORIZATION SERVICE
    // ============================================================

    public AuthorizationService
    getAuthorizationService() {

        return authorizationService;
    }


    // ============================================================
    // LOGIN
    // ============================================================

    public void login(
            SecuritySession session
    ) {

        SecurityContext.setSession(
                session
        );
    }


    // ============================================================
    // LOGOUT
    // ============================================================

    public void logout() {

        SecurityContext.clear();
    }


    // ============================================================
    // CURRENT SESSION
    // ============================================================

    public SecuritySession
    getCurrentSession() {

        return SecurityContext.getSession();
    }
}