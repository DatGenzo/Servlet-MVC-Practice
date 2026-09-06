package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.PageResult;
import com.thanhdat.servletmvc.models.Product;
import com.thanhdat.servletmvc.services.ProductService;
import com.thanhdat.servletmvc.services.impl.ProductServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "productListController",
        urlPatterns = "/product"
)
public class ProductListController extends HttpServlet {

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
        Integer requestedPage = parsePage(
                request.getParameter("page")
        );

        if (requestedPage == null) {
            redirectToPage(request, response, 1);
            return;
        }

        try {
            PageResult<Product> pageResult =
                    productService.getPage(requestedPage);

            if (pageResult.getPage() != requestedPage) {
                redirectToPage(
                        request,
                        response,
                        pageResult.getPage()
                );
                return;
            }

            request.setAttribute("pageResult", pageResult);

            request.getRequestDispatcher(
                    "/WEB-INF/views/products/list.jsp"
            ).forward(request, response);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Không thể tải trang Product.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private Integer parsePage(String value) {
        if (value == null || value.isBlank()) {
            return 1;
        }

        try {
            int page = Integer.parseInt(value.trim());
            return page > 0 ? page : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private void redirectToPage(
            HttpServletRequest request,
            HttpServletResponse response,
            int page
    ) throws IOException {
        response.sendRedirect(
                request.getContextPath()
                        + "/product?page="
                        + page
        );
    }
}
