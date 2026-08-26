package com.thanhdat.servletmvc.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
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
        name = "categoryIconController",
        urlPatterns = "/admin/category-icons"
)
public class CategoryIconController
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
    ) throws IOException {

        try {
            int categoryId =
                    RequestUtils.getPositiveIntParameter(
                            request,
                            "id"
                    );

            Category category =
                    categoryService.getById(categoryId);

            Optional<Path> iconFile =
                    fileStorageService.find(
                            category.getIcon()
                    );

            if (iconFile.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND
                );
                return;
            }

            Path file = iconFile.get();

            response.setContentType(
                    determineContentType(file)
            );
            response.setContentLengthLong(
                    Files.size(file)
            );

            response.setHeader(
                    "X-Content-Type-Options",
                    "nosniff"
            );
            response.setHeader(
                    "Cache-Control",
                    "private, max-age=3600"
            );

            Files.copy(
                    file,
                    response.getOutputStream()
            );
        } catch (ValidationException exception) {
            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST
            );
        } catch (ResourceNotFoundException exception) {
            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND
            );
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Không thể tải thông tin icon Category.",
                    exception
            );

            response.sendError(
                    HttpServletResponse
                            .SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private String determineContentType(Path file) {
        String fileName =
                file.getFileName()
                        .toString()
                        .toLowerCase();

        if (fileName.endsWith(".png")) {
            return "image/png";
        }

        return "image/jpeg";
    }
}