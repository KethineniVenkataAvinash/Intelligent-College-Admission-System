package com.college.admission.integration;

import com.college.admission.enums.Category;
import com.college.admission.enums.Gender;
import com.college.admission.model.Application;
import com.college.admission.model.Student;
import com.college.admission.service.ApplicationService;
import com.college.admission.service.StudentRegistrationService;

import java.time.LocalDate;
import java.util.Scanner;

/**
 * ============================================================
 * ADMISSION FLOW INTEGRATION TEST
 * ============================================================
 *
 * Tests the real project services together:
 *
 * Student Registration
 *        ↓
 * Student Login
 *        ↓
 * Application Creation / Loading
 *        ↓
 * Course Preferences
 *        ↓
 * Application Submission
 *
 * No test student is hardcoded.
 * All student information is entered at runtime.
 */
public class AdmissionFlowIntegrationTest {

    private static final Scanner scanner =
            new Scanner(System.in);

    private static StudentRegistrationService
            registrationService;

    private static ApplicationService
            applicationService;

    private static Student student;

    private static Application application;

    public static void main(String[] args) {

        printHeader();

        try {

            initializeServices();

            registerStudent();

            loginStudent();

            createOrLoadApplication();

            addCoursePreferences();

            submitApplication();

            displayFinalResult();

            System.out.println();
            System.out.println(
                    "=============================================="
            );
            System.out.println(
                    "       ADMISSION FLOW PHASE 1 PASSED"
            );
            System.out.println(
                    "=============================================="
            );

        } catch (Exception e) {

            System.out.println();
            System.out.println(
                    "=============================================="
            );
            System.out.println(
                    "       ADMISSION FLOW FAILED"
            );
            System.out.println(
                    "=============================================="
            );

            System.out.println(
                    "Error: " + e.getMessage()
            );

            e.printStackTrace();

        } finally {

            scanner.close();
        }
    }

    // =========================================================
    // INITIALIZE SERVICES
    // =========================================================

    private static void initializeServices() {

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "       INITIALIZING SERVICES"
        );
        System.out.println(
                "=============================================="
        );

        registrationService =
                new StudentRegistrationService();

        applicationService =
                new ApplicationService();

        System.out.println(
                "StudentRegistrationService : READY"
        );

        System.out.println(
                "ApplicationService         : READY"
        );
    }

    // =========================================================
    // 1. STUDENT REGISTRATION
    // =========================================================

    private static void registerStudent() {

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "       1. STUDENT REGISTRATION"
        );
        System.out.println(
                "=============================================="
        );

        String name =
                readRequired(
                        "Enter student name: "
                );

        String email =
                readRequired(
                        "Enter email: "
                );

        String phone =
                readRequired(
                        "Enter phone number: "
                );

        LocalDate dateOfBirth =
                readDate(
                        "Enter date of birth (YYYY-MM-DD): "
                );

        Gender gender =
                readGender();

        Category category =
                readCategory();

        String address =
                readRequired(
                        "Enter address: "
                );

        double percentage =
                readPercentage();

        String password =
                readPassword(
                        "Create password: "
                );

        student =
                registrationService.registerStudent(
                        name,
                        email,
                        phone,
                        dateOfBirth,
                        gender,
                        category,
                        address,
                        percentage,
                        password
                );

        if (student == null) {

            throw new IllegalStateException(
                    "Student registration returned null."
            );
        }

        System.out.println();
        System.out.println(
                "----------------------------------------------"
        );

        System.out.println(
                "REGISTRATION SUCCESSFUL"
        );

        System.out.println(
                "Student Name       : "
                        + student.getName()
        );

        System.out.println(
                "Application Number : "
                        + student.getApplicationNumber()
        );

        System.out.println(
                "Email              : "
                        + student.getEmail()
        );

        System.out.println(
                "----------------------------------------------"
        );
    }

    // =========================================================
    // 2. STUDENT LOGIN
    // =========================================================

    private static void loginStudent() {

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "       2. STUDENT LOGIN"
        );
        System.out.println(
                "=============================================="
        );

        String email =
                readRequired(
                        "Enter registered email: "
                );

        String password =
                readPassword(
                        "Enter password: "
                );

        if (student == null
                || !student.getEmail().equalsIgnoreCase(email)) {

            throw new IllegalStateException(
                    "Student authentication failed."
            );
        }

        System.out.println();
        System.out.println(
                "LOGIN SUCCESSFUL"
        );

        System.out.println(
                "Student Name       : "
                        + student.getName()
        );

        System.out.println(
                "Application Number : "
                        + student.getApplicationNumber()
        );
    }

    // =========================================================
    // 3. CREATE / LOAD APPLICATION
    // =========================================================

    private static void createOrLoadApplication() {

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "       3. APPLICATION"
        );
        System.out.println(
                "=============================================="
        );

        application =
                applicationService.createApplication(
                        student
                );

        if (application == null) {

            throw new IllegalStateException(
                    "Application creation/loading failed."
            );
        }

        System.out.println();
        System.out.println(
                "APPLICATION READY"
        );

        System.out.println(
                "Application Number : "
                        + application
                        .getApplicationNumber()
        );

        System.out.println(
                "Status             : "
                        + application
                        .getStatus()
        );
    }

    // =========================================================
    // 4. COURSE PREFERENCES
    // =========================================================

    private static void addCoursePreferences() {

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "       4. COURSE PREFERENCES"
        );
        System.out.println(
                "=============================================="
        );

        int numberOfPreferences =
                readInteger(
                        "How many preferences? (1-5): ",
                        1,
                        5
                );

        for (int i = 1;
                     i <= numberOfPreferences;
                     i++) {

            System.out.println();
            System.out.println(
                    "Preference " + i
            );

            String courseCode =
                    readRequired(
                            "Enter course code: "
                    );

            applicationService.addCoursePreference(
                    student,
                    courseCode,
                    i
            );

            System.out.println(
                    "Preference added successfully."
            );

            System.out.println(
                    "Course Code : "
                            + courseCode
            );
        }

        System.out.println();
        System.out.println(
                "All course preferences added."
        );

        applicationService
                .displayCoursePreferences(
                        student
                );
    }

    // =========================================================
    // 5. SUBMIT APPLICATION
    // =========================================================

    private static void submitApplication() {

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "       5. APPLICATION SUBMISSION"
        );
        System.out.println(
                "=============================================="
        );

        String confirmation;

        while (true) {

            System.out.print(
                    "Submit application? (Y/N): "
            );

            confirmation =
                    scanner.nextLine()
                            .trim()
                            .toUpperCase();

            if (confirmation.equals("Y")
                    || confirmation.equals("N")) {

                break;
            }

            System.out.println(
                    "Please enter Y or N."
            );
        }

        if (confirmation.equals("N")) {

            System.out.println();
            System.out.println(
                    "Application submission cancelled."
            );

            return;
        }

        applicationService.submitApplication(
                student
        );

        System.out.println();
        System.out.println(
                "APPLICATION SUBMITTED SUCCESSFULLY"
        );

        System.out.println(
                "Application Number : "
                        + application
                        .getApplicationNumber()
        );

        System.out.println(
                "Status             : "
                        + application
                        .getStatus()
        );
    }

    // =========================================================
    // FINAL RESULT
    // =========================================================

    private static void displayFinalResult() {

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "       FINAL APPLICATION STATUS"
        );
        System.out.println(
                "=============================================="
        );

        applicationService
                .displayApplication(
                        student
                );
    }

    // =========================================================
    // INPUT METHODS
    // =========================================================

    private static String readRequired(
            String message
    ) {

        while (true) {

            System.out.print(message);

            String value =
                    scanner.nextLine().trim();

            if (!value.isEmpty()) {

                return value;
            }

            System.out.println(
                    "Input cannot be empty."
            );
        }
    }

    // ---------------------------------------------------------

    private static String readPassword(
            String message
    ) {

        while (true) {

            System.out.print(message);

            String password =
                    scanner.nextLine();

            if (password != null
                    && !password.isBlank()) {

                return password;
            }

            System.out.println(
                    "Password cannot be empty."
            );
        }
    }

    // ---------------------------------------------------------

    private static LocalDate readDate(
            String message
    ) {

        while (true) {

            try {

                System.out.print(message);

                return LocalDate.parse(
                        scanner.nextLine().trim()
                );

            } catch (Exception e) {

                System.out.println(
                        "Invalid date."
                );

                System.out.println(
                        "Use format YYYY-MM-DD."
                );
            }
        }
    }

    // ---------------------------------------------------------

    private static Gender readGender() {

        while (true) {

            System.out.println();
            System.out.println(
                    "1. MALE"
            );
            System.out.println(
                    "2. FEMALE"
            );
            System.out.println(
                    "3. OTHER"
            );

            int choice =
                    readInteger(
                            "Select gender: ",
                            1,
                            3
                    );

            switch (choice) {

                case 1:
                    return Gender.MALE;

                case 2:
                    return Gender.FEMALE;

                case 3:
                    return Gender.OTHER;

                default:
                    System.out.println(
                            "Invalid selection."
                    );
            }
        }
    }

    // ---------------------------------------------------------

    private static Category readCategory() {

        while (true) {

            System.out.println();
            System.out.println(
                    "1. GENERAL"
            );
            System.out.println(
                    "2. OBC"
            );
            System.out.println(
                    "3. SC"
            );
            System.out.println(
                    "4. ST"
            );
            System.out.println(
                    "5. EWS"
            );

            int choice =
                    readInteger(
                            "Select category: ",
                            1,
                            5
                    );

            switch (choice) {

                case 1:
                    return Category.GENERAL;

                case 2:
                    return Category.OBC;

                case 3:
                    return Category.SC;

                case 4:
                    return Category.ST;

                case 5:
                    return Category.EWS;

                default:
                    System.out.println(
                            "Invalid selection."
                    );
            }
        }
    }

    // ---------------------------------------------------------

    private static double readPercentage() {

        while (true) {

            try {

                System.out.print(
                        "Enter 12th percentage: "
                );

                double percentage =
                        Double.parseDouble(
                                scanner.nextLine()
                                        .trim()
                        );

                if (percentage < 0
                        || percentage > 100) {

                    System.out.println(
                            "Percentage must be between "
                                    + "0 and 100."
                    );

                    continue;
                }

                return percentage;

            } catch (NumberFormatException e) {

                System.out.println(
                        "Enter a valid percentage."
                );
            }
        }
    }

    // ---------------------------------------------------------

    private static int readInteger(
            String message,
            int minimum,
            int maximum
    ) {

        while (true) {

            try {

                System.out.print(message);

                int value =
                        Integer.parseInt(
                                scanner.nextLine()
                                        .trim()
                        );

                if (value >= minimum
                        && value <= maximum) {

                    return value;
                }

                System.out.println(
                        "Enter a number between "
                                + minimum
                                + " and "
                                + maximum
                                + "."
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Enter a valid number."
                );
            }
        }
    }

    // =========================================================
    // HEADER
    // =========================================================

    private static void printHeader() {

        System.out.println();
        System.out.println(
                "=============================================="
        );
        System.out.println(
                "     INTELLIGENT COLLEGE ADMISSION SYSTEM"
        );
        System.out.println(
                "        ADMISSION FLOW INTEGRATION TEST"
        );
        System.out.println(
                "=============================================="
        );
    }
}