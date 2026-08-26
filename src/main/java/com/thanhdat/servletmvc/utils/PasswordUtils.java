package com.thanhdat.servletmvc.utils;

import java.util.Arrays;
import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordUtils {
    private static final int BCRYPT_COST = 12;
    private PasswordUtils() {
        // Không cho phép tạo đối tượng PasswordUtils.
    }
    
    public static String hash(char[] rawPassword) {
        validatePassword(rawPassword);

        char[] passwordCopy = Arrays.copyOf(
                rawPassword,
                rawPassword.length
        );

        try {
            return BCrypt.withDefaults()
                    .hashToString(BCRYPT_COST, passwordCopy);
        } finally {
            Arrays.fill(passwordCopy, '\0');
        }
    }

    public static boolean matches(
            char[] rawPassword,
            String passwordHash
    ) {
        if (rawPassword == null
                || rawPassword.length == 0
                || passwordHash == null
                || passwordHash.isBlank()) {
            return false;
        }

        char[] passwordCopy = Arrays.copyOf(
                rawPassword,
                rawPassword.length
        );

        try {
            BCrypt.Result result = BCrypt.verifyer()
                    .verify(passwordCopy, passwordHash);

            return result.verified;
        } finally {
            Arrays.fill(passwordCopy, '\0');
        }
    }

    private static void validatePassword(char[] rawPassword) {
        if (rawPassword == null || rawPassword.length == 0) {
            throw new IllegalArgumentException(
                    "Mật khẩu không được để trống."
            );
        }
    }
}