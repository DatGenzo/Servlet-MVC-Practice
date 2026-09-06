package com.thanhdat.servletmvc.utils;

public final class AppConstants {

    public static final String COOKIE_DEMO_USERNAME =
            "mvc_cookie_username";

    public static final int COOKIE_DEMO_MAX_AGE =
            30 * 60;

    public static final String SESSION_AUTHENTICATED_USER =
            "authenticatedUser";

    public static final int SESSION_MAX_INACTIVE_INTERVAL =
            30 * 60;

    public static final String SESSION_PENDING_ACTIVATION_USER_ID =
            "pendingActivationUserId";

    public static final String SESSION_PENDING_ACTIVATION_EMAIL =
            "pendingActivationEmail";

    public static final String SESSION_PENDING_PASSWORD_RESET_USER_ID =
            "pendingPasswordResetUserId";

    public static final String SESSION_PENDING_PASSWORD_RESET_EMAIL =
            "pendingPasswordResetEmail";

    public static final String SESSION_PASSWORD_RESET_VERIFIED_AT =
            "passwordResetVerifiedAt";

    public static final long PASSWORD_RESET_AUTHORIZATION_SECONDS =
            10 * 60;

    public static final String ROLE_ADMIN = "ADMIN";

    public static final long MAX_CATEGORY_ICON_BYTES =
            2L * 1024 * 1024;

    public static final long MAX_MULTIPART_REQUEST_BYTES =
            3L * 1024 * 1024;
    private AppConstants() {
    }
}
