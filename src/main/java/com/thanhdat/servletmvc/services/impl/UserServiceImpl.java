package com.thanhdat.servletmvc.services.impl;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

import com.thanhdat.servletmvc.daos.UserDao;
import com.thanhdat.servletmvc.daos.impl.UserDaoImpl;
import com.thanhdat.servletmvc.exceptions.DuplicateResourceException;
import com.thanhdat.servletmvc.exceptions.MailDeliveryException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.OtpPurpose;
import com.thanhdat.servletmvc.models.OtpVerificationStatus;
import com.thanhdat.servletmvc.models.PasswordResetRequestResult;
import com.thanhdat.servletmvc.models.RegistrationResult;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.services.MailService;
import com.thanhdat.servletmvc.services.OtpService;
import com.thanhdat.servletmvc.services.UserService;
import com.thanhdat.servletmvc.utils.PasswordUtils;

public class UserServiceImpl implements UserService {

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("[A-Za-z0-9._-]{3,50}");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 72;

    private final UserDao userDao;
    private final OtpService otpService;
    private final MailService mailService;

    public UserServiceImpl() {
        this(
                new UserDaoImpl(),
                new OtpServiceImpl(),
                new SmtpMailService()
        );
    }

    public UserServiceImpl(UserDao userDao) {
        this(
                userDao,
                new OtpServiceImpl(),
                new SmtpMailService()
        );
    }

    public UserServiceImpl(
            UserDao userDao,
            OtpService otpService,
            MailService mailService
    ) {
        this.userDao = userDao;
        this.otpService = otpService;
        this.mailService = mailService;
    }

    @Override
    public Optional<User> authenticate(
            String username,
            char[] rawPassword
    ) {
        if (username == null
                || username.isBlank()
                || rawPassword == null
                || rawPassword.length == 0
                || rawPassword.length > MAX_PASSWORD_LENGTH) {
            return Optional.empty();
        }

        String normalizedUsername = username.trim();

        if (!USERNAME_PATTERN.matcher(
                normalizedUsername
        ).matches()) {
            return Optional.empty();
        }

        Optional<User> userOptional =
                userDao.findByUsername(normalizedUsername);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        if (!user.isActive()) {
            return Optional.empty();
        }

        boolean passwordMatches = PasswordUtils.matches(
                rawPassword,
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            return Optional.empty();
        }

        /*
         * Controller và Session không cần giữ passwordHash.
         * Xóa thông tin này trước khi trả User ra ngoài.
         */
        user.setPasswordHash(null);

        return Optional.of(user);
    }

    @Override
    public RegistrationResult register(
            String username,
            String fullName,
            String email,
            char[] rawPassword,
            char[] confirmedPassword
    ) {
        String normalizedUsername =
                normalizeUsername(username);

        String normalizedFullName =
                normalizeFullName(fullName);

        String normalizedEmail =
                normalizeEmail(email);

        validatePasswords(
                rawPassword,
                confirmedPassword
        );

        if (userDao.existsByUsername(normalizedUsername)) {
            throw new DuplicateResourceException(
                    "Tên đăng nhập đã được sử dụng."
            );
        }

        if (userDao.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException(
                    "Email đã được sử dụng."
            );
        }

        User user = new User();

        user.setUsername(normalizedUsername);
        user.setFullName(normalizedFullName);
        user.setEmail(normalizedEmail);
        user.setPasswordHash(
                PasswordUtils.hash(rawPassword)
        );

        int userId = userDao.insertInactive(user);

        String rawOtp = otpService.issueOtp(
                userId,
                OtpPurpose.ACCOUNT_ACTIVATION
        );

        boolean emailSent = true;

        try {
            sendActivationEmail(
                    normalizedEmail,
                    rawOtp
            );
        } catch (MailDeliveryException exception) {
            emailSent = false;
        }

        return new RegistrationResult(
                userId,
                normalizedEmail,
                emailSent
        );
    }

    @Override
    public boolean resendActivationOtp(
            int userId,
            String email
    ) {
        if (userId <= 0) {
            throw new ValidationException(
                    "Phiên kích hoạt không hợp lệ."
            );
        }

        String normalizedEmail = normalizeEmail(email);

        String rawOtp = otpService.issueOtp(
                userId,
                OtpPurpose.ACCOUNT_ACTIVATION
        );

        sendActivationEmail(
                normalizedEmail,
                rawOtp
        );

        return true;
    }

    @Override
    public OtpVerificationStatus activateAccount(
            int userId,
            String rawOtp
    ) {
        if (userId <= 0) {
            return OtpVerificationStatus.NOT_FOUND;
        }

        OtpVerificationStatus status =
                otpService.verifyOtp(
                        userId,
                        OtpPurpose.ACCOUNT_ACTIVATION,
                        rawOtp
                );

        if (status != OtpVerificationStatus.VERIFIED) {
            return status;
        }

        if (!userDao.activateById(userId)) {
            return OtpVerificationStatus.NOT_FOUND;
        }

        return OtpVerificationStatus.VERIFIED;
    }

    @Override
    public PasswordResetRequestResult requestPasswordReset(
            String email
    ) {
        String normalizedEmail = normalizeEmail(email);

        Optional<User> userOptional =
                userDao.findByEmail(normalizedEmail);

        if (userOptional.isEmpty()
                || !userOptional.get().isActive()) {
            return new PasswordResetRequestResult(
                    -1,
                    normalizedEmail
            );
        }

        User user = userOptional.get();

        try {
            String rawOtp = otpService.issueOtp(
                    user.getId(),
                    OtpPurpose.PASSWORD_RESET
            );

            sendPasswordResetEmail(
                    normalizedEmail,
                    rawOtp
            );
        } catch (
                MailDeliveryException
                | ValidationException exception
        ) {
            /*
             * Không để phản hồi khác nhau làm lộ email
             * có tồn tại trong hệ thống hay không.
             */
        }

        return new PasswordResetRequestResult(
                user.getId(),
                normalizedEmail
        );
    }

    @Override
    public boolean resendPasswordResetOtp(
            int userId,
            String email
    ) {
        if (userId <= 0) {
            return true;
        }

        String normalizedEmail = normalizeEmail(email);

        try {
            String rawOtp = otpService.issueOtp(
                    userId,
                    OtpPurpose.PASSWORD_RESET
            );

            sendPasswordResetEmail(
                    normalizedEmail,
                    rawOtp
            );

            return true;
        } catch (
                MailDeliveryException
                | ValidationException exception
        ) {
            return false;
        }
    }

    @Override
    public OtpVerificationStatus verifyPasswordResetOtp(
            int userId,
            String rawOtp
    ) {
        if (userId <= 0) {
            return OtpVerificationStatus.NOT_FOUND;
        }

        return otpService.verifyOtp(
                userId,
                OtpPurpose.PASSWORD_RESET,
                rawOtp
        );
    }

    @Override
    public boolean resetPassword(
            int userId,
            char[] rawPassword,
            char[] confirmedPassword
    ) {
        if (userId <= 0) {
            throw new ValidationException(
                    "Phiên đặt lại mật khẩu không hợp lệ."
            );
        }

        validatePasswords(
                rawPassword,
                confirmedPassword
        );

        String passwordHash =
                PasswordUtils.hash(rawPassword);

        return userDao.updatePasswordHash(
                userId,
                passwordHash
        );
    }

    private String normalizeUsername(String username) {
        if (username == null) {
            throw new ValidationException(
                    "Tên đăng nhập không được để trống."
            );
        }

        String normalized = username.trim();

        if (!USERNAME_PATTERN.matcher(normalized).matches()) {
            throw new ValidationException(
                    "Tên đăng nhập phải dài 3-50 ký tự và chỉ gồm chữ, số, dấu chấm, gạch dưới hoặc gạch ngang."
            );
        }

        return normalized;
    }

    private String normalizeFullName(String fullName) {
        if (fullName == null) {
            throw new ValidationException(
                    "Họ tên không được để trống."
            );
        }

        String normalized = fullName.trim();

        if (normalized.length() < 2
                || normalized.length() > 100) {
            throw new ValidationException(
                    "Họ tên phải dài từ 2 đến 100 ký tự."
            );
        }

        return normalized;
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            throw new ValidationException(
                    "Email không được để trống."
            );
        }

        String normalized = email
                .trim()
                .toLowerCase(Locale.ROOT);

        if (normalized.length() > 100
                || !EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new ValidationException(
                    "Email không đúng định dạng."
            );
        }

        return normalized;
    }

    private void validatePasswords(
            char[] rawPassword,
            char[] confirmedPassword
    ) {
        if (rawPassword == null
                || rawPassword.length < MIN_PASSWORD_LENGTH
                || rawPassword.length > MAX_PASSWORD_LENGTH) {
            throw new ValidationException(
                    "Mật khẩu phải dài từ 8 đến 72 ký tự."
            );
        }

        if (confirmedPassword == null
                || !Arrays.equals(
                        rawPassword,
                        confirmedPassword
                )) {
            throw new ValidationException(
                    "Xác nhận mật khẩu không khớp."
            );
        }
    }

    private void sendActivationEmail(
            String recipient,
            String rawOtp
    ) {
        String subject =
                "Servlet MVC Practice - Kích hoạt tài khoản";

        String content = """
                Mã OTP kích hoạt tài khoản của bạn là: %s

                Mã có hiệu lực trong 5 phút.
                Không cung cấp mã này cho người khác.
                """.formatted(rawOtp);

        mailService.sendTextEmail(
                recipient,
                subject,
                content
        );
    }

    private void sendPasswordResetEmail(
            String recipient,
            String rawOtp
    ) {
        String subject =
                "Servlet MVC Practice - Đặt lại mật khẩu";

        String content = """
                Mã OTP đặt lại mật khẩu của bạn là: %s

                Mã có hiệu lực trong 5 phút.
                Không cung cấp mã này cho người khác.
                """.formatted(rawOtp);

        mailService.sendTextEmail(
                recipient,
                subject,
                content
        );
    }
}
