package com.college.admission.util;

public final class DatabaseConfiguration {

    public static final String URL_PROPERTY = "DB_URL";
    public static final String USERNAME_PROPERTY = "DB_USERNAME";
    public static final String PASSWORD_PROPERTY = "DB_PASSWORD";
    public static final String DDL_AUTO_PROPERTY =
            "HIBERNATE_DDL_AUTO";

    private DatabaseConfiguration() {
    }

    public static String getUrl() {
        return requireEnvironmentVariable(URL_PROPERTY);
    }

    public static String getUsername() {
        return requireEnvironmentVariable(USERNAME_PROPERTY);
    }

    public static String getPassword() {
        return requireEnvironmentVariable(PASSWORD_PROPERTY);
    }

    public static String getDdlAuto() {
        String configuredValue =
                System.getenv(DDL_AUTO_PROPERTY);

        if (configuredValue == null
                || configuredValue.isBlank()) {
            return "update";
        }

        String ddlAuto =
                configuredValue.trim().toLowerCase();

        if (!ddlAuto.equals("validate")
                && !ddlAuto.equals("update")
                && !ddlAuto.equals("none")) {
            throw new IllegalArgumentException(
                    "HIBERNATE_DDL_AUTO must be one of: "
                            + "validate, update, none."
            );
        }

        return ddlAuto;
    }

    /**
     * Makes environment-backed values available to the placeholders in
     * persistence.xml before the persistence provider is initialized.
     */
    public static void configureJpaProperties() {
        System.setProperty(URL_PROPERTY, getUrl());
        System.setProperty(USERNAME_PROPERTY, getUsername());
        System.setProperty(PASSWORD_PROPERTY, getPassword());
        System.setProperty(
                DDL_AUTO_PROPERTY,
                getDdlAuto()
        );
    }

    private static String requireEnvironmentVariable(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Required database environment variable is missing: "
                            + name
            );
        }

        return value.trim();
    }
}
