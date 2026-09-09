package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.DuplicateResourceException;
import com.thanhdat.servletmvc.exceptions.FileStorageException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.services.CategoryService;
import com.thanhdat.servletmvc.services.FileStorageService;
import com.thanhdat.servletmvc.services.impl.CategoryServiceImpl;
import com.thanhdat.servletmvc.services.impl.LocalFileStorageService;
import com.thanhdat.servletmvc.utils.AppConstants;
import com.thanhdat.servletmvc.utils.MultipartUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(
        name = "categoryCreateController",
        urlPatterns = "/admin/categories/create"
)
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize =
                AppConstants.MAX_CATEGORY_ICON_BYTES,
        maxRequestSize =
                AppConstants.MAX_MULTIPART_REQUEST_BYTES
)
public class CategoryCreateController
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
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setAttribute(
                "category",
                new Category()
        );

        prepareForm(request);
        forwardToForm(request, response);
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Category category = new Category();

        String savedIcon = null;

        try {
            String name = request.getParameter("name");
            category.setName(name);
            Part iconPart = request.getPart("icon");

            if (MultipartUtils.hasUploadedFile(iconPart)) {
                savedIcon =
                        fileStorageService
                                .saveCategoryIcon(iconPart);

                category.setIcon(savedIcon);
            }

            categoryService.create(category);

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/categories?created=success"
            );
        } catch (
                ValidationException
                | DuplicateResourceException exception
        ) {
            deleteQuietly(savedIcon);

            category.setIcon(null);

            request.setAttribute("category", category);
            request.setAttribute(
                    "alert",
                    exception.getMessage()
            );

            prepareForm(request);
            forwardToForm(request, response);
        } catch (IllegalStateException exception) {
            deleteQuietly(savedIcon);

            request.setAttribute("category", category);
            request.setAttribute(
                    "alert",
                    "Dữ liệu tải lên vượt quá giới hạn cho phép."
            );

            prepareForm(request);
            forwardToForm(request, response);
        } catch (FileStorageException exception) {
            deleteQuietly(savedIcon);

            getServletContext().log(
                    "Không thể lưu icon Category.",
                    exception
            );

            response.sendError(
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR
            );
        } catch (DataAccessException exception) {
            deleteQuietly(savedIcon);

            getServletContext().log(
                    "Không thể thêm Category.",
                    exception
            );

            response.sendError(
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private void deleteQuietly(String relativePath) {
        if (relativePath == null) {
            return;
        }

        try {
            fileStorageService.delete(relativePath);
        } catch (FileStorageException exception) {
            getServletContext().log(
                    "Không thể dọn icon vừa tải lên.",
                    exception
            );
        }
    }

    private void prepareForm(
            HttpServletRequest request
    ) {
        request.setAttribute(
                "pageTitle",
                "Thêm Category"
        );
        request.setAttribute(
                "submitLabel",
                "Thêm Category"
        );
        request.setAttribute(
                "formAction",
                "/admin/categories/create"
        );
        request.setAttribute("editMode", false);
    }

    private void forwardToForm(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/admin/categories/form.jsp"
        ).include(request, response);
    }
}