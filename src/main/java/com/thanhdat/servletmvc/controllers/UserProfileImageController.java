package com.thanhdat.servletmvc.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.services.FileStorageService;
import com.thanhdat.servletmvc.services.UserProfileService;
import com.thanhdat.servletmvc.services.impl.LocalFileStorageService;
import com.thanhdat.servletmvc.services.impl.UserProfileServiceImpl;
import com.thanhdat.servletmvc.utils.AppConstants;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(
        name = "userProfileImageController",
        urlPatterns = "/session/profile/image"
)
public class UserProfileImageController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private transient UserProfileService userProfileService;
    private transient FileStorageService fileStorageService;

    @Override
    public void init() {
        userProfileService = new UserProfileServiceImpl();
        fileStorageService = new LocalFileStorageService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        User authenticatedUser = getAuthenticatedUser(request);

        if (authenticatedUser == null) {
            response.sendRedirect(
                    request.getContextPath() + "/session/login"
            );
            return;
        }

        try {
            User currentUser = userProfileService.getById(
                    authenticatedUser.getId()
            );

            Optional<Path> imageFile = fileStorageService.find(
                    currentUser.getImage()
            );

            if (imageFile.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Path file = imageFile.get();

            response.setContentType(contentType(file));
            response.setContentLengthLong(Files.size(file));
            response.setHeader(
                    "Cache-Control",
                    "private, no-cache, no-store, must-revalidate"
            );
            response.setHeader("X-Content-Type-Options", "nosniff");

            try (InputStream inputStream = Files.newInputStream(file)) {
                inputStream.transferTo(response.getOutputStream());
            }
        } catch (ResourceNotFoundException exception) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Không thể tải ảnh Profile.",
                    exception
            );
            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    private User getAuthenticatedUser(
            HttpServletRequest request
    ) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Object user = session.getAttribute(
                AppConstants.SESSION_AUTHENTICATED_USER
        );

        return user instanceof User authenticatedUser
                ? authenticatedUser
                : null;
    }

    private String contentType(Path file) {
        String name = file.getFileName()
                .toString()
                .toLowerCase(Locale.ROOT);

        if (name.endsWith(".png")) {
            return "image/png";
        }

        return "image/jpeg";
    }
}
