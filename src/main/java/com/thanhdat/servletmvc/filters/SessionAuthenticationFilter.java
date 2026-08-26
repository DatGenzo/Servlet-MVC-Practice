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
        filterName = "sessionAuthenticationFilter",
        urlPatterns = {
                "/session/profile"
        }
)
public class SessionAuthenticationFilter implements Filter {

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

        boolean authenticated =
                session != null
                && session.getAttribute(
                        AppConstants.SESSION_AUTHENTICATED_USER
                ) instanceof User;

        if (!authenticated) {
            response.sendRedirect(
                    request.getContextPath()
                            + "/session/login"
            );
            return;
        }

        /*
         * Không cache các trang yêu cầu đăng nhập.
         * Sau logout, trình duyệt không nên hiển thị lại
         * nội dung cũ bằng nút Back.
         */
        response.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }
}