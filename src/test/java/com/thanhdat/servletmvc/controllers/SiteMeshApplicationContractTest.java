package com.thanhdat.servletmvc.controllers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class SiteMeshApplicationContractTest {

    private static final Path CONTROLLERS = Path.of(
            "src/main/java/com/thanhdat/servletmvc/controllers"
    );

    @Test
    public void shouldDecorateApplicationPagesAndExcludeResources()
            throws IOException {

        String webXml = read("src/main/webapp/WEB-INF/web.xml");
        String siteMesh = read(
                "src/main/webapp/WEB-INF/sitemesh3.xml"
        );
        String decorator = read(
                "src/main/webapp/WEB-INF/decorators/bootstrap.jsp"
        );

        assertTrue(webXml.contains(
                "<url-pattern>/*</url-pattern>"
        ));
        assertTrue(webXml.contains("<dispatcher>REQUEST</dispatcher>"));
        assertFalse(webXml.contains("<dispatcher>FORWARD</dispatcher>"));

        assertTrue(siteMesh.contains("path=\"/*\""));
        assertTrue(siteMesh.contains(
                "path=\"/assets/*\" exclude=\"true\""
        ));
        assertTrue(siteMesh.contains(
                "path=\"/admin/category-icons\" exclude=\"true\""
        ));
        assertTrue(siteMesh.contains(
                "path=\"/session/profile/image\" exclude=\"true\""
        ));

        assertTrue(decorator.contains("bootstrap@5.3.3"));
        assertTrue(decorator.contains(
                "<sitemesh:write property=\"head\" />"
        ));
        assertTrue(decorator.contains(
                "<sitemesh:write property=\"body\" />"
        ));
        assertTrue(decorator.contains("sitemesh-page"));
    }

    @Test
    public void shouldRenderControllerViewsIntoSiteMeshBuffer()
            throws IOException {

        List<String> pageControllers = List.of(
                "AccountActivationController.java",
                "ActivationResendController.java",
                "CategoryCreateController.java",
                "CategoryEditController.java",
                "CategoryListController.java",
                "CookieLoginController.java",
                "CookieProfileController.java",
                "ForgotPasswordController.java",
                "HomeController.java",
                "PasswordResetOtpController.java",
                "PasswordResetResendController.java",
                "ProductAdminListController.java",
                "ProductCreateController.java",
                "ProductDetailController.java",
                "ProductEditController.java",
                "ProductListController.java",
                "RegisterController.java",
                "ResetPasswordController.java",
                "SessionLoginController.java",
                "SessionProfileController.java"
        );

        for (String fileName : pageControllers) {
            String source = Files.readString(
                    CONTROLLERS.resolve(fileName),
                    StandardCharsets.UTF_8
            );

            assertTrue(
                    fileName + " phải render JSP bằng include",
                    source.contains(").include(request, response);")
            );
            assertFalse(
                    fileName + " không được forward khi SiteMesh wrap REQUEST",
                    source.contains(").forward(request, response);")
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
