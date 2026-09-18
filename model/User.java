package com.college.admission.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;


@MappedSuperclass
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phoneNumber;

    protected User() {
    }

    protected User(
            Long userId,
            String name,
            String email,
            String phone
    ) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phone;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }

    public abstract void displayDashboard();

    public void displayBasicInformation() {

        System.out.println();
        System.out.println("======================================");
        System.out.println("          USER INFORMATION");
        System.out.println("======================================");

        System.out.println("User ID : " + userId);
        System.out.println("Name    : " + name);
        System.out.println("Email   : " + email);
        System.out.println("Phone   : " + phoneNumber);
    }
}