package com.thanhdat.servletmvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;

import com.thanhdat.servletmvc.config.DBConnection;
import com.thanhdat.servletmvc.daos.UserDao;
import com.thanhdat.servletmvc.daos.UserOtpDao;
import com.thanhdat.servletmvc.daos.impl.UserDaoImpl;
import com.thanhdat.servletmvc.daos.impl.UserOtpDaoImpl;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.OtpPurpose;
import com.thanhdat.servletmvc.models.OtpVerificationStatus;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.models.UserOtp;
import com.thanhdat.servletmvc.services.impl.OtpServiceImpl;
import com.thanhdat.servletmvc.utils.PasswordUtils;

public class OtpServiceIntegrationTest {

    private static final int MAX_ATTEMPTS = 5;

    @Test
    public void shouldEnforceCompleteOtpLifecycle()
            throws SQLException {

        UserDao userDao = new UserDaoImpl();
        UserOtpDao userOtpDao = new UserOtpDaoImpl();
        OtpService otpService =
                new OtpServiceImpl(userOtpDao);

        int testUserId = 0;

        try {
            testUserId = userDao.insertInactive(
                    createTestUser()
            );

            String activationOtp = otpService.issueOtp(
                    testUserId,
                    OtpPurpose.ACCOUNT_ACTIVATION
            );

            assertTrue(
                    activationOtp.matches("[0-9]{6}")
            );

            UserOtp storedActivationOtp =
                    userOtpDao.findLatestActive(
                            testUserId,
                            OtpPurpose.ACCOUNT_ACTIVATION
                    ).orElseThrow();

            assertFalse(
                    activationOtp.equals(
                            storedActivationOtp.getOtpHash()
                    )
            );

            assertTrue(
                    matches(
                            activationOtp,
                            storedActivationOtp.getOtpHash()
                    )
            );

            boolean cooldownRejected = false;

            try {
                otpService.issueOtp(
                        testUserId,
                        OtpPurpose.ACCOUNT_ACTIVATION
                );
            } catch (ValidationException exception) {
                cooldownRejected = true;
            }

            assertTrue(cooldownRejected);

            String wrongOtp = activationOtp.equals("000000")
                    ? "999999"
                    : "000000";

            assertEquals(
                    OtpVerificationStatus.INVALID,
                    otpService.verifyOtp(
                            testUserId,
                            OtpPurpose.ACCOUNT_ACTIVATION,
                            wrongOtp
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

            assertEquals(
                    OtpVerificationStatus.VERIFIED,
                    otpService.verifyOtp(
                            testUserId,
                            OtpPurpose.ACCOUNT_ACTIVATION,
                            activationOtp
                    )
            );

            assertEquals(
                    OtpVerificationStatus.NOT_FOUND,
                    otpService.verifyOtp(
                            testUserId,
                            OtpPurpose.ACCOUNT_ACTIVATION,
                            activationOtp
                    )
            );

            String expiredOtp = "333333";

            userOtpDao.replaceActiveOtp(
                    testUserId,
                    OtpPurpose.PASSWORD_RESET,
                    hash(expiredOtp),
                    LocalDateTime.now().minusSeconds(1)
            );

            assertEquals(
                    OtpVerificationStatus.EXPIRED,
                    otpService.verifyOtp(
                            testUserId,
                            OtpPurpose.PASSWORD_RESET,
                            expiredOtp
                    )
            );

            String limitedOtp = "444444";

            userOtpDao.replaceActiveOtp(
                    testUserId,
                    OtpPurpose.PASSWORD_RESET,
                    hash(limitedOtp),
                    LocalDateTime.now().plusMinutes(5)
            );

            String wrongLimitedOtp = "555555";

            for (int attempt = 1;
                    attempt < MAX_ATTEMPTS;
                    attempt++) {

                assertEquals(
                        OtpVerificationStatus.INVALID,
                        otpService.verifyOtp(
                                testUserId,
                                OtpPurpose.PASSWORD_RESET,
                                wrongLimitedOtp
                        )
                );
            }

            assertEquals(
                    OtpVerificationStatus.ATTEMPTS_EXCEEDED,
                    otpService.verifyOtp(
                            testUserId,
                            OtpPurpose.PASSWORD_RESET,
                            wrongLimitedOtp
                    )
            );

            assertEquals(
                    OtpVerificationStatus.ATTEMPTS_EXCEEDED,
                    otpService.verifyOtp(
                            testUserId,
                            OtpPurpose.PASSWORD_RESET,
                            limitedOtp
                    )
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

        user.setUsername("otp_service_" + suffix);
        user.setPasswordHash(
                hash("TestPassword123!")
        );
        user.setFullName("OTP Service Integration Test");
        user.setEmail(
                "otp_service_"
                        + suffix
                        + "@local.test"
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

    private boolean matches(
            String rawValue,
            String hash
    ) {
        char[] characters = rawValue.toCharArray();

        try {
            return PasswordUtils.matches(
                    characters,
                    hash
            );
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