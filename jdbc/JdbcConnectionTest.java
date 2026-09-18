package com.college.admission.jdbc;

import java.sql.Connection;

public class JdbcConnectionTest {

    public static void main(String[] args) {

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            System.out.println();
            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "       JDBC CONNECTION SUCCESS"
            );

            System.out.println(
                    "======================================"
            );

            System.out.println(
                    "Database : college_admission"
            );

            System.out.println(
                    "Status   : Connected"
            );

            System.out.println(
                    "======================================"
            );

        } catch (Exception e) {

            System.out.println();

            System.out.println(
                    "JDBC connection failed."
            );

            System.out.println(
                    "Reason: "
                            + e.getMessage()
            );
        }
    }
}