package com.thanhdat.servletmvc.models;

import java.time.LocalDateTime;

public class UserOtp {

    private long id;
    private int userId;
    private OtpPurpose purpose;
    private String otpHash;
    private LocalDateTime expiresAt;
    private int attemptCount;
    private LocalDateTime consumedAt;
    private LocalDateTime createdAt;

    public UserOtp() {
    }

    public UserOtp(
            long id,
            int userId,
            OtpPurpose purpose,
            String otpHash,
            LocalDateTime expiresAt,
            int attemptCount,
            LocalDateTime consumedAt,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.purpose = purpose;
        this.otpHash = otpHash;
        this.expiresAt = expiresAt;
        this.attemptCount = attemptCount;
        this.consumedAt = consumedAt;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public OtpPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(OtpPurpose purpose) {
        this.purpose = purpose;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = otpHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public LocalDateTime getConsumedAt() {
        return consumedAt;
    }

    public void setConsumedAt(LocalDateTime consumedAt) {
        this.consumedAt = consumedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isConsumed() {
        return consumedAt != null;
    }
}