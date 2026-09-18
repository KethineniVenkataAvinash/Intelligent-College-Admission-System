
package com.college.admission.model;

import com.college.admission.enums.Category;
import com.college.admission.enums.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student extends User {

    /*
     * ============================================================
     * STUDENT DETAILS
     * ============================================================
     */

    @Column(
            name = "application_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String applicationNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Column(name = "address")
    private String address;

    /*
     * DECIMAL(5,2) in MySQL
     *
     * Example:
     * 88.50
     * 95.25
     * 100.00
     */
    @Column(
            name = "percentage",
            precision = 5,
            scale = 2
    )
    private BigDecimal percentage;

    @Column(name = "rank_number")
    private Integer rank;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "authenticated")
    private boolean authenticated;

    /*
     * Application is currently handled by the
     * normal Java application layer.
     *
     * It is not stored in the students table.
     */

    @OneToOne(
        mappedBy = "student",
        fetch = FetchType.LAZY
    )
    private Application application;


    /*
     * ============================================================
     * DEFAULT CONSTRUCTOR
     * ============================================================
     *
     * REQUIRED BY JPA
     */

    public Student() {
        super();
    }


    /*
     * ============================================================
     * MAIN CONSTRUCTOR
     * ============================================================
     *
     * IMPORTANT:
     * userId should normally be NULL when creating
     * a new Student because the database generates it.
     */

    public Student(
            Long userId,
            String name,
            String email,
            String phone,
            String applicationNumber,
            LocalDate dateOfBirth,
            Gender gender,
            Category category,
            String address,
            BigDecimal percentage
    ) {

        super(
                userId,
                name,
                email,
                phone
        );

        this.applicationNumber = applicationNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.category = category;
        this.address = address;
        this.percentage = percentage;
    }


    /*
     * ============================================================
     * BACKWARD-COMPATIBLE CONSTRUCTOR
     * ============================================================
     *
     * This allows existing code using:
     *
     *     88.50
     *
     * to continue working.
     */

    public Student(
            Long userId,
            String name,
            String email,
            String phone,
            String applicationNumber,
            LocalDate dateOfBirth,
            Gender gender,
            Category category,
            String address,
            double percentage
    ) {

        this(
                userId,
                name,
                email,
                phone,
                applicationNumber,
                dateOfBirth,
                gender,
                category,
                address,
                BigDecimal.valueOf(percentage)
        );
    }


    /*
     * ============================================================
     * GETTERS
     * ============================================================
     */

    public Long getUserId() {
        return super.getUserId();
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public Category getCategory() {
        return category;
    }

    public String getAddress() {
        return address;
    }

    /*
     * Return BigDecimal directly.
     *
     * This preserves the exact DECIMAL(5,2) database value.
     */
    public BigDecimal getPercentage() {
        return percentage;
    }

    public Integer getRank() {
        return rank;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Application getApplication() {
        return application;
    }


    /*
     * ============================================================
     * SETTERS
     * ============================================================
     */

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    /*
     * Backward-compatible setter.
     */
    public void setPercentage(double percentage) {
        this.percentage = BigDecimal.valueOf(percentage);
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setApplication(Application application) {
        this.application = application;
    }


    /*
     * ============================================================
     * DISPLAY BASIC INFORMATION
     * ============================================================
     */

    @Override
    public void displayBasicInformation() {

        System.out.println();
        System.out.println("======================================");
        System.out.println("          STUDENT PROFILE");
        System.out.println("======================================");

        System.out.println(
                "User ID            : "
                        + getUserId()
        );

        System.out.println(
                "Name               : "
                        + getName()
        );

        System.out.println(
                "Email              : "
                        + getEmail()
        );

        System.out.println(
                "Phone              : "
                        + getPhoneNumber()
        );

        System.out.println(
                "Application Number : "
                        + applicationNumber
        );

        System.out.println(
                "Date of Birth      : "
                        + dateOfBirth
        );

        System.out.println(
                "Gender             : "
                        + gender
        );

        System.out.println(
                "Category           : "
                        + category
        );

        System.out.println(
                "Address            : "
                        + address
        );

        System.out.println(
                "12th Percentage    : "
                        + percentage
        );
    }


    /*
     * ============================================================
     * DISPLAY DASHBOARD
     * ============================================================
     */

    @Override
    public void displayDashboard() {

        System.out.println();
        System.out.println("======================================");
        System.out.println("        STUDENT DASHBOARD");
        System.out.println("======================================");

        System.out.println(
                "Application Number : "
                        + applicationNumber
        );

        System.out.println(
                "Student Name       : "
                        + getName()
        );

        System.out.println(
                "Category           : "
                        + category
        );

        System.out.println(
                "Rank               : "
                        + (
                        rank == null
                                ? "Not Generated"
                                : rank
                )
        );
    }
}
