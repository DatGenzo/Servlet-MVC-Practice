package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.services.ProductService;
import com.thanhdat.servletmvc.services.impl.ProductServiceImpl;
import com.thanhdat.servletmvc.utils.RequestUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "productDeleteController",
        urlPatterns = "/admin/products/delete"
)
public class ProductDeleteController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient ProductService productService;

    @Override
    public void init() {
        productService = new ProductServiceImpl();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        try {
            int id = RequestUtils.getPositiveIntParameter(
                    request,
                    "id"
            );

            productService.delete(id);

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/products?deleted=success"
            );
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
                    "Không thể xóa Product.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }
}
