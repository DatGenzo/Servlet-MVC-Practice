package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.utils.AppConstants;
import com.thanhdat.servletmvc.utils.CookieUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "cookieLogoutController",
        urlPatterns = "/cookie/logout"
)
public class CookieLogoutController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        CookieUtils.delete(
                request,
                response,
                AppConstants.COOKIE_DEMO_USERNAME
        );

        response.sendRedirect(
                request.getContextPath()
                        + "/cookie/login?logout=success"
        );
    }
}