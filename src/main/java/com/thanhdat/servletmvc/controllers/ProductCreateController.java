package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.models.Product;
import com.thanhdat.servletmvc.services.CategoryService;
import com.thanhdat.servletmvc.services.ProductService;
import com.thanhdat.servletmvc.services.impl.CategoryServiceImpl;
import com.thanhdat.servletmvc.services.impl.ProductServiceImpl;
import com.thanhdat.servletmvc.utils.ProductFormUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "productCreateController",
        urlPatterns = "/admin/products/create"
)
public class ProductCreateController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient ProductService productService;
    private transient CategoryService categoryService;

    @Override
    public void init() {
        productService = new ProductServiceImpl();
        categoryService = new CategoryServiceImpl();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        try {
            Product product = new Product();
            product.setQuantity(0);
            request.setAttribute("product", product);

            prepareForm(request, false);
            forwardToForm(request, response);
        } catch (DataAccessException exception) {
            handleDatabaseError(response, exception);
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        Product product = new Product();

        try {
            bindProduct(request, product);
            productService.create(product);

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/products?created=success"
            );
        } catch (
                ValidationException
                | ResourceNotFoundException exception
        ) {
            request.setAttribute("product", product);
            request.setAttribute("alert", exception.getMessage());

            prepareForm(request, false);
            forwardToForm(request, response);
        } catch (DataAccessException exception) {
            handleDatabaseError(response, exception);
        }
    }

    private void bindProduct(
            HttpServletRequest request,
            Product product
    ) {
        product.setName(request.getParameter("name"));
        product.setDescription(
                request.getParameter("description")
        );
        product.setPrice(ProductFormUtils.getPrice(request));
        product.setQuantity(
                ProductFormUtils.getQuantity(request)
        );

        Category category = new Category();
        category.setId(ProductFormUtils.getCategoryId(request));
        product.setCategory(category);
    }

    private void prepareForm(
            HttpServletRequest request,
            boolean editMode
    ) {
        request.setAttribute(
                "categories",
                categoryService.getAll()
        );
        request.setAttribute("pageTitle", "Thêm Product");
        request.setAttribute("submitLabel", "Thêm Product");
        request.setAttribute(
                "formAction",
                "/admin/products/create"
        );
        request.setAttribute("editMode", editMode);
    }

    private void forwardToForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher(
                "/WEB-INF/views/admin/products/form.jsp"
        ).include(request, response);
    }

    private void handleDatabaseError(
            HttpServletResponse response,
            DataAccessException exception
    ) throws IOException {
        getServletContext().log(
                "Không thể thêm Product.",
                exception
        );

        response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );
    }
}
