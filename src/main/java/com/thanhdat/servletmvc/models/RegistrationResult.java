package com.thanhdat.servletmvc.models;

public record RegistrationResult(
        int userId,
        String email,
        boolean emailSent
) {
}
