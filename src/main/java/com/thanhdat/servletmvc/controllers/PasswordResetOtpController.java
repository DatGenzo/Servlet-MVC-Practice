package com.thanhdat.servletmvc.controllers;

import java.io.IOException;
import java.time.LocalDateTime;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.OtpVerificationStatus;
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
        name = "passwordResetOtpController",
        urlPatterns = "/reset-password/verify"
)
public class PasswordResetOtpController extends HttpServlet {

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

        if (!hasPendingReset(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/forgot-password"
            );
            return;
        }

        forwardToVerification(request, response, session);
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

        try {
            OtpVerificationStatus status =
                    userService.verifyPasswordResetOtp(
                            userId,
                            request.getParameter("otp")
                    );

            if (status == OtpVerificationStatus.VERIFIED) {
                session.setAttribute(
                        AppConstants
                                .SESSION_PASSWORD_RESET_VERIFIED_AT,
                        LocalDateTime.now()
                );

                response.sendRedirect(
                        request.getContextPath()
                                + "/reset-password"
                );
                return;
            }

            request.setAttribute("alert", messageFor(status));
            forwardToVerification(request, response, session);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Lỗi database khi xác nhận OTP đặt lại mật khẩu.",
                    exception
            );

            request.setAttribute(
                    "alert",
                    "Hệ thống đang xảy ra lỗi. Vui lòng thử lại."
            );

            forwardToVerification(request, response, session);
        }
    }

    private String messageFor(OtpVerificationStatus status) {
        return switch (status) {
            case INVALID -> "Mã OTP không đúng.";
            case EXPIRED ->
                    "Mã OTP đã hết hạn. Vui lòng gửi lại mã.";
            case ATTEMPTS_EXCEEDED ->
                    "Bạn đã nhập sai quá số lần cho phép. Vui lòng gửi lại mã.";
            case NOT_FOUND ->
                    "Mã OTP không hợp lệ hoặc không còn hiệu lực.";
            case VERIFIED -> "Xác nhận OTP thành công.";
        };
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
            HttpSession session
    ) throws ServletException, IOException {
        request.setAttribute(
                "pendingEmail",
                session.getAttribute(
                        AppConstants
                                .SESSION_PENDING_PASSWORD_RESET_EMAIL
                )
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/auth/verify-reset-otp.jsp"
        ).forward(request, response);
    }
}
