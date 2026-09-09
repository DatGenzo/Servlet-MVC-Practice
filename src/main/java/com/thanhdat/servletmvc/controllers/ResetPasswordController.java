package com.thanhdat.servletmvc.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.services.UserService;
import com.thanhdat.servletmvc.services.impl.UserServiceImpl;
import com.thanhdat.servletmvc.utils.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(
        name = "resetPasswordController",
        urlPatterns = "/reset-password"
)
public class ResetPasswordController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (!hasValidResetAuthorization(session)) {
            clearResetSession(session);
            response.sendRedirect(
                    request.getContextPath()
                            + "/forgot-password?authorization=expired"
            );
            return;
        }

        forwardToResetPassword(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (!hasValidResetAuthorization(session)) {
            clearResetSession(session);
            response.sendRedirect(
                    request.getContextPath()
                            + "/forgot-password?authorization=expired"
            );
            return;
        }

        int userId = (Integer) session.getAttribute(
                AppConstants
                        .SESSION_PENDING_PASSWORD_RESET_USER_ID
        );

        char[] rawPassword = toCharacters(
                request.getParameter("password")
        );

        char[] confirmedPassword = toCharacters(
                request.getParameter("confirmedPassword")
        );

        try {
            if (!userService.resetPassword(
                    userId,
                    rawPassword,
                    confirmedPassword
            )) {
                throw new ValidationException(
                        "Không thể đặt lại mật khẩu cho tài khoản này."
                );
            }

            clearResetSession(session);

            response.sendRedirect(
                    request.getContextPath()
                            + "/session/login?reset=success"
            );
        } catch (ValidationException exception) {
            request.setAttribute(
                    "alert",
                    exception.getMessage()
            );

            forwardToResetPassword(request, response);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Lỗi database khi đặt lại mật khẩu.",
                    exception
            );

            request.setAttribute(
                    "alert",
                    "Hệ thống đang xảy ra lỗi. Vui lòng thử lại."
            );

            forwardToResetPassword(request, response);
        } finally {
            Arrays.fill(rawPassword, '\0');
            Arrays.fill(confirmedPassword, '\0');
        }
    }

    private char[] toCharacters(String value) {
        if (value == null) {
            return new char[0];
        }

        return value.toCharArray();
    }

    private boolean hasValidResetAuthorization(
            HttpSession session
    ) {
        if (session == null
                || !(session.getAttribute(
                        AppConstants
                                .SESSION_PENDING_PASSWORD_RESET_USER_ID
                ) instanceof Integer userId)
                || userId <= 0
                || !(session.getAttribute(
                        AppConstants
                                .SESSION_PASSWORD_RESET_VERIFIED_AT
                ) instanceof LocalDateTime verifiedAt)) {
            return false;
        }

        LocalDateTime earliestValidTime =
                LocalDateTime.now().minusSeconds(
                        AppConstants
                                .PASSWORD_RESET_AUTHORIZATION_SECONDS
                );

        return !verifiedAt.isBefore(earliestValidTime);
    }

    private void clearResetSession(HttpSession session) {
        if (session == null) {
            return;
        }

        session.removeAttribute(
                AppConstants
                        .SESSION_PENDING_PASSWORD_RESET_USER_ID
        );

        session.removeAttribute(
                AppConstants
                        .SESSION_PENDING_PASSWORD_RESET_EMAIL
        );

        session.removeAttribute(
                AppConstants
                        .SESSION_PASSWORD_RESET_VERIFIED_AT
        );
    }

    private void forwardToResetPassword(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher(
                "/WEB-INF/views/auth/reset-password.jsp"
        ).include(request, response);
    }
}
