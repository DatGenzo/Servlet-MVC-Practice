package com.thanhdat.servletmvc.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OtpUtilsTest {

    @Test
    public void shouldGenerateSixNumericDigits() {
        for (int index = 0; index < 1_000; index++) {
            String otp = OtpUtils.generateNumericOtp();

            assertNotNull(otp);

            assertTrue(
                    "OTP không đúng định dạng 6 chữ số: " + otp,
                    otp.matches("[0-9]{6}")
            );
        }
    }
}