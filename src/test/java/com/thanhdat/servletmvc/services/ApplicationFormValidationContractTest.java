package com.thanhdat.servletmvc.services;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class ApplicationFormValidationContractTest {

    @Test
    public void shouldDeclareBrowserConstraintsForAuthForms()
            throws IOException {

        String register = read(
                "src/main/webapp/WEB-INF/views/auth/register.jsp"
        );
        String sessionLogin = read(
                "src/main/webapp/WEB-INF/views/auth/session-login.jsp"
        );
        String cookieLogin = read(
                "src/main/webapp/WEB-INF/views/auth/cookie-login.jsp"
        );
        String activation = read(
                "src/main/webapp/WEB-INF/views/auth/activate-account.jsp"
        );
        String forgotPassword = read(
                "src/main/webapp/WEB-INF/views/auth/forgot-password.jsp"
        );
        String verifyReset = read(
                "src/main/webapp/WEB-INF/views/auth/verify-reset-otp.jsp"
        );
        String resetPassword = read(
                "src/main/webapp/WEB-INF/views/auth/reset-password.jsp"
        );

        assertContains(register,
                "pattern=\"[A-Za-z0-9._-]{3,50}\"",
                "minlength=\"2\"",
                "type=\"email\"",
                "maxlength=\"72\"");

        assertContains(sessionLogin,
                "minlength=\"3\"",
                "maxlength=\"50\"",
                "maxlength=\"72\"");

        assertContains(cookieLogin,
                "minlength=\"3\"",
                "maxlength=\"50\"",
                "maxlength=\"72\"");

        assertContains(activation,
                "pattern=\"[0-9]{6}\"",
                "maxlength=\"6\"");

        assertContains(forgotPassword,
                "type=\"email\"",
                "maxlength=\"100\"");

        assertContains(verifyReset,
                "pattern=\"[0-9]{6}\"",
                "maxlength=\"6\"");

        assertContains(resetPassword,
                "minlength=\"8\"",
                "maxlength=\"72\"");
    }

    @Test
    public void shouldDeclareBrowserConstraintsForDataForms()
            throws IOException {

        String categoryForm = read(
                "src/main/webapp/WEB-INF/views/admin/categories/form.jsp"
        );
        String categoryList = read(
                "src/main/webapp/WEB-INF/views/admin/categories/list.jsp"
        );
        String productForm = read(
                "src/main/webapp/WEB-INF/views/admin/products/form.jsp"
        );
        String profile = read(
                "src/main/webapp/WEB-INF/views/auth/session-profile.jsp"
        );

        assertContains(categoryForm,
                "maxlength=\"100\"",
                "accept=\".jpg,.jpeg,.png,image/jpeg,image/png\"");

        assertContains(categoryList,
                "name=\"keyword\"",
                "maxlength=\"100\"");

        assertContains(productForm,
                "maxlength=\"150\"",
                "min=\"0\"",
                "max=\"9999999999999.99\"",
                "step=\"0.01\"",
                "max=\"2147483647\"",
                "maxlength=\"2000\"");

        assertContains(profile,
                "minlength=\"2\"",
                "maxlength=\"100\"",
                "pattern=\"\\+?[0-9 .-]{8,20}\"",
                "accept=\"image/jpeg,image/png\"");
    }

    @Test
    public void shouldKeepServerSideValidationForEveryFormGroup()
            throws IOException {

        String userService = read(
                "src/main/java/com/thanhdat/servletmvc/services/impl/UserServiceImpl.java"
        );
        String otpService = read(
                "src/main/java/com/thanhdat/servletmvc/services/impl/OtpServiceImpl.java"
        );
        String categoryService = read(
                "src/main/java/com/thanhdat/servletmvc/services/impl/CategoryServiceImpl.java"
        );
        String productService = read(
                "src/main/java/com/thanhdat/servletmvc/services/impl/ProductServiceImpl.java"
        );
        String profileService = read(
                "src/main/java/com/thanhdat/servletmvc/services/impl/UserProfileServiceImpl.java"
        );
        String requestUtils = read(
                "src/main/java/com/thanhdat/servletmvc/utils/RequestUtils.java"
        );
        String storage = read(
                "src/main/java/com/thanhdat/servletmvc/services/impl/LocalFileStorageService.java"
        );
        String categoryListController = read(
                "src/main/java/com/thanhdat/servletmvc/controllers/CategoryListController.java"
        );
        String categoryEditController = read(
                "src/main/java/com/thanhdat/servletmvc/controllers/CategoryEditController.java"
        );
        String categoryDeleteController = read(
                "src/main/java/com/thanhdat/servletmvc/controllers/CategoryDeleteController.java"
        );
        String productEditController = read(
                "src/main/java/com/thanhdat/servletmvc/controllers/ProductEditController.java"
        );
        String productDeleteController = read(
                "src/main/java/com/thanhdat/servletmvc/controllers/ProductDeleteController.java"
        );

        assertContains(userService,
                "USERNAME_PATTERN.matcher",
                "rawPassword.length > MAX_PASSWORD_LENGTH",
                "EMAIL_PATTERN.matcher",
                "Arrays.equals");

        assertContains(otpService,
                "matches(\"[0-9]{6}\")",
                "MAX_ATTEMPTS",
                "OTP_EXPIRY_MINUTES");

        assertContains(categoryService,
                "normalizedKeyword.length() > MAX_NAME_LENGTH",
                "normalizeAndValidateName");

        assertContains(productService,
                "validateAndNormalize",
                "price.signum() < 0",
                "MAX_DESCRIPTION_LENGTH",
                "categoryService.getById");

        assertContains(profileService,
                "PHONE_PATTERN.matcher",
                "normalizeFullName",
                "normalizeImage");

        assertContains(requestUtils,
                "getPositiveIntParameter",
                "value <= 0");

        assertContains(storage,
                "MAX_CATEGORY_ICON_BYTES",
                "MAX_PROFILE_IMAGE_BYTES",
                "detectImageExtension");

        assertContains(categoryListController,
                "catch (ValidationException exception)",
                "request.setAttribute(\"alert\", exception.getMessage())");

        assertContains(categoryEditController,
                "RequestUtils.getPositiveIntParameter");
        assertContains(categoryDeleteController,
                "RequestUtils.getPositiveIntParameter");
        assertContains(productEditController,
                "RequestUtils.getPositiveIntParameter");
        assertContains(productDeleteController,
                "RequestUtils.getPositiveIntParameter");
    }

    private void assertContains(
            String source,
            String... expectedFragments
    ) {
        for (String fragment : expectedFragments) {
            assertTrue(
                    "Thiếu validation contract: " + fragment,
                    source.contains(fragment)
            );
        }
    }

    private String read(String relativePath) throws IOException {
        return Files.readString(
                Path.of(relativePath),
                StandardCharsets.UTF_8
        );
    }
}
