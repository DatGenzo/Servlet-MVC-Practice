package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

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
        name = "accountActivationController",
        urlPatterns = "/account/activate"
)
public class AccountActivationController
        extends HttpServlet {

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

        if (!hasPendingActivation(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/register"
            );
            return;
        }

        forwardToActivation(request, response, session);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        if (!hasPendingActivation(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/register"
            );
            return;
        }

        int userId = (Integer) session.getAttribute(
                AppConstants.SESSION_PENDING_ACTIVATION_USER_ID
        );

        String rawOtp = request.getParameter("otp");

        try {
            OtpVerificationStatus status =
                    userService.activateAccount(
                            userId,
                            rawOtp
                    );

            if (status == OtpVerificationStatus.VERIFIED) {
                session.removeAttribute(
                        AppConstants
                                .SESSION_PENDING_ACTIVATION_USER_ID
                );

                session.removeAttribute(
                        AppConstants
                                .SESSION_PENDING_ACTIVATION_EMAIL
                );

                response.sendRedirect(
                        request.getContextPath()
                                + "/session/login?activated=success"
                );
                return;
            }

            request.setAttribute(
                    "alert",
                    messageFor(status)
            );

            forwardToActivation(request, response, session);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Lỗi database khi kích hoạt tài khoản.",
                    exception
            );

            request.setAttribute(
                    "alert",
                    "Hệ thống đang xảy ra lỗi. Vui lòng thử lại."
            );

            forwardToActivation(request, response, session);
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
                    "Không tìm thấy mã OTP còn hiệu lực. Vui lòng gửi lại mã.";
            case VERIFIED -> "Kích hoạt thành công.";
        };
    }

    private boolean hasPendingActivation(HttpSession session) {
        return session != null
                && session.getAttribute(
                        AppConstants
                                .SESSION_PENDING_ACTIVATION_USER_ID
                ) instanceof Integer
                && session.getAttribute(
                        AppConstants
                                .SESSION_PENDING_ACTIVATION_EMAIL
                ) instanceof String;
    }

    private void forwardToActivation(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session
    ) throws ServletException, IOException {
        request.setAttribute(
                "pendingEmail",
                session.getAttribute(
                        AppConstants
                                .SESSION_PENDING_ACTIVATION_EMAIL
                )
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/auth/activate-account.jsp"
        ).forward(request, response);
    }
}
