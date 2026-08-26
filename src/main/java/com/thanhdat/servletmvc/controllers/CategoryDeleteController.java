package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.FileStorageException;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.services.CategoryService;
import com.thanhdat.servletmvc.services.FileStorageService;
import com.thanhdat.servletmvc.services.impl.CategoryServiceImpl;
import com.thanhdat.servletmvc.services.impl.LocalFileStorageService;
import com.thanhdat.servletmvc.utils.RequestUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(
        name = "categoryDeleteController",
        urlPatterns = "/admin/categories/delete"
)
public class CategoryDeleteController
        extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient CategoryService categoryService;
    private transient FileStorageService fileStorageService;

    @Override
    public void init() {
        categoryService = new CategoryServiceImpl();
        fileStorageService =
                new LocalFileStorageService();
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

            Category category =
                    categoryService.getById(id);

            categoryService.delete(id);

            /*
             * Chỉ xóa file sau khi record đã xóa
             * thành công khỏi database.
             */
            deleteIconQuietly(category.getIcon());

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/categories?deleted=success"
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
                    "Không thể xóa Category.",
                    exception
            );

            response.sendError(
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void deleteIconQuietly(String relativePath) {
        if (relativePath == null
                || relativePath.isBlank()) {
            return;
        }

        try {
            fileStorageService.delete(relativePath);
        } catch (FileStorageException exception) {
            getServletContext().log(
                    "Đã xóa Category nhưng không thể xóa icon.",
                    exception
            );
        }
    }
}