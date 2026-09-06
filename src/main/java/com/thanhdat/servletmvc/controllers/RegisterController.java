package com.thanhdat.servletmvc.controllers;

import java.io.IOException;
import java.util.Arrays;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.DuplicateResourceException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.RegistrationResult;
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
        name = "registerController",
        urlPatterns = "/register"
)
public class RegisterController extends HttpServlet {

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
        forwardToRegister(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        String username = request.getParameter("username");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");

        request.setAttribute("username", username);
        request.setAttribute("fullName", fullName);
        request.setAttribute("email", email);

        char[] rawPassword = toCharacters(
                request.getParameter("password")
        );

        char[] confirmedPassword = toCharacters(
                request.getParameter("confirmedPassword")
        );

        try {
            RegistrationResult result =
                    userService.register(
                            username,
                            fullName,
                            email,
                            rawPassword,
                            confirmedPassword
                    );

            HttpSession session = request.getSession(true);

            session.setAttribute(
                    AppConstants
                            .SESSION_PENDING_ACTIVATION_USER_ID,
                    result.userId()
            );

            session.setAttribute(
                    AppConstants
                            .SESSION_PENDING_ACTIVATION_EMAIL,
                    result.email()
            );

            session.setMaxInactiveInterval(
                    AppConstants.SESSION_MAX_INACTIVE_INTERVAL
            );

            String resultParameter = result.emailSent()
                    ? "sent=success"
                    : "mailError=true";

            response.sendRedirect(
                    request.getContextPath()
                            + "/account/activate?"
                            + resultParameter
            );
        } catch (
                ValidationException
                | DuplicateResourceException exception
        ) {
            request.setAttribute(
                    "alert",
                    exception.getMessage()
            );

            forwardToRegister(request, response);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Lỗi database khi đăng ký.",
                    exception
            );

            request.setAttribute(
                    "alert",
                    "Hệ thống đang xảy ra lỗi. Vui lòng thử lại."
            );

            forwardToRegister(request, response);
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

    private void forwardToRegister(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher(
                "/WEB-INF/views/auth/register.jsp"
        ).forward(request, response);
    }
}
