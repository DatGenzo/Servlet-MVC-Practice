package com.thanhdat.servletmvc.utils;

import java.security.SecureRandom;
import java.util.Locale;

public final class OtpUtils {

    public static final int OTP_LENGTH = 6;

    private static final int OTP_BOUND = 1_000_000;

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private OtpUtils() {
        // Không cho phép tạo đối tượng OtpUtils.
    }

    public static String generateNumericOtp() {
        int value = SECURE_RANDOM.nextInt(OTP_BOUND);

        return String.format(
                Locale.ROOT,
                "%0" + OTP_LENGTH + "d",
                value
        );
    }
}