package com.thanhdat.servletmvc.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class MailConfig {

    private static final int DEFAULT_TIMEOUT_MILLIS = 10_000;

    private static final Properties PROPERTIES =
            loadProperties();

    private MailConfig() {
    }

    public static Properties getSmtpProperties() {
        Properties smtpProperties = new Properties();

        smtpProperties.setProperty(
                "mail.smtp.host",
                getRequiredConfig(
                        "MAIL_SMTP_HOST",
                        "mail.smtp.host"
                )
        );

        smtpProperties.setProperty(
                "mail.smtp.port",
                Integer.toString(
                        getRequiredPositiveInteger(
                                "MAIL_SMTP_PORT",
                                "mail.smtp.port"
                        )
                )
        );

        smtpProperties.setProperty(
                "mail.smtp.auth",
                Boolean.toString(
                        getRequiredBoolean(
                                "MAIL_SMTP_AUTH",
                                "mail.smtp.auth"
                        )
                )
        );

        smtpProperties.setProperty(
                "mail.smtp.starttls.enable",
                Boolean.toString(
                        getRequiredBoolean(
                                "MAIL_SMTP_STARTTLS_ENABLE",
                                "mail.smtp.starttls.enable"
                        )
                )
        );

        smtpProperties.setProperty(
                "mail.smtp.connectiontimeout",
                Integer.toString(
                        getPositiveInteger(
                                "MAIL_SMTP_CONNECTION_TIMEOUT",
                                "mail.smtp.connectiontimeout",
                                DEFAULT_TIMEOUT_MILLIS
                        )
                )
        );

        smtpProperties.setProperty(
                "mail.smtp.timeout",
                Integer.toString(
                        getPositiveInteger(
                                "MAIL_SMTP_TIMEOUT",
                                "mail.smtp.timeout",
                                DEFAULT_TIMEOUT_MILLIS
                        )
                )
        );

        smtpProperties.setProperty(
                "mail.smtp.writetimeout",
                Integer.toString(
                        getPositiveInteger(
                                "MAIL_SMTP_WRITE_TIMEOUT",
                                "mail.smtp.writetimeout",
                                DEFAULT_TIMEOUT_MILLIS
                        )
                )
        );

        return smtpProperties;
    }

    public static String getUsername() {
        return getRequiredConfig(
                "MAIL_USERNAME",
                "mail.username"
        );
    }

    public static String getPassword() {
        return getRequiredConfig(
                "MAIL_PASSWORD",
                "mail.password"
        );
    }

    public static String getFromAddress() {
        return getRequiredConfig(
                "MAIL_FROM_ADDRESS",
                "mail.from.address"
        );
    }

    public static String getFromName() {
        String configuredValue = getConfig(
                "MAIL_FROM_NAME",
                "mail.from.name"
        );

        if (configuredValue == null) {
            return "Servlet MVC Practice";
        }

        return configuredValue;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream =
                MailConfig.class
                        .getClassLoader()
                        .getResourceAsStream(
                                "mail.properties"
                        )) {

            if (inputStream != null) {
                properties.load(inputStream);
            }

            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Không thể đọc file mail.properties.",
                    exception
            );
        }
    }

    private static String getRequiredConfig(
            String environmentVariable,
            String propertyName
    ) {
        String configuredValue = getConfig(
                environmentVariable,
                propertyName
        );

        if (configuredValue == null) {
            throw new IllegalStateException(
                    "Thiếu cấu hình mail: " + propertyName
            );
        }

        return configuredValue;
    }

    private static String getConfig(
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
            return null;
        }

        return propertyValue.trim();
    }

    private static int getRequiredPositiveInteger(
            String environmentVariable,
            String propertyName
    ) {
        String configuredValue = getRequiredConfig(
                environmentVariable,
                propertyName
        );

        return parsePositiveInteger(
                configuredValue,
                propertyName
        );
    }

    private static int getPositiveInteger(
            String environmentVariable,
            String propertyName,
            int defaultValue
    ) {
        String configuredValue = getConfig(
                environmentVariable,
                propertyName
        );

        if (configuredValue == null) {
            return defaultValue;
        }

        return parsePositiveInteger(
                configuredValue,
                propertyName
        );
    }

    private static int parsePositiveInteger(
            String configuredValue,
            String propertyName
    ) {
        try {
            int parsedValue =
                    Integer.parseInt(configuredValue);

            if (parsedValue <= 0) {
                throw new IllegalStateException(
                        "Cấu hình mail phải lớn hơn 0: "
                                + propertyName
                );
            }

            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Cấu hình mail không phải số hợp lệ: "
                            + propertyName,
                    exception
            );
        }
    }

    private static boolean getRequiredBoolean(
            String environmentVariable,
            String propertyName
    ) {
        String configuredValue = getRequiredConfig(
                environmentVariable,
                propertyName
        );

        if ("true".equalsIgnoreCase(configuredValue)) {
            return true;
        }

        if ("false".equalsIgnoreCase(configuredValue)) {
            return false;
        }

        throw new IllegalStateException(
                "Cấu hình mail phải là true hoặc false: "
                        + propertyName
        );
    }
}