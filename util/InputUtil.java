package com.college.admission.util;

import com.college.admission.enums.Category;
import com.college.admission.enums.Gender;
import com.college.admission.exception.ValidationException;

import java.util.Scanner;

public final class InputUtil {

    private InputUtil() {
        // Private constructor to prevent instantiation
    }
    public static String readRequiredString(
        Scanner scanner, 
        String message
    ) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }
    public static String readEmail(
        Scanner scanner
    ){
        while (true) {
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return email;
            }
            System.out.println("Invalid email format. Please try again.");
            
        }
    }
    public static String readPhoneNumber(
        Scanner scanner
    ) {
        while (true) {
            System.out.print("Enter phone number: ");
            String phoneNumber = scanner.nextLine().trim();
            if (phoneNumber.matches("\\d{10}")) {
                return phoneNumber;
            }
            System.out.println("Invalid phone number. Please enter a 10-digit number.");
        }
    }
    public static String readDateOfBirth(
        Scanner scanner
    ) {
        while (true) {
            System.out.print("Enter date of birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine().trim();
            if (dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return dob;
            }
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
        }
    }
    public static double readPercentage(
        Scanner scanner
    ) {
        while (true) {
            System.out.print("Enter 12th percentage: ");
            String input = scanner.nextLine().trim();
            try {
                double percentage = Double.parseDouble(input);
                if (percentage >= 0 && percentage <= 100) {
                    return percentage;
                } else {
                    System.out.println("Percentage must be between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }
    public static Gender readGender(
        Scanner scanner
    ) {
        while (true) {
            System.out.println();
            System.out.println("Select Gender:");
            System.out.println("1. Male");
            System.out.println("2. Female");
            System.out.println("3. Other");
            System.out.print("Enter your choice (1-3): ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    return Gender.MALE;
                case "2":
                    return Gender.FEMALE;
                case "3":
                    return Gender.OTHER;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 3.");
            }
        }
    }

    public static Category readCategory(
        Scanner scanner
    ) {
        while (true) {
            System.out.println();
            System.out.println("Select Category:");
            System.out.println("1. General");
            System.out.println("2. OBC");
            System.out.println("3. SC");
            System.out.println("4. ST");
            System.out.println("5. EWS");
            System.out.print("Enter your choice (1-5): ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    return Category.GENERAL;
                case "2":
                    return Category.OBC;
                case "3":
                    return Category.SC;
                case "4":
                    return Category.ST;
                case "5":
                    return Category.EWS;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }
}