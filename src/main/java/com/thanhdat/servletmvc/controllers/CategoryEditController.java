package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.DuplicateResourceException;
import com.thanhdat.servletmvc.exceptions.FileStorageException;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.services.CategoryService;
import com.thanhdat.servletmvc.services.FileStorageService;
import com.thanhdat.servletmvc.services.impl.CategoryServiceImpl;
import com.thanhdat.servletmvc.services.impl.LocalFileStorageService;
import com.thanhdat.servletmvc.utils.AppConstants;
import com.thanhdat.servletmvc.utils.MultipartUtils;
import com.thanhdat.servletmvc.utils.RequestUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet(
        name = "categoryEditController",
        urlPatterns = "/admin/categories/edit"
)
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize =
                AppConstants.MAX_CATEGORY_ICON_BYTES,
        maxRequestSize =
                AppConstants.MAX_MULTIPART_REQUEST_BYTES
)
public class CategoryEditController
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

        try {
            int id = RequestUtils.getPositiveIntParameter(
                    request,
                    "id"
            );

            Category category =
                    categoryService.getById(id);

            request.setAttribute(
                    "category",
                    category
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
            getServletContext().log(
                    "Không thể tải Category cần sửa.",
                    exception
            );

            response.sendError(
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Category existingCategory = null;
        Category submittedCategory = new Category();

        String savedNewIcon = null;
        boolean databaseUpdated = false;

        try {
            int id = RequestUtils.getPositiveIntParameter(
                    request,
                    "id"
            );

            existingCategory =
                    categoryService.getById(id);

            String name =
                    request.getParameter("name");

            submittedCategory.setId(id);
            submittedCategory.setName(name);
            submittedCategory.setIcon(
                    existingCategory.getIcon()
            );

            Part iconPart = request.getPart("icon");

            if (MultipartUtils.hasUploadedFile(iconPart)) {
                savedNewIcon =
                        fileStorageService
                                .saveCategoryIcon(iconPart);

                submittedCategory.setIcon(savedNewIcon);
            }

            categoryService.update(submittedCategory);
            databaseUpdated = true;

            /*
             * Database đã trỏ tới icon mới,
             * lúc này mới xóa icon cũ.
             */
            if (savedNewIcon != null) {
                deleteOldIconQuietly(
                        existingCategory.getIcon()
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                            + "/admin/categories?updated=success"
            );
        } catch (
                ValidationException
                | DuplicateResourceException exception
        ) {
            if (existingCategory == null) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        exception.getMessage()
                );
                return;
            }

            submittedCategory.setIcon(
                    existingCategory.getIcon()
            );

            request.setAttribute(
                    "category",
                    submittedCategory
            );
            request.setAttribute(
                    "alert",
                    exception.getMessage()
            );

            prepareForm(request);
            forwardToForm(request, response);
        } catch (ResourceNotFoundException exception) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    exception.getMessage()
            );
        } catch (IllegalStateException exception) {
            if (existingCategory == null) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Dữ liệu tải lên không hợp lệ."
                );
                return;
            }

            submittedCategory.setIcon(
                    existingCategory.getIcon()
            );

            request.setAttribute(
                    "category",
                    submittedCategory
            );
            request.setAttribute(
                    "alert",
                    "Dữ liệu tải lên vượt quá giới hạn cho phép."
            );

            prepareForm(request);
            forwardToForm(request, response);
        } catch (
                FileStorageException
                | DataAccessException exception
        ) {
            getServletContext().log(
                    "Không thể cập nhật Category.",
                    exception
            );

            response.sendError(
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR
            );
        } finally {
            /*
             * Nếu icon mới đã lưu nhưng database chưa
             * cập nhật thì phải xóa icon mới.
             */
            if (!databaseUpdated
                    && savedNewIcon != null) {
                deleteNewIconQuietly(savedNewIcon);
            }
        }
    }

    private void deleteOldIconQuietly(
            String relativePath
    ) {
        if (relativePath == null
                || relativePath.isBlank()) {
            return;
        }

        try {
            fileStorageService.delete(relativePath);
        } catch (FileStorageException exception) {
            getServletContext().log(
                    "Database đã cập nhật nhưng không thể xóa icon cũ.",
                    exception
            );
        }
    }

    private void deleteNewIconQuietly(
            String relativePath
    ) {
        try {
            fileStorageService.delete(relativePath);
        } catch (FileStorageException exception) {
            getServletContext().log(
                    "Không thể dọn icon mới sau khi cập nhật thất bại.",
                    exception
            );
        }
    }

    private void prepareForm(
            HttpServletRequest request
    ) {
        request.setAttribute(
                "pageTitle",
                "Sửa Category"
        );
        request.setAttribute(
                "submitLabel",
                "Lưu thay đổi"
        );
        request.setAttribute(
                "formAction",
                "/admin/categories/edit"
        );
        request.setAttribute("editMode", true);
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