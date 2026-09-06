package com.thanhdat.servletmvc.services;

import com.thanhdat.servletmvc.models.OtpPurpose;
import com.thanhdat.servletmvc.models.OtpVerificationStatus;

public interface OtpService {

    String issueOtp(
            int userId,
            OtpPurpose purpose
    );

    OtpVerificationStatus verifyOtp(
            int userId,
            OtpPurpose purpose,
            String rawOtp
    );
}