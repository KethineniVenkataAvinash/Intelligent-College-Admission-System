package com.college.admission.jdbc;

import com.college.admission.util.DatabaseConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                DatabaseConfiguration.getUrl(),
                DatabaseConfiguration.getUsername(),
                DatabaseConfiguration.getPassword()
        );
    }
}