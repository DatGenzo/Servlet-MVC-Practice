package com.thanhdat.servletmvc.filters;

import java.io.IOException;

import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.utils.AppConstants;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(
        filterName = "adminAuthorizationFilter",
        urlPatterns = "/admin/*"
)
public class AdminAuthorizationFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest request =
                (HttpServletRequest) servletRequest;

        HttpServletResponse response =
                (HttpServletResponse) servletResponse;

        HttpSession session =
                request.getSession(false);

        Object authenticatedUser =
                session == null
                        ? null
                        : session.getAttribute(
                                AppConstants
                                        .SESSION_AUTHENTICATED_USER
                        );

        if (!(authenticatedUser instanceof User user)) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/session/login"
            );
            return;
        }

        if (!AppConstants.ROLE_ADMIN.equals(user.getRole())) {
            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Bạn không có quyền truy cập trang quản trị."
            );
            return;
        }

        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }
}