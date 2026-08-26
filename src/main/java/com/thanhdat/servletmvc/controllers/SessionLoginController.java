package com.thanhdat.servletmvc.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.User;
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
        name = "sessionLoginController",
        urlPatterns = "/session/login"
)
public class SessionLoginController extends HttpServlet {

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

        if (session != null
                && session.getAttribute(
                        AppConstants.SESSION_AUTHENTICATED_USER
                ) instanceof User) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/session/profile"
            );
            return;
        }

        forwardToLogin(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String username = request.getParameter("username");
        String passwordValue =
                request.getParameter("password");

        request.setAttribute("username", username);

        if (username == null
                || username.isBlank()
                || passwordValue == null
                || passwordValue.isBlank()) {

            request.setAttribute(
                    "alert",
                    "Tài khoản và mật khẩu không được để trống."
            );

            forwardToLogin(request, response);
            return;
        }

        char[] rawPassword = passwordValue.toCharArray();

        try {
            Optional<User> authenticatedUser =
                    userService.authenticate(
                            username,
                            rawPassword
                    );

            if (authenticatedUser.isEmpty()) {
                request.setAttribute(
                        "alert",
                        "Tài khoản hoặc mật khẩu không đúng."
                );

                forwardToLogin(request, response);
                return;
            }

            HttpSession session =
                    request.getSession(true);

            /*
             * Đổi session ID sau khi đăng nhập thành công,
             * giúp phòng tránh Session Fixation.
             */
            request.changeSessionId();

            session.setAttribute(
                    AppConstants.SESSION_AUTHENTICATED_USER,
                    authenticatedUser.get()
            );

            session.setMaxInactiveInterval(
                    AppConstants.SESSION_MAX_INACTIVE_INTERVAL
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/session/profile"
            );
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Lỗi database khi đăng nhập bằng Session.",
                    exception
            );

            request.setAttribute(
                    "alert",
                    "Hệ thống đang xảy ra lỗi. Vui lòng thử lại."
            );

            forwardToLogin(request, response);
        } finally {
            Arrays.fill(rawPassword, '\0');
        }
    }

    private void forwardToLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/auth/session-login.jsp"
        ).forward(request, response);
    }
}