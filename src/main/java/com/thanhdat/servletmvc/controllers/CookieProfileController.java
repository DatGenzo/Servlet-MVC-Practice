package com.thanhdat.servletmvc.controllers;

import java.io.IOException;
import java.util.Optional;

import com.thanhdat.servletmvc.utils.AppConstants;
import com.thanhdat.servletmvc.utils.CookieUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "cookieProfileController",
        urlPatterns = "/cookie/profile"
)
public class CookieProfileController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Optional<String> usernameCookie =
                CookieUtils.getValue(
                        request,
                        AppConstants.COOKIE_DEMO_USERNAME
                );

        if (usernameCookie.isEmpty()) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/cookie/login"
            );
            return;
        }

        request.setAttribute(
                "username",
                usernameCookie.get()
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/auth/cookie-profile.jsp"
        ).include(request, response);
    }
}