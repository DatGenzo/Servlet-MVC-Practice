package com.thanhdat.servletmvc.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieUtils {

    private CookieUtils() {
    }

    public static Optional<String> getValue(
            HttpServletRequest request,
            String cookieName
    ) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return Optional.empty();
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return decode(cookie.getValue());
            }
        }

        return Optional.empty();
    }

    public static void add(
            HttpServletRequest request,
            HttpServletResponse response,
            String name,
            String value,
            int maxAge
    ) {
        Cookie cookie = new Cookie(name, encode(value));

        configureCookie(request, cookie);
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public static void delete(
            HttpServletRequest request,
            HttpServletResponse response,
            String name
    ) {
        Cookie cookie = new Cookie(name, "");

        configureCookie(request, cookie);
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    private static void configureCookie(
            HttpServletRequest request,
            Cookie cookie
    ) {
        String contextPath = request.getContextPath();

        cookie.setPath(
                contextPath == null || contextPath.isBlank()
                        ? "/"
                        : contextPath
        );

        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setAttribute("SameSite", "Lax");
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        value.getBytes(StandardCharsets.UTF_8)
                );
    }

    private static Optional<String> decode(String value) {
        try {
            byte[] decodedValue = Base64.getUrlDecoder()
                    .decode(value);

            return Optional.of(
                    new String(
                            decodedValue,
                            StandardCharsets.UTF_8
                    )
            );
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}