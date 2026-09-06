package com.thanhdat.servletmvc.daos;

import java.time.LocalDateTime;
import java.util.Optional;

import com.thanhdat.servletmvc.models.OtpPurpose;
import com.thanhdat.servletmvc.models.UserOtp;

public interface UserOtpDao {

    long replaceActiveOtp(
            int userId,
            OtpPurpose purpose,
            String otpHash,
            LocalDateTime expiresAt
    );

    Optional<UserOtp> findLatestActive(
            int userId,
            OtpPurpose purpose
    );

    boolean incrementAttempt(
            long otpId,
            int maxAttempts
    );

    boolean consumeIfUsable(
            long otpId,
            int maxAttempts
    );
}