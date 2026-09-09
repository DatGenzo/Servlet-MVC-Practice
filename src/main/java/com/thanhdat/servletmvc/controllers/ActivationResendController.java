package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.MailDeliveryException;
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
        name = "activationResendController",
        urlPatterns = "/account/activate/resend"
)
public class ActivationResendController
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

        if (!hasPendingActivation(session)) {
            response.sendRedirect(
                    request.getContextPath() + "/register"
            );
            return;
        }

        int userId = (Integer) session.getAttribute(
                AppConstants.SESSION_PENDING_ACTIVATION_USER_ID
        );

        String email = (String) session.getAttribute(
                AppConstants.SESSION_PENDING_ACTIVATION_EMAIL
        );

        try {
            userService.resendActivationOtp(
                    userId,
                    email
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/account/activate?resent=success"
            );
        } catch (ValidationException exception) {
            request.setAttribute(
                    "alert",
                    exception.getMessage()
            );

            forwardToActivation(request, response, email);
        } catch (
                MailDeliveryException
                | DataAccessException exception
        ) {
            getServletContext().log(
                    "Không thể gửi lại OTP kích hoạt.",
                    exception
            );

            request.setAttribute(
                    "alert",
                    "Không thể gửi email lúc này. Vui lòng thử lại."
            );

            forwardToActivation(request, response, email);
        }
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
            String email
    ) throws ServletException, IOException {
        request.setAttribute("pendingEmail", email);

        request.getRequestDispatcher(
                "/WEB-INF/views/auth/activate-account.jsp"
        ).include(request, response);
    }
}
