package com.thanhdat.servletmvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.thanhdat.servletmvc.config.DBConnection;
import com.thanhdat.servletmvc.daos.UserDao;
import com.thanhdat.servletmvc.daos.UserOtpDao;
import com.thanhdat.servletmvc.daos.impl.UserDaoImpl;
import com.thanhdat.servletmvc.daos.impl.UserOtpDaoImpl;
import com.thanhdat.servletmvc.models.OtpVerificationStatus;
import com.thanhdat.servletmvc.models.RegistrationResult;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.services.impl.OtpServiceImpl;
import com.thanhdat.servletmvc.services.impl.UserServiceImpl;

public class RegistrationServiceIntegrationTest {

    private static final Pattern OTP_PATTERN =
            Pattern.compile("\\b([0-9]{6})\\b");

    @Test
    public void shouldRegisterActivateAndAuthenticateUser()
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

        String username = "register_" + suffix;
        String email = "register_"
                + suffix
                + "@local.test";

        char[] password =
                "TestPassword123!".toCharArray();

        char[] confirmation =
                "TestPassword123!".toCharArray();

        int testUserId = 0;

        try {
            RegistrationResult result =
                    userService.register(
                            username,
                            "Registration Integration Test",
                            email,
                            password,
                            confirmation
                    );

            testUserId = result.userId();

            assertTrue(testUserId > 0);
            assertEquals(email, result.email());
            assertTrue(result.emailSent());

            Optional<User> createdOptional =
                    userDao.findByEmail(email);

            assertTrue(createdOptional.isPresent());

            User created = createdOptional.get();

            assertFalse(created.isActive());
            assertEquals("USER", created.getRole());
            assertFalse(
                    created.getPasswordHash().equals(
                            new String(password)
                    )
            );

            assertEquals(email, mailService.recipient);
            assertNotNull(mailService.content);

            Matcher matcher = OTP_PATTERN.matcher(
                    mailService.content
            );

            assertTrue(matcher.find());

            String rawOtp = matcher.group(1);

            assertTrue(
                    userService.authenticate(
                            username,
                            password
                    ).isEmpty()
            );

            String wrongOtp = rawOtp.equals("000000")
                    ? "999999"
                    : "000000";

            assertEquals(
                    OtpVerificationStatus.INVALID,
                    userService.activateAccount(
                            testUserId,
                            wrongOtp
                    )
            );

            assertEquals(
                    OtpVerificationStatus.VERIFIED,
                    userService.activateAccount(
                            testUserId,
                            rawOtp
                    )
            );

            Optional<User> authenticated =
                    userService.authenticate(
                            username,
                            password
                    );

            assertTrue(authenticated.isPresent());
            assertTrue(authenticated.get().isActive());

            assertEquals(
                    OtpVerificationStatus.NOT_FOUND,
                    userService.activateAccount(
                            testUserId,
                            rawOtp
                    )
            );
        } finally {
            Arrays.fill(password, '\0');
            Arrays.fill(confirmation, '\0');

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

        private String recipient;
        private String content;

        @Override
        public void sendTextEmail(
                String recipient,
                String subject,
                String content
        ) {
            this.recipient = recipient;
            this.content = content;
        }
    }
}
