package com.thanhdat.servletmvc.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

public final class ApplicationConfig {

    private static final Properties PROPERTIES =
            loadProperties();

    private ApplicationConfig() {
    }

    public static Path getUploadDirectory() {
        String configuredPath = getRequiredConfig(
                "UPLOAD_DIR",
                "app.upload.dir"
        );

        return Path.of(configuredPath)
                .toAbsolutePath()
                .normalize();
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream =
                ApplicationConfig.class
                        .getClassLoader()
                        .getResourceAsStream(
                                "application.properties"
                        )) {

            if (inputStream != null) {
                properties.load(inputStream);
            }

            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Không thể đọc application.properties.",
                    exception
            );
        }
    }

    private static String getRequiredConfig(
            String environmentVariable,
            String propertyName
    ) {
        String environmentValue =
                System.getenv(environmentVariable);

        if (environmentValue != null
                && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }

        String propertyValue =
                PROPERTIES.getProperty(propertyName);

        if (propertyValue == null
                || propertyValue.isBlank()) {
            throw new IllegalStateException(
                    "Thiếu cấu hình: " + propertyName
            );
        }

        return propertyValue.trim();
    }
}