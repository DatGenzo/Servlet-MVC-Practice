package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

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
        name = "passwordResetResendController",
        urlPatterns = "/reset-password/verify/resend"
)
public class PasswordResetResendController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient UserService userService;

    @Override
    public void init() {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (!hasPendingReset(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/forgot-password"
            );
            return;
        }

        int userId = (Integer) session.getAttribute(
                AppConstants
                        .SESSION_PENDING_PASSWORD_RESET_USER_ID
        );

        String email = (String) session.getAttribute(
                AppConstants
                        .SESSION_PENDING_PASSWORD_RESET_EMAIL
        );

        try {
            userService.resendPasswordResetOtp(
                    userId,
                    email
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/reset-password/verify?resent=success"
            );
        } catch (ValidationException exception) {
            request.setAttribute(
                    "alert",
                    exception.getMessage()
            );

            forwardToVerification(request, response, email);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Không thể gửi lại OTP đặt lại mật khẩu.",
                    exception
            );

            request.setAttribute(
                    "alert",
                    "Hệ thống đang xảy ra lỗi. Vui lòng thử lại."
            );

            forwardToVerification(request, response, email);
        }
    }

    private boolean hasPendingReset(HttpSession session) {
        return session != null
                && session.getAttribute(
                        AppConstants
                                .SESSION_PENDING_PASSWORD_RESET_USER_ID
                ) instanceof Integer
                && session.getAttribute(
                        AppConstants
                                .SESSION_PENDING_PASSWORD_RESET_EMAIL
                ) instanceof String;
    }

    private void forwardToVerification(
            HttpServletRequest request,
            HttpServletResponse response,
            String email
    ) throws ServletException, IOException {
        request.setAttribute("pendingEmail", email);

        request.getRequestDispatcher(
                "/WEB-INF/views/auth/verify-reset-otp.jsp"
        ).forward(request, response);
    }
}
