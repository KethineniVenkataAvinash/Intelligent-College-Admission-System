package com.college.admission.model;

public class Admin extends User {
    private String employeeId;
    public Admin(
        Long userID,
        String name,
        String email,
        String phoneNumber,
        String employeeId
    )
    {
        super(userID, name, email, phoneNumber);
        this.employeeId = employeeId;
    }
    public String getEmployeeId() {
        return employeeId;
    }

    @Override
    public void displayDashboard() {
        super.displayBasicInformation();
        System.out.println("Admin Dashboard");
        System.out.println("Employee ID: " + employeeId);
        System.out.println("Name: " + getName());
    }
}
    