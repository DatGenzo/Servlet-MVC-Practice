package com.thanhdat.servletmvc.models;

public enum OtpVerificationStatus {
    VERIFIED,
    INVALID,
    EXPIRED,
    ATTEMPTS_EXCEEDED,
    NOT_FOUND
}