package com.thanhdat.servletmvc.services;

import java.util.Optional;

import com.thanhdat.servletmvc.models.OtpVerificationStatus;
import com.thanhdat.servletmvc.models.PasswordResetRequestResult;
import com.thanhdat.servletmvc.models.RegistrationResult;
import com.thanhdat.servletmvc.models.User;

public interface UserService {

    Optional<User> authenticate(
            String username,
            char[] rawPassword
    );

    RegistrationResult register(
            String username,
            String fullName,
            String email,
            char[] rawPassword,
            char[] confirmedPassword
    );

    boolean resendActivationOtp(
            int userId,
            String email
    );

    OtpVerificationStatus activateAccount(
            int userId,
            String rawOtp
    );

    PasswordResetRequestResult requestPasswordReset(
            String email
    );

    boolean resendPasswordResetOtp(
            int userId,
            String email
    );

    OtpVerificationStatus verifyPasswordResetOtp(
            int userId,
            String rawOtp
    );

    boolean resetPassword(
            int userId,
            char[] rawPassword,
            char[] confirmedPassword
    );
}
