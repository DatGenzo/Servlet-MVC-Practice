package com.thanhdat.servletmvc.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {
    private static final Properties PROPERTIES = loadProperties();

    private DBConnection() {
        // Không cho phép tạo đối tượng DBConnection.
    }

    public static Connection getConnection() throws SQLException {
        String driver = getRequiredConfig("DB_DRIVER", "db.driver");
        String url = getRequiredConfig("DB_URL", "db.url");
        String username = getRequiredConfig("DB_USERNAME", "db.username");
        String password = getRequiredConfig("DB_PASSWORD", "db.password");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException exception) {
            throw new SQLException(
                    "Không tìm thấy MySQL JDBC Driver: " + driver,
                    exception
            );
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = DBConnection.class
                .getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (inputStream != null) {
                properties.load(inputStream);
            }

            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Không thể đọc file database.properties.",
                    exception
            );
        }
    }

    private static String getRequiredConfig(
            String environmentVariable,
            String propertyName
    ) {
        String environmentValue = System.getenv(environmentVariable);

        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }

        String propertyValue = PROPERTIES.getProperty(propertyName);

        if (propertyValue == null || propertyValue.isBlank()) {
            throw new IllegalStateException(
                    "Thiếu cấu hình database: " + propertyName
            );
        }

        return propertyValue.trim();
    }
}
