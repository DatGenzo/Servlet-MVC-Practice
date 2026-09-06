package com.thanhdat.servletmvc.utils;

import java.math.BigDecimal;

import com.thanhdat.servletmvc.exceptions.ValidationException;

import jakarta.servlet.http.HttpServletRequest;

public final class ProductFormUtils {

    private ProductFormUtils() {
    }

    public static BigDecimal getPrice(
            HttpServletRequest request
    ) {
        String value = request.getParameter("price");

        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    "Giá Product không được để trống."
            );
        }

        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            throw new ValidationException(
                    "Giá Product phải là một số hợp lệ."
            );
        }
    }

    public static int getQuantity(
            HttpServletRequest request
    ) {
        String value = request.getParameter("quantity");

        if (value == null || value.isBlank()) {
            throw new ValidationException(
                    "Số lượng Product không được để trống."
            );
        }

        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new ValidationException(
                    "Số lượng Product phải là số nguyên."
            );
        }
    }

    public static int getCategoryId(
            HttpServletRequest request
    ) {
        return RequestUtils.getPositiveIntParameter(
                request,
                "categoryId"
        );
    }
}
