package com.thanhdat.servletmvc.services.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import com.thanhdat.servletmvc.daos.UserOtpDao;
import com.thanhdat.servletmvc.daos.impl.UserOtpDaoImpl;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.OtpPurpose;
import com.thanhdat.servletmvc.models.OtpVerificationStatus;
import com.thanhdat.servletmvc.models.UserOtp;
import com.thanhdat.servletmvc.services.OtpService;
import com.thanhdat.servletmvc.utils.OtpUtils;
import com.thanhdat.servletmvc.utils.PasswordUtils;

public class OtpServiceImpl implements OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;

    private final UserOtpDao userOtpDao;

    public OtpServiceImpl() {
        this(new UserOtpDaoImpl());
    }

    public OtpServiceImpl(UserOtpDao userOtpDao) {
        this.userOtpDao = userOtpDao;
    }

    @Override
    public String issueOtp(
            int userId,
            OtpPurpose purpose
    ) {
        validateIdentity(userId, purpose);

        LocalDateTime now = LocalDateTime.now();

        enforceResendCooldown(
                userId,
                purpose,
                now
        );

        String rawOtp = OtpUtils.generateNumericOtp();
        char[] otpCharacters = rawOtp.toCharArray();

        try {
            String otpHash =
                    PasswordUtils.hash(otpCharacters);

            userOtpDao.replaceActiveOtp(
                    userId,
                    purpose,
                    otpHash,
                    now.plusMinutes(OTP_EXPIRY_MINUTES)
            );

            return rawOtp;
        } finally {
            Arrays.fill(otpCharacters, '\0');
        }
    }

    @Override
    public OtpVerificationStatus verifyOtp(
            int userId,
            OtpPurpose purpose,
            String rawOtp
    ) {
        validateIdentity(userId, purpose);

        if (rawOtp == null
                || !rawOtp.trim().matches("[0-9]{6}")) {
            return OtpVerificationStatus.INVALID;
        }

        Optional<UserOtp> otpOptional =
                userOtpDao.findLatestActive(
                        userId,
                        purpose
                );

        if (otpOptional.isEmpty()) {
            return OtpVerificationStatus.NOT_FOUND;
        }

        UserOtp storedOtp = otpOptional.get();
        LocalDateTime now = LocalDateTime.now();

        if (storedOtp.getExpiresAt() == null
                || !storedOtp.getExpiresAt().isAfter(now)) {
            return OtpVerificationStatus.EXPIRED;
        }

        if (storedOtp.getAttemptCount()
                >= MAX_ATTEMPTS) {
            return OtpVerificationStatus
                    .ATTEMPTS_EXCEEDED;
        }

        char[] otpCharacters =
                rawOtp.trim().toCharArray();

        boolean matches;

        try {
            matches = PasswordUtils.matches(
                    otpCharacters,
                    storedOtp.getOtpHash()
            );
        } finally {
            Arrays.fill(otpCharacters, '\0');
        }

        if (!matches) {
            boolean incremented =
                    userOtpDao.incrementAttempt(
                            storedOtp.getId(),
                            MAX_ATTEMPTS
                    );

            if (!incremented) {
                if (!storedOtp.getExpiresAt().isAfter(
                        LocalDateTime.now()
                )) {
                    return OtpVerificationStatus.EXPIRED;
                }

                return OtpVerificationStatus
                        .ATTEMPTS_EXCEEDED;
            }

            if (storedOtp.getAttemptCount() + 1
                    >= MAX_ATTEMPTS) {
                return OtpVerificationStatus
                        .ATTEMPTS_EXCEEDED;
            }

            return OtpVerificationStatus.INVALID;
        }

        boolean consumed =
                userOtpDao.consumeIfUsable(
                        storedOtp.getId(),
                        MAX_ATTEMPTS
                );

        if (consumed) {
            return OtpVerificationStatus.VERIFIED;
        }

        if (!storedOtp.getExpiresAt().isAfter(
                LocalDateTime.now()
        )) {
            return OtpVerificationStatus.EXPIRED;
        }

        return OtpVerificationStatus.NOT_FOUND;
    }

    private void enforceResendCooldown(
            int userId,
            OtpPurpose purpose,
            LocalDateTime now
    ) {
        Optional<UserOtp> activeOtpOptional =
                userOtpDao.findLatestActive(
                        userId,
                        purpose
                );

        if (activeOtpOptional.isEmpty()) {
            return;
        }

        LocalDateTime createdAt =
                activeOtpOptional.get().getCreatedAt();

        if (createdAt == null) {
            return;
        }

        LocalDateTime allowedAt =
                createdAt.plusSeconds(
                        RESEND_COOLDOWN_SECONDS
                );

        if (!now.isBefore(allowedAt)) {
            return;
        }

        long remainingMilliseconds =
                Duration.between(
                        now,
                        allowedAt
                ).toMillis();

        long remainingSeconds = Math.max(
                1,
                (remainingMilliseconds + 999) / 1_000
        );

        throw new ValidationException(
                "Vui lòng đợi "
                        + remainingSeconds
                        + " giây trước khi gửi lại OTP."
        );
    }

    private void validateIdentity(
            int userId,
            OtpPurpose purpose
    ) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "userId phải lớn hơn 0."
            );
        }

        if (purpose == null) {
            throw new IllegalArgumentException(
                    "Mục đích OTP không được để trống."
            );
        }
    }
}