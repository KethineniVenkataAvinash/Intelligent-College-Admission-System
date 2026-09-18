package com.college.admission.security;

public final class SecurityContext {

    private static final ThreadLocal<SecuritySession>
            CURRENT_SESSION =
            new ThreadLocal<>();


    private SecurityContext() {
    }


    // ============================================================
    // SET SESSION
    // ============================================================

    public static void setSession(
            SecuritySession session
    ) {

        if (session == null) {

            throw new IllegalArgumentException(
                    "Security session cannot be null."
            );
        }

        CURRENT_SESSION.set(session);
    }


    // ============================================================
    // GET SESSION
    // ============================================================

    public static SecuritySession getSession() {

        return CURRENT_SESSION.get();
    }


    // ============================================================
    // CHECK LOGIN
    // ============================================================

    public static boolean isAuthenticated() {

        SecuritySession session =
                CURRENT_SESSION.get();

        return session != null
                && session.isActive();
    }


    // ============================================================
    // CLEAR SESSION
    // ============================================================

    public static void clear() {

        SecuritySession session =
                CURRENT_SESSION.get();

        if (session != null) {

            session.invalidate();
        }

        CURRENT_SESSION.remove();
    }
}