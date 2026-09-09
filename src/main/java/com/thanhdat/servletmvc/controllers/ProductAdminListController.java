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
        name = "productAdminListController",
        urlPatterns = "/admin/products"
)
public class ProductAdminListController extends HttpServlet {

    private static final long serialVersionUID = 1L;

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
                    productService.getAll()
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/admin/products/list.jsp"
            ).include(request, response);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Không thể tải danh sách Product quản trị.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
