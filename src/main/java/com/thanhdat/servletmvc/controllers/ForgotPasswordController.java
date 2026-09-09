package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.PasswordResetRequestResult;
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
        name = "forgotPasswordController",
        urlPatterns = "/forgot-password"
)
public class ForgotPasswordController extends HttpServlet {

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
        forwardToForgotPassword(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String email = request.getParameter("email");
        request.setAttribute("email", email);

        try {
            PasswordResetRequestResult result =
                    userService.requestPasswordReset(email);

            HttpSession session = request.getSession(true);

            clearResetAuthorization(session);

            session.setAttribute(
                    AppConstants
                            .SESSION_PENDING_PASSWORD_RESET_USER_ID,
                    result.userId()
            );

            session.setAttribute(
                    AppConstants
                            .SESSION_PENDING_PASSWORD_RESET_EMAIL,
                    result.email()
            );

            session.setMaxInactiveInterval(
                    AppConstants.SESSION_MAX_INACTIVE_INTERVAL
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/reset-password/verify?requested=success"
            );
        } catch (ValidationException exception) {
            request.setAttribute(
                    "alert",
                    exception.getMessage()
            );

            forwardToForgotPassword(request, response);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Lỗi database khi yêu cầu đặt lại mật khẩu.",
                    exception
            );

            request.setAttribute(
                    "alert",
                    "Hệ thống đang xảy ra lỗi. Vui lòng thử lại."
            );

            forwardToForgotPassword(request, response);
        }
    }

    private void clearResetAuthorization(HttpSession session) {
        session.removeAttribute(
                AppConstants.SESSION_PASSWORD_RESET_VERIFIED_AT
        );
    }

    private void forwardToForgotPassword(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher(
                "/WEB-INF/views/auth/forgot-password.jsp"
        ).include(request, response);
    }
}
