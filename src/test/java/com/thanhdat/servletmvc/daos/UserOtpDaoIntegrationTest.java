package com.thanhdat.servletmvc.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import com.thanhdat.servletmvc.config.DBConnection;
import com.thanhdat.servletmvc.daos.impl.UserDaoImpl;
import com.thanhdat.servletmvc.daos.impl.UserOtpDaoImpl;
import com.thanhdat.servletmvc.models.OtpPurpose;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.models.UserOtp;
import com.thanhdat.servletmvc.utils.PasswordUtils;

public class UserOtpDaoIntegrationTest {

    private static final int MAX_ATTEMPTS = 5;

    @Test
    public void shouldReplaceIncrementAndConsumeOtp()
            throws SQLException {

        UserDao userDao = new UserDaoImpl();
        UserOtpDao userOtpDao = new UserOtpDaoImpl();

        int testUserId = 0;

        try {
            User testUser = createTestUser();

            testUserId = userDao.insertInactive(testUser);

            assertTrue(testUserId > 0);

            long firstOtpId = userOtpDao.replaceActiveOtp(
                    testUserId,
                    OtpPurpose.ACCOUNT_ACTIVATION,
                    hash("111111"),
                    LocalDateTime.now().plusMinutes(5)
            );

            assertTrue(firstOtpId > 0);

            long secondOtpId = userOtpDao.replaceActiveOtp(
                    testUserId,
                    OtpPurpose.ACCOUNT_ACTIVATION,
                    hash("222222"),
                    LocalDateTime.now().plusMinutes(5)
            );

            assertTrue(secondOtpId > 0);
            assertTrue(firstOtpId != secondOtpId);

            Optional<UserOtp> latestOptional =
                    userOtpDao.findLatestActive(
                            testUserId,
                            OtpPurpose.ACCOUNT_ACTIVATION
                    );

            assertTrue(latestOptional.isPresent());

            UserOtp latest = latestOptional.get();

            assertEquals(secondOtpId, latest.getId());
            assertEquals(0, latest.getAttemptCount());
            assertFalse(latest.isConsumed());

            assertTrue(
                    userOtpDao.incrementAttempt(
                            secondOtpId,
                            MAX_ATTEMPTS
                    )
            );

            UserOtp afterWrongAttempt =
                    userOtpDao.findLatestActive(
                            testUserId,
                            OtpPurpose.ACCOUNT_ACTIVATION
                    ).orElseThrow();

            assertEquals(
                    1,
                    afterWrongAttempt.getAttemptCount()
            );

            assertTrue(
                    userOtpDao.consumeIfUsable(
                            secondOtpId,
                            MAX_ATTEMPTS
                    )
            );

            assertFalse(
                    userOtpDao.consumeIfUsable(
                            secondOtpId,
                            MAX_ATTEMPTS
                    )
            );

            assertTrue(
                    userOtpDao.findLatestActive(
                            testUserId,
                            OtpPurpose.ACCOUNT_ACTIVATION
                    ).isEmpty()
            );
        } finally {
            if (testUserId > 0) {
                deleteTestUser(testUserId);
            }
        }
    }

    private User createTestUser() {
        String suffix = Long.toString(
                System.nanoTime(),
                36
        );

        User user = new User();

        user.setUsername("otp_test_" + suffix);
        user.setPasswordHash(hash("TestPassword123!"));
        user.setFullName("OTP Integration Test");
        user.setEmail(
                "otp_test_" + suffix + "@local.test"
        );

        return user;
    }

    private String hash(String rawValue) {
        char[] characters = rawValue.toCharArray();

        try {
            return PasswordUtils.hash(characters);
        } finally {
            Arrays.fill(characters, '\0');
        }
    }

    private void deleteTestUser(int userId)
            throws SQLException {

        String sql = """
                DELETE FROM users
                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }
}