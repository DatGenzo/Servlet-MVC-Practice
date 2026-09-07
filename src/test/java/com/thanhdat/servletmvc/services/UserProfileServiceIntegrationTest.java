package com.thanhdat.servletmvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.Test;

import com.thanhdat.servletmvc.config.DBConnection;
import com.thanhdat.servletmvc.daos.UserDao;
import com.thanhdat.servletmvc.daos.impl.UserDaoImpl;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.services.impl.UserProfileServiceImpl;

public class UserProfileServiceIntegrationTest {

    @Test
    public void shouldPersistProfileThroughJpa() throws SQLException {
        UserDao userDao = new UserDaoImpl();
        UserProfileService profileService =
                new UserProfileServiceImpl();

        String suffix = Long.toString(System.nanoTime());
        String username = "profile_" + suffix;
        int userId = 0;

        try {
            User testUser = new User();
            testUser.setUsername(username);
            testUser.setPasswordHash("integration-test-hash");
            testUser.setFullName("Profile Before");
            testUser.setEmail(username + "@local.test");

            userId = userDao.insertInactive(testUser);
            assertTrue(userId > 0);

            User updated = profileService.updateProfile(
                    userId,
                    "  Profile   After  ",
                    "+84 901.234.567",
                    "profiles/" + userId + "/avatar.png"
            );

            assertEquals("Profile After", updated.getFullName());
            assertEquals("+84901234567", updated.getPhone());
            assertEquals(
                    "profiles/" + userId + "/avatar.png",
                    updated.getImage()
            );
            assertNull(updated.getPasswordHash());

            User jdbcReloaded = userDao.findByUsername(username)
                    .orElseThrow();

            assertEquals("Profile After", jdbcReloaded.getFullName());
            assertEquals("+84901234567", jdbcReloaded.getPhone());
            assertEquals(
                    "profiles/" + userId + "/avatar.png",
                    jdbcReloaded.getImage()
            );
        } finally {
            deleteTestUser(userId);
        }
    }

    private void deleteTestUser(int userId) throws SQLException {
        if (userId <= 0) {
            return;
        }

        try (
                Connection connection =
                        DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(
                                "DELETE FROM users WHERE user_id = ?"
                        )
        ) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }
}
