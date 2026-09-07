package com.thanhdat.servletmvc.controllers;

import java.io.IOException;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.FileStorageException;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.services.FileStorageService;
import com.thanhdat.servletmvc.services.UserProfileService;
import com.thanhdat.servletmvc.services.impl.LocalFileStorageService;
import com.thanhdat.servletmvc.services.impl.UserProfileServiceImpl;
import com.thanhdat.servletmvc.utils.AppConstants;
import com.thanhdat.servletmvc.utils.MultipartUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet(
        name = "sessionProfileController",
        urlPatterns = "/session/profile"
)
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = AppConstants.MAX_PROFILE_IMAGE_BYTES,
        maxRequestSize = AppConstants.MAX_MULTIPART_REQUEST_BYTES
)
public class SessionProfileController extends HttpServlet {

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
            redirectToLogin(request, response);
            return;
        }

        try {
            User currentUser = userProfileService.getById(
                    authenticatedUser.getId()
            );

            refreshSessionUser(request, currentUser);
            request.setAttribute("user", currentUser);

            if ("success".equals(request.getParameter("updated"))) {
                request.setAttribute(
                        "success",
                        "Cập nhật Profile thành công."
                );
            }

            forwardToProfile(request, response);
        } catch (ResourceNotFoundException exception) {
            invalidateSession(request);
            redirectToLogin(request, response);
        } catch (DataAccessException exception) {
            getServletContext().log(
                    "Không thể tải Profile bằng JPA.",
                    exception
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        User authenticatedUser = getAuthenticatedUser(request);

        if (authenticatedUser == null) {
            redirectToLogin(request, response);
            return;
        }

        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String savedNewImage = null;
        User currentUser = authenticatedUser;

        try {
            currentUser = userProfileService.getById(
                    authenticatedUser.getId()
            );

            String image = currentUser.getImage();
            Part imagePart = request.getPart("image");

            if (MultipartUtils.hasUploadedFile(imagePart)) {
                savedNewImage = fileStorageService.saveProfileImage(
                        authenticatedUser.getId(),
                        imagePart
                );
                image = savedNewImage;
            }

            User updatedUser = userProfileService.updateProfile(
                    authenticatedUser.getId(),
                    fullName,
                    phone,
                    image
            );

            refreshSessionUser(request, updatedUser);

            if (savedNewImage != null) {
                deleteQuietly(currentUser.getImage());
            }

            response.sendRedirect(
                    request.getContextPath()
                            + "/session/profile?updated=success"
            );
        } catch (ValidationException exception) {
            deleteQuietly(savedNewImage);
            showSubmittedForm(
                    request,
                    response,
                    currentUser,
                    fullName,
                    phone,
                    exception.getMessage()
            );
        } catch (IllegalStateException exception) {
            deleteQuietly(savedNewImage);
            showSubmittedForm(
                    request,
                    response,
                    currentUser,
                    fullName,
                    phone,
                    "Ảnh tải lên vượt quá giới hạn cho phép."
            );
        } catch (ResourceNotFoundException exception) {
            deleteQuietly(savedNewImage);
            invalidateSession(request);
            redirectToLogin(request, response);
        } catch (
                FileStorageException
                | DataAccessException exception
        ) {
            deleteQuietly(savedNewImage);

            getServletContext().log(
                    "Không thể cập nhật Profile.",
                    exception
            );

            showSubmittedForm(
                    request,
                    response,
                    currentUser,
                    fullName,
                    phone,
                    "Hệ thống đang xảy ra lỗi. Vui lòng thử lại."
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

    private void showSubmittedForm(
            HttpServletRequest request,
            HttpServletResponse response,
            User currentUser,
            String fullName,
            String phone,
            String alert
    ) throws ServletException, IOException {

        request.setAttribute("user", currentUser);
        request.setAttribute("formFullName", fullName);
        request.setAttribute("formPhone", phone);
        request.setAttribute("alert", alert);
        forwardToProfile(request, response);
    }

    private void refreshSessionUser(
            HttpServletRequest request,
            User user
    ) {
        request.getSession().setAttribute(
                AppConstants.SESSION_AUTHENTICATED_USER,
                user
        );
    }

    private void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }

    private void redirectToLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        response.sendRedirect(
                request.getContextPath() + "/session/login"
        );
    }

    private void forwardToProfile(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {
        request.getRequestDispatcher(
                "/WEB-INF/views/auth/session-profile.jsp"
        ).include(request, response);
    }

    private void deleteQuietly(String relativePath) {
        try {
            fileStorageService.delete(relativePath);
        } catch (RuntimeException exception) {
            getServletContext().log(
                    "Không thể dọn ảnh Profile.",
                    exception
            );
        }
    }
}
