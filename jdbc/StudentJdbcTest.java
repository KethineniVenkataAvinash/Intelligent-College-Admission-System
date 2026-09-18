package com.college.admission.jdbc;

import com.college.admission.enums.Category;
import com.college.admission.enums.Gender;
import com.college.admission.model.Student;
import com.college.admission.util.PasswordUtil;

import java.time.LocalDate;

public class StudentJdbcTest {

    public static void main(String[] args) {

        StudentRepository repository =
                new StudentRepository();

        /*
         * ========================================================
         * CREATE TEST STUDENT
         * ========================================================
         */

        String applicationNumber =
                "JDBCTEST" + System.currentTimeMillis();

        Student student =
                new Student(
                        0L,
                        "JDBC Test Student",
                        applicationNumber.toLowerCase()
                                + "@example.com",
                        "9876543210",
                        applicationNumber,
                        LocalDate.of(2006, 8, 15),
                        Gender.MALE,
                        Category.GENERAL,
                        "College Campus",
                        85.50
                );

        student.setPasswordHash(
                PasswordUtil.hashPassword(
                        "test123"
                )
        );

        /*
         * ========================================================
         * INSERT
         * ========================================================
         */

        System.out.println();

        System.out.println(
                "======================================"
        );

        System.out.println(
                "          JDBC INSERT TEST"
        );

        System.out.println(
                "======================================"
        );

        boolean saved =
                repository.save(student);

        if (saved) {

            System.out.println(
                    "Student inserted successfully."
            );

            System.out.println(
                    "Application Number : "
                            + applicationNumber
            );

        } else {

            System.out.println(
                    "Student insertion failed."
            );

            return;
        }

        /*
         * ========================================================
         * SELECT
         * ========================================================
         */

        System.out.println();

        System.out.println(
                "======================================"
        );

        System.out.println(
                "          JDBC SELECT TEST"
        );

        System.out.println(
                "======================================"
        );

        Student retrievedStudent =
                repository.findByApplicationNumber(
                        applicationNumber
                );

        if (retrievedStudent == null) {

            System.out.println(
                    "Student could not be retrieved."
            );

            return;
        }

        System.out.println(
                "Student retrieved successfully."
        );

        System.out.println();

        System.out.println(
                "Application Number : "
                        + retrievedStudent.getApplicationNumber()
        );

        System.out.println(
                "Name               : "
                        + retrievedStudent.getName()
        );

        System.out.println(
                "Email              : "
                        + retrievedStudent.getEmail()
        );

        System.out.println(
                "Phone              : "
                        + retrievedStudent.getPhoneNumber()
        );

        System.out.println(
                "Date of Birth      : "
                        + retrievedStudent.getDateOfBirth()
        );

        System.out.println(
                "Gender             : "
                        + retrievedStudent.getGender()
        );

        System.out.println(
                "Category           : "
                        + retrievedStudent.getCategory()
        );

        System.out.println(
                "Address            : "
                        + retrievedStudent.getAddress()
        );

        System.out.println(
                "Percentage         : "
                        + retrievedStudent.getPercentage()
        );

        /*
         * ========================================================
         * VERIFY PASSWORD HASH
         * ========================================================
         */

        boolean passwordMatches =
                        PasswordUtil.verifyPassword(
                        "test123",
                        retrievedStudent.getPasswordHash()
                );

        System.out.println(
                "Password Hash Test : "
                        + (passwordMatches
                        ? "PASSED"
                        : "FAILED")
        );

       // ==========================================
// UPDATE
// ==========================================

System.out.println();
System.out.println("JPA UPDATE TEST");

boolean updated =
        repository.updatePercentage(
                applicationNumber,
                95.75
        );

System.out.println(
        "Update Status : "
                + (updated
                ? "SUCCESS"
                : "FAILED")
);

Student updatedStudent =
        repository.findByApplicationNumber(
                applicationNumber
        );

if (updatedStudent != null) {

    System.out.println(
            "Updated Percentage : "
                    + updatedStudent.getPercentage()
    );
}


// ==========================================
// DELETE
// ==========================================

System.out.println();
System.out.println("JPA DELETE TEST");

boolean deleted =
        repository.deleteByApplicationNumber(
                applicationNumber
        );

System.out.println(
        "Delete Status : "
                + (deleted
                ? "SUCCESS"
                : "FAILED")
);

Student deletedStudent =
        repository.findByApplicationNumber(
                applicationNumber
        );

System.out.println(
        "Student After Delete : "
                + (deletedStudent == null
                ? "NOT FOUND"
                : "STILL EXISTS")
);
        /*
         * ========================================================
         * FINAL RESULT
         * ========================================================
         */

        System.out.println();

        System.out.println(
                "======================================"
        );

        System.out.println(
                "          JDBC CRUD TEST DONE"
        );

        System.out.println(
                "======================================"
        );
    }
}