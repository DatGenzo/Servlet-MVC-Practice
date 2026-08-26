package com.thanhdat.servletmvc.utils;

import com.thanhdat.servletmvc.exceptions.ValidationException;

import jakarta.servlet.http.HttpServletRequest;

public final class RequestUtils {

    private RequestUtils() {
    }

    public static int getPositiveIntParameter(
            HttpServletRequest request,
            String parameterName
    ) {
        String parameterValue =
                request.getParameter(parameterName);

        if (parameterValue == null
                || parameterValue.isBlank()) {
            throw new ValidationException(
                    "Thiếu tham số " + parameterName + "."
            );
        }

        try {
            int value = Integer.parseInt(
                    parameterValue.trim()
            );

            if (value <= 0) {
                throw new ValidationException(
                        "Tham số "
                                + parameterName
                                + " không hợp lệ."
                );
            }

            return value;
        } catch (NumberFormatException exception) {
            throw new ValidationException(
                    "Tham số "
                            + parameterName
                            + " phải là số nguyên."
            );
        }
    }
}