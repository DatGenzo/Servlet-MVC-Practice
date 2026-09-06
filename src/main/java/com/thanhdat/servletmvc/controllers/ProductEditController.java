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
import com.thanhdat.servletmvc.utils.RequestUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "productEditController",
        urlPatterns = "/admin/products/edit"
)
public class ProductEditController extends HttpServlet {

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
            int id = RequestUtils.getPositiveIntParameter(
                    request,
                    "id"
            );

            request.setAttribute(
                    "product",
                    productService.getById(id)
            );

            prepareForm(request);
            forwardToForm(request, response);
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
            product.setId(
                    RequestUtils.getPositiveIntParameter(
                            request,
                            "id"
                    )
            );

            bindProduct(request, product);
            productService.update(product);

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/products?updated=success"
            );
        } catch (ValidationException exception) {
            request.setAttribute("product", product);
            request.setAttribute("alert", exception.getMessage());

            prepareForm(request);
            forwardToForm(request, response);
        } catch (ResourceNotFoundException exception) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    exception.getMessage()
            );
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

    private void prepareForm(HttpServletRequest request) {
        request.setAttribute(
                "categories",
                categoryService.getAll()
        );
        request.setAttribute("pageTitle", "Sửa Product");
        request.setAttribute("submitLabel", "Lưu thay đổi");
        request.setAttribute(
                "formAction",
                "/admin/products/edit"
        );
        request.setAttribute("editMode", true);
    }

    private void forwardToForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher(
                "/WEB-INF/views/admin/products/form.jsp"
        ).forward(request, response);
    }

    private void handleDatabaseError(
            HttpServletResponse response,
            DataAccessException exception
    ) throws IOException {
        getServletContext().log(
                "Không thể cập nhật Product.",
                exception
        );

        response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        );
    }
}
