package com.college.admission.security;

import com.college.admission.enums.Permission;
import com.college.admission.enums.Role;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public final class SecuritySession {

    private final Long userId;

    private final String username;

    private final String displayName;

    private final Role role;

    private final Set<Permission> permissions;

    private final LocalDateTime loginTime;

    private boolean active;


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public SecuritySession(
            Long userId,
            String username,
            String displayName,
            Role role,
            Set<Permission> permissions
    ) {

        if (username == null
                || username.isBlank()) {

            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        if (role == null) {

            throw new IllegalArgumentException(
                    "Role cannot be null."
            );
        }

        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.role = role;

        this.permissions =
                permissions == null
                        ? EnumSet.noneOf(Permission.class)
                        : EnumSet.copyOf(permissions);

        this.loginTime =
                LocalDateTime.now();

        this.active = true;
    }


    // ============================================================
    // GETTERS
    // ============================================================

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Role getRole() {
        return role;
    }

    public Set<Permission> getPermissions() {

        return Collections.unmodifiableSet(
                permissions
        );
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public boolean isActive() {
        return active;
    }


    // ============================================================
    // PERMISSION CHECK
    // ============================================================

    public boolean hasPermission(
            Permission permission
    ) {

        return active
                && permission != null
                && permissions.contains(permission);
    }


    // ============================================================
    // ROLE CHECK
    // ============================================================

    public boolean hasRole(
            Role requiredRole
    ) {

        return active
                && requiredRole != null
                && role == requiredRole;
    }


    // ============================================================
    // LOGOUT
    // ============================================================

    public void invalidate() {

        active = false;
    }


    @Override
    public String toString() {

        return "SecuritySession{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", role=" + role +
                ", loginTime=" + loginTime +
                ", active=" + active +
                '}';
    }
}