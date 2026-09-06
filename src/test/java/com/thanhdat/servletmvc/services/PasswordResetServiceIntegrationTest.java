package com.thanhdat.servletmvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.thanhdat.servletmvc.config.DBConnection;
import com.thanhdat.servletmvc.daos.UserDao;
import com.thanhdat.servletmvc.daos.UserOtpDao;
import com.thanhdat.servletmvc.daos.impl.UserDaoImpl;
import com.thanhdat.servletmvc.daos.impl.UserOtpDaoImpl;
import com.thanhdat.servletmvc.models.OtpVerificationStatus;
import com.thanhdat.servletmvc.models.PasswordResetRequestResult;
import com.thanhdat.servletmvc.models.RegistrationResult;
import com.thanhdat.servletmvc.services.impl.OtpServiceImpl;
import com.thanhdat.servletmvc.services.impl.UserServiceImpl;

public class PasswordResetServiceIntegrationTest {

    private static final Pattern OTP_PATTERN =
            Pattern.compile("\\b([0-9]{6})\\b");

    @Test
    public void shouldResetPasswordWithoutLeakingUnknownEmail()
            throws SQLException {

        UserDao userDao = new UserDaoImpl();
        UserOtpDao userOtpDao = new UserOtpDaoImpl();
        CapturingMailService mailService =
                new CapturingMailService();

        UserService userService = new UserServiceImpl(
                userDao,
                new OtpServiceImpl(userOtpDao),
                mailService
        );

        String suffix = Long.toString(
                System.nanoTime(),
                36
        );

        String username = "reset_" + suffix;
        String email = "reset_" + suffix + "@local.test";

        char[] oldPassword =
                "OldPassword123!".toCharArray();
        char[] oldConfirmation =
                "OldPassword123!".toCharArray();
        char[] newPassword =
                "NewPassword456!".toCharArray();
        char[] newConfirmation =
                "NewPassword456!".toCharArray();

        int testUserId = 0;

        try {
            RegistrationResult registration =
                    userService.register(
                            username,
                            "Password Reset Integration Test",
                            email,
                            oldPassword,
                            oldConfirmation
                    );

            testUserId = registration.userId();

            assertEquals(
                    OtpVerificationStatus.VERIFIED,
                    userService.activateAccount(
                            testUserId,
                            mailService.latestOtp()
                    )
            );

            assertTrue(
                    userService.authenticate(
                            username,
                            oldPassword
                    ).isPresent()
            );

            int sentBeforeUnknownEmail =
                    mailService.sendCount;

            PasswordResetRequestResult unknownResult =
                    userService.requestPasswordReset(
                            "missing_" + suffix + "@local.test"
                    );

            assertEquals(-1, unknownResult.userId());
            assertEquals(
                    sentBeforeUnknownEmail,
                    mailService.sendCount
            );

            PasswordResetRequestResult resetRequest =
                    userService.requestPasswordReset(email);

            assertEquals(testUserId, resetRequest.userId());
            assertEquals(email, resetRequest.email());

            String rawOtp = mailService.latestOtp();
            String wrongOtp = rawOtp.equals("000000")
                    ? "999999"
                    : "000000";

            assertEquals(
                    OtpVerificationStatus.NOT_FOUND,
                    userService.activateAccount(
                            testUserId,
                            rawOtp
                    )
            );

            assertEquals(
                    OtpVerificationStatus.INVALID,
                    userService.verifyPasswordResetOtp(
                            testUserId,
                            wrongOtp
                    )
            );

            assertEquals(
                    OtpVerificationStatus.VERIFIED,
                    userService.verifyPasswordResetOtp(
                            testUserId,
                            rawOtp
                    )
            );

            assertTrue(
                    userService.resetPassword(
                            testUserId,
                            newPassword,
                            newConfirmation
                    )
            );

            assertTrue(
                    userService.authenticate(
                            username,
                            oldPassword
                    ).isEmpty()
            );

            assertTrue(
                    userService.authenticate(
                            username,
                            newPassword
                    ).isPresent()
            );

            assertEquals(
                    OtpVerificationStatus.NOT_FOUND,
                    userService.verifyPasswordResetOtp(
                            testUserId,
                            rawOtp
                    )
            );
        } finally {
            Arrays.fill(oldPassword, '\0');
            Arrays.fill(oldConfirmation, '\0');
            Arrays.fill(newPassword, '\0');
            Arrays.fill(newConfirmation, '\0');

            if (testUserId > 0) {
                deleteTestUser(testUserId);
            }
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

    private static final class CapturingMailService
            implements MailService {

        private int sendCount;
        private String content;

        @Override
        public void sendTextEmail(
                String recipient,
                String subject,
                String content
        ) {
            sendCount++;
            this.content = content;
        }

        private String latestOtp() {
            Matcher matcher = OTP_PATTERN.matcher(content);

            assertTrue(matcher.find());

            return matcher.group(1);
        }
    }
}
