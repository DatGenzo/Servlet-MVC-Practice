package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.services.ProductService;
import com.thanhdat.servletmvc.services.impl.ProductServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "homeController",
        urlPatterns = "/home"
)
public class HomeController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final int LATEST_PRODUCT_LIMIT = 10;

    private transient ProductService productService;

    @Override
    public void init() {
        productService = new ProductServiceImpl();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        try {
            request.setAttribute(
                    "products",
                    productService.getLatest(
                            LATEST_PRODUCT_LIMIT
                    )
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/home.jsp"
            ).include(request, response);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Không thể tải Product mới nhất.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
