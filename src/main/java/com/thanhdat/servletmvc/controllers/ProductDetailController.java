package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.services.ProductService;
import com.thanhdat.servletmvc.services.impl.ProductServiceImpl;
import com.thanhdat.servletmvc.utils.RequestUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "productDetailController",
        urlPatterns = "/product/detail"
)
public class ProductDetailController extends HttpServlet {

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
            int id = RequestUtils.getPositiveIntParameter(
                    request,
                    "id"
            );

            request.setAttribute(
                    "product",
                    productService.getById(id)
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/products/detail.jsp"
            ).include(request, response);
        } catch (ValidationException exception) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    exception.getMessage()
            );
        } catch (ResourceNotFoundException exception) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    exception.getMessage()
            );
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Không thể tải chi tiết Product.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
