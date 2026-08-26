package com.thanhdat.servletmvc.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaConfig {

    private static final String PERSISTENCE_UNIT_NAME =
        "jpa-hibernate-mysql";

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
        createEntityManagerFactory();

    private JpaConfig() {
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public static void close() {
        if (ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        Properties databaseProperties = loadDatabaseProperties();

        Map<String, Object> jpaProperties = new HashMap<>();

        jpaProperties.put(
            "jakarta.persistence.jdbc.url",
            getRequiredValue(
                databaseProperties,
                "DB_URL",
                "db.url"
            )
        );

        jpaProperties.put(
            "jakarta.persistence.jdbc.user",
            getRequiredValue(
                databaseProperties,
                "DB_USERNAME",
                "db.username"
            )
        );

        jpaProperties.put(
            "jakarta.persistence.jdbc.password",
            getRequiredValue(
                databaseProperties,
                "DB_PASSWORD",
                "db.password"
            )
        );

        jpaProperties.put(
            "jakarta.persistence.jdbc.driver",
            getValueOrDefault(
                databaseProperties,
                "DB_DRIVER",
                "db.driver",
                "com.mysql.cj.jdbc.Driver"
            )
        );

        return Persistence.createEntityManagerFactory(
            PERSISTENCE_UNIT_NAME,
            jpaProperties
        );
    }

    private static Properties loadDatabaseProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream =
                 JpaConfig.class
                     .getClassLoader()
                     .getResourceAsStream("database.properties")) {

            if (inputStream != null) {
                properties.load(inputStream);
            }

            return properties;

        } catch (IOException exception) {
            throw new IllegalStateException(
                "Không thể đọc database.properties.",
                exception
            );
        }
    }

    private static String getRequiredValue(
            Properties properties,
            String environmentName,
            String propertyName) {

        String value = System.getenv(environmentName);

        if (value == null || value.isBlank()) {
            value = properties.getProperty(propertyName);
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Thiếu cấu hình database: "
                    + environmentName
                    + " hoặc "
                    + propertyName
            );
        }

        return value;
    }

    private static String getValueOrDefault(
            Properties properties,
            String environmentName,
            String propertyName,
            String defaultValue) {

        String value = System.getenv(environmentName);

        if (value == null || value.isBlank()) {
            value = properties.getProperty(propertyName);
        }

        return value == null || value.isBlank()
            ? defaultValue
            : value;
    }
}