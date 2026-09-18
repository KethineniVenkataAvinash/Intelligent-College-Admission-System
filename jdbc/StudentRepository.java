package com.college.admission.jdbc;

import com.college.admission.enums.Category;
import com.college.admission.enums.Gender;
import com.college.admission.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StudentRepository {

    /*
     * ============================================================
     * INSERT STUDENT
     * ============================================================
     */

    public boolean save(Student student) {

        if (student == null) {
            throw new IllegalArgumentException(
                    "Student cannot be null."
            );
        }

        String sql =
                "INSERT INTO students " +
                "(application_number, name, email, phone, " +
                "date_of_birth, gender, category, address, " +
                "percentage, rank_number, password_hash, authenticated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    student.getApplicationNumber()
            );

            statement.setString(
                    2,
                    student.getName()
            );

            statement.setString(
                    3,
                    student.getEmail()
            );

            statement.setString(
                    4,
                    student.getPhoneNumber()
            );

            /*
             * Student now stores DOB as LocalDate.
             *
             * MySQL stores DOB as DATE.
             */
            if (student.getDateOfBirth() != null) {

                statement.setDate(
                        5,
                        java.sql.Date.valueOf(
                                student.getDateOfBirth()
                        )
                );

            } else {

                statement.setNull(
                        5,
                        java.sql.Types.DATE
                );
            }

            statement.setString(
                    6,
                    student.getGender() == null
                            ? null
                            : student.getGender().name()
            );

            statement.setString(
                    7,
                    student.getCategory() == null
                            ? null
                            : student.getCategory().name()
            );

            statement.setString(
                    8,
                    student.getAddress()
            );

            statement.setBigDecimal(
                    9,
                    student.getPercentage()
            );

            if (student.getRank() == null) {

                statement.setNull(
                        10,
                        java.sql.Types.INTEGER
                );

            } else {

                statement.setInt(
                        10,
                        student.getRank()
                );
            }

            statement.setString(
                    11,
                    student.getPasswordHash()
            );

            statement.setBoolean(
                    12,
                    student.isAuthenticated()
            );

            int rowsInserted =
                    statement.executeUpdate();

            return rowsInserted > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to save student: "
                            + e.getMessage(),
                    e
            );
        }
    }


    /*
     * ============================================================
     * FIND STUDENT BY APPLICATION NUMBER
     * ============================================================
     */

    public Student findByApplicationNumber(
            String applicationNumber
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            return null;
        }

        String sql =
                "SELECT * FROM students " +
                "WHERE application_number = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    applicationNumber
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return mapStudent(
                            resultSet
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find student: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }


    /*
     * ============================================================
     * FIND STUDENT BY EMAIL
     * ============================================================
     */

    public Student findByEmail(
            String email
    ) {

        if (email == null
                || email.isBlank()) {

            return null;
        }

        String sql =
                "SELECT * FROM students " +
                "WHERE email = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    email
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    return mapStudent(
                            resultSet
                    );
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to find student by email: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }


    /*
     * ============================================================
     * MAP RESULTSET TO STUDENT
     * ============================================================
     */

    private Student mapStudent(
            ResultSet resultSet
    ) throws SQLException {

        Long studentId =
                resultSet.getLong(
                        "student_id"
                );

        String applicationNumber =
                resultSet.getString(
                        "application_number"
                );

        String name =
                resultSet.getString(
                        "name"
                );

        String email =
                resultSet.getString(
                        "email"
                );

        String phone =
                resultSet.getString(
                        "phone"
                );

        /*
         * MySQL DATE → Java LocalDate
         */
        java.sql.Date sqlDate =
                resultSet.getDate(
                        "date_of_birth"
                );

        LocalDate dateOfBirth = null;

        if (sqlDate != null) {

            dateOfBirth =
                    sqlDate.toLocalDate();
        }


        /*
         * Gender
         */
        String genderValue =
                resultSet.getString(
                        "gender"
                );

        Gender gender = null;

        if (genderValue != null
                && !genderValue.isBlank()) {

            gender =
                    Gender.valueOf(
                            genderValue
                    );
        }


        /*
         * Category
         */
        String categoryValue =
                resultSet.getString(
                        "category"
                );

        Category category = null;

        if (categoryValue != null
                && !categoryValue.isBlank()) {

            category =
                    Category.valueOf(
                            categoryValue
                    );
        }


        /*
         * Other fields
         */
        String address =
                resultSet.getString(
                        "address"
                );

        double percentage =
                resultSet.getDouble(
                        "percentage"
                );


        /*
         * Rank
         */
        int rankValue =
                resultSet.getInt(
                        "rank_number"
                );

        Integer rank = null;

        if (!resultSet.wasNull()) {

            rank = rankValue;
        }


        /*
         * Password
         */
        String passwordHash =
                resultSet.getString(
                        "password_hash"
                );


        /*
         * Authentication status
         */
        boolean authenticated =
                resultSet.getBoolean(
                        "authenticated"
                );


        /*
         * Create Student using the CURRENT
         * Student constructor.
         */
        Student student =
                new Student(
                        studentId,
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

        student.setRank(rank);

        student.setPasswordHash(
                passwordHash
        );

        student.setAuthenticated(
                authenticated
        );

        return student;
    }


    /*
     * ============================================================
     * UPDATE PERCENTAGE
     * ============================================================
     */

    public boolean updatePercentage(
            String applicationNumber,
            double percentage
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        if (percentage < 0
                || percentage > 100) {

            throw new IllegalArgumentException(
                    "Percentage must be between 0 and 100."
            );
        }

        String sql =
                "UPDATE students " +
                "SET percentage = ? " +
                "WHERE application_number = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setDouble(
                    1,
                    percentage
            );

            statement.setString(
                    2,
                    applicationNumber
            );

            int rowsUpdated =
                    statement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to update student: "
                            + e.getMessage(),
                    e
            );
        }
    }


    /*
     * ============================================================
     * DELETE STUDENT
     * ============================================================
     */

    public boolean deleteByApplicationNumber(
            String applicationNumber
    ) {

        if (applicationNumber == null
                || applicationNumber.isBlank()) {

            throw new IllegalArgumentException(
                    "Application number cannot be empty."
            );
        }

        String sql =
                "DELETE FROM students " +
                "WHERE application_number = ?";

        try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    applicationNumber
            );

            int rowsDeleted =
                    statement.executeUpdate();

            return rowsDeleted > 0;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to delete student: "
                            + e.getMessage(),
                    e
            );
        }
    }
}