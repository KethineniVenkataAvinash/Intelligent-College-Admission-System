package com.college.admission.service;

import com.college.admission.enums.Category;
import com.college.admission.enums.Gender;
import com.college.admission.exception.ValidationException;
import com.college.admission.jpa.StudentJpaRepository;
import com.college.admission.model.Application;
import com.college.admission.model.Student;
import com.college.admission.util.PasswordUtil;

import java.time.LocalDate;
import java.util.UUID;

public class StudentRegistrationService {

    private final StudentJpaRepository studentRepository;

    public StudentRegistrationService() {
        this.studentRepository = new StudentJpaRepository();
    }

    public Student registerStudent(
            String name,
            String email,
            String phone,
            LocalDate dateOfBirth,
            Gender gender,
            Category category,
            String address,
            double percentage,
            String password
    ) {

        validateInput(
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

        /*
         * Check duplicate email.
         */
        if (studentRepository.findByEmail(email) != null) {
            throw new ValidationException(
                    "Email is already registered."
            );
        }

        /*
         * Generate unique application number.
         */
        String applicationNumber =
                generateApplicationNumber();

        /*
         * Create Student.
         */
        Student student = new Student(
                null,
                name,
                email,
                phone,
                applicationNumber,
                dateOfBirth,
                gender,
                category,
                address,
                percentage
        );

        /*
         * Hash password before storing it.
         */
        student.setPasswordHash(
                PasswordUtil.hashPassword(password)
        );

        /*
         * Create initial Application.
         *
         * New students start with:
         * Application Status = DRAFT
         * Counselling Status = NOT_STARTED
         */
        Application application =
                new Application(
                        applicationNumber,
                        student
                );

        /*
         * Connect both sides of the relationship.
         */
        student.setApplication(application);

        /*
         * Save Student + Application
         * inside ONE database transaction.
         */
        studentRepository.saveStudentWithApplication(
                student,
                application
        );

        System.out.println();
        System.out.println(
                "Student and Application saved successfully to MySQL."
        );

        return student;
    }

    private String generateApplicationNumber() {

        String applicationNumber;

        do {

            String uniquePart =
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 10)
                            .toUpperCase();

            applicationNumber =
                    "APP" + uniquePart;

        } while (
                studentRepository.findByApplicationNumber(
                        applicationNumber
                ) != null
        );

        return applicationNumber;
    }

    private void validateInput(
            String name,
            String email,
            String phone,
            LocalDate dateOfBirth,
            Gender gender,
            Category category,
            String address,
            double percentage,
            String password
    ) {

        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Name cannot be empty."
            );
        }

        if (email == null || email.isBlank()) {
            throw new ValidationException(
                    "Email cannot be empty."
            );
        }

        if (phone == null || phone.isBlank()) {
            throw new ValidationException(
                    "Phone number cannot be empty."
            );
        }

        if (dateOfBirth == null) {
            throw new ValidationException(
                    "Date of birth is required."
            );
        }

        if (gender == null) {
            throw new ValidationException(
                    "Gender is required."
            );
        }

        if (category == null) {
            throw new ValidationException(
                    "Category is required."
            );
        }

        if (address == null || address.isBlank()) {
            throw new ValidationException(
                    "Address cannot be empty."
            );
        }

        if (percentage < 0 || percentage > 100) {
            throw new ValidationException(
                    "Percentage must be between 0 and 100."
            );
        }

        if (password == null || password.isBlank()) {
            throw new ValidationException(
                    "Password cannot be empty."
            );
        }
    }
}