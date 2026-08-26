package com.thanhdat.servletmvc.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

import com.thanhdat.servletmvc.config.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "databaseHealthController",
        urlPatterns = "/health/database"
)
public class DatabaseHealthController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.setContentType("text/plain");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (Connection connection = DBConnection.getConnection()) {
            if (connection.isValid(2)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(
                        "Kết nối MySQL thành công."
                );
                return;
            }

            response.setStatus(
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE
            );
            response.getWriter().println(
                    "MySQL không phản hồi kết nối."
            );
        } catch (SQLException | IllegalStateException exception) {
            getServletContext().log(
                    "Không thể kết nối đến MySQL.",
                    exception
            );

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
            response.getWriter().println(
                    "Kết nối MySQL thất bại. Hãy kiểm tra log Tomcat."
            );
        }
    }
}
