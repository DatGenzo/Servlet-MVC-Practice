package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.utils.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(
        name = "sessionProfileController",
        urlPatterns = "/session/profile"
)
public class SessionProfileController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/session/login"
            );
            return;
        }

        Object userAttribute = session.getAttribute(
                AppConstants.SESSION_AUTHENTICATED_USER
        );

        if (!(userAttribute instanceof User user)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/session/login"
            );
            return;
        }

        request.setAttribute("user", user);

        request.getRequestDispatcher(
                "/WEB-INF/views/auth/session-profile.jsp"
        ).forward(request, response);
    }
}