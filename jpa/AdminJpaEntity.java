package com.college.admission.jpa;

import com.college.admission.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "admin_accounts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_admin_employee_id",
                        columnNames = "employee_id"
                ),
                @UniqueConstraint(
                        name = "uk_admin_email",
                        columnNames = "email"
                )
        }
)
public class AdminJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(
            name = "employee_id",
            nullable = false,
            unique = true
    )
    private String employeeId;

    @Column(
            name = "name",
            nullable = false
    )
    private String name;

    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    private String email;

    @Column(
            name = "phone",
            nullable = false
    )
    private String phoneNumber;

    @Column(
            name = "password_hash",
            nullable = false
    )
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false
    )
    private Role role;

    @Column(
            name = "failed_login_attempts",
            nullable = false
    )
    private int failedLoginAttempts;

    @Column(
            name = "account_locked",
            nullable = false
    )
    private boolean accountLocked;

    @Column(
            name = "last_login"
    )
    private LocalDateTime lastLogin;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;


    // ============================================================
    // JPA CONSTRUCTOR
    // ============================================================

    protected AdminJpaEntity() {
    }


    // ============================================================
    // MAIN CONSTRUCTOR
    // ============================================================

    public AdminJpaEntity(
            String employeeId,
            String name,
            String email,
            String phoneNumber,
            String passwordHash
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
                    "Admin phone number cannot be empty."
            );
        }

        if (passwordHash == null
                || passwordHash.isBlank()) {

            throw new IllegalArgumentException(
                    "Password hash cannot be empty."
            );
        }

        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;

        this.role = Role.ADMIN;

        this.failedLoginAttempts = 0;
        this.accountLocked = false;

        this.createdAt =
                LocalDateTime.now();
    }


    // ============================================================
    // GETTERS
    // ============================================================

    public Long getAdminId() {
        return adminId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    // ============================================================
    // SETTERS
    // ============================================================

    public void setPasswordHash(
            String passwordHash
    ) {

        if (passwordHash == null
                || passwordHash.isBlank()) {

            throw new IllegalArgumentException(
                    "Password hash cannot be empty."
            );
        }

        this.passwordHash = passwordHash;
    }

    public void setFailedLoginAttempts(
            int failedLoginAttempts
    ) {

        if (failedLoginAttempts < 0) {

            throw new IllegalArgumentException(
                    "Failed login attempts cannot be negative."
            );
        }

        this.failedLoginAttempts =
                failedLoginAttempts;
    }

    public void setAccountLocked(
            boolean accountLocked
    ) {

        this.accountLocked =
                accountLocked;
    }

    public void setLastLogin(
            LocalDateTime lastLogin
    ) {

        this.lastLogin =
                lastLogin;
    }


    // ============================================================
    // LOGIN FAILURE
    // ============================================================

    public void registerFailedLogin() {

        failedLoginAttempts++;

        /*
         * Project security rule:
         * lock after 5 failed attempts.
         */
        if (failedLoginAttempts >= 5) {

            accountLocked = true;
        }
    }


    // ============================================================
    // SUCCESSFUL LOGIN
    // ============================================================

    public void registerSuccessfulLogin() {

        failedLoginAttempts = 0;

        accountLocked = false;

        lastLogin =
                LocalDateTime.now();
    }


    // ============================================================
    // DISPLAY
    // ============================================================

    public void display() {

        System.out.println();
        System.out.println(
                "======================================"
        );
        System.out.println(
                "          ADMIN ACCOUNT"
        );
        System.out.println(
                "======================================"
        );

        System.out.println(
                "Admin ID       : " + adminId
        );

        System.out.println(
                "Employee ID    : " + employeeId
        );

        System.out.println(
                "Name           : " + name
        );

        System.out.println(
                "Email          : " + email
        );

        System.out.println(
                "Phone          : " + phoneNumber
        );

        System.out.println(
                "Role           : " + role
        );

        System.out.println(
                "Failed Attempts: "
                        + failedLoginAttempts
        );

        System.out.println(
                "Account Locked : "
                        + accountLocked
        );

        System.out.println(
                "Last Login     : "
                        + lastLogin
        );
    }
}