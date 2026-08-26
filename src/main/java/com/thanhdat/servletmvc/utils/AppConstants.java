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

    public static final String ROLE_ADMIN = "ADMIN";

    public static final long MAX_CATEGORY_ICON_BYTES =
            2L * 1024 * 1024;

    public static final long MAX_MULTIPART_REQUEST_BYTES =
            3L * 1024 * 1024;
    private AppConstants() {
    }
}