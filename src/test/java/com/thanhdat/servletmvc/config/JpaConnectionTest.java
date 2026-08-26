package com.thanhdat.servletmvc.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Test;

import jakarta.persistence.EntityManager;

public class JpaConnectionTest {

    @Test
    public void shouldConnectToMysqlThroughJpa() {
        EntityManager entityManager =
            JpaConfig.getEntityManager();

        try {
            assertTrue(entityManager.isOpen());

            Number result = (Number) entityManager
                .createNativeQuery("SELECT 1")
                .getSingleResult();

            assertEquals(1, result.intValue());

        } finally {
            entityManager.close();
        }
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
        JpaConfig.close();
    }
}