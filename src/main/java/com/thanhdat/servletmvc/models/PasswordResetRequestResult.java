package com.thanhdat.servletmvc.models;

public record PasswordResetRequestResult(
        int userId,
        String email
) {
}
