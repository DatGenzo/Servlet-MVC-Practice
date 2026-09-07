package com.thanhdat.servletmvc.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.Test;

import com.thanhdat.servletmvc.filters.SessionAuthenticationFilter;
import com.thanhdat.servletmvc.models.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;

public class ProfileSiteMeshContractTest {

    @Test
    public void shouldExposeProtectedMultipartProfileRoutes() {
        assertServletRoute(
                SessionProfileController.class,
                "/session/profile"
        );
        assertServletRoute(
                UserProfileImageController.class,
                "/session/profile/image"
        );

        MultipartConfig multipart =
                SessionProfileController.class.getAnnotation(
                        MultipartConfig.class
                );

        assertNotNull(multipart);
        assertTrue(multipart.maxFileSize() > 0);

        WebFilter filter = SessionAuthenticationFilter.class
                .getAnnotation(WebFilter.class);

        assertNotNull(filter);
        assertTrue(
                Arrays.asList(filter.urlPatterns())
                        .contains("/session/profile/*")
        );
    }

    @Test
    public void shouldMapUserProfileFieldsAsJpaColumns()
            throws NoSuchFieldException {

        assertNotNull(User.class.getAnnotation(Entity.class));

        Column phone = User.class
                .getDeclaredField("phone")
                .getAnnotation(Column.class);

        Column image = User.class
                .getDeclaredField("image")
                .getAnnotation(Column.class);

        assertNotNull(phone);
        assertNotNull(image);
        assertEquals("phone", phone.name());
        assertEquals("images", image.name());
    }

    @Test
    public void shouldConfigureBootstrapDecoratorForProfile()
            throws IOException {

        String pom = read("pom.xml");
        String webXml = read("src/main/webapp/WEB-INF/web.xml");
        String siteMesh = read(
                "src/main/webapp/WEB-INF/sitemesh3.xml"
        );
        String decorator = read(
                "src/main/webapp/WEB-INF/decorators/bootstrap.jsp"
        );
        String profile = read(
                "src/main/webapp/WEB-INF/views/auth/session-profile.jsp"
        );
        String profileController = read(
                "src/main/java/com/thanhdat/servletmvc/controllers/SessionProfileController.java"
        );

        assertTrue(pom.contains("<artifactId>sitemesh</artifactId>"));
        assertTrue(webXml.contains("ConfigurableSiteMeshFilter"));
        assertTrue(webXml.contains("<param-value>include</param-value>"));
        assertTrue(webXml.contains(
                "<url-pattern>/session/profile</url-pattern>"
        ));
        assertFalse(webXml.contains("<dispatcher>FORWARD</dispatcher>"));
        assertTrue(siteMesh.contains("path=\"/session/profile\""));
        assertTrue(siteMesh.contains("bootstrap.jsp"));
        assertTrue(decorator.contains("bootstrap@5.3.3"));
        assertTrue(decorator.contains(
                "<sitemesh:write property=\"body\" />"
        ));
        assertTrue(profile.contains("enctype=\"multipart/form-data\""));
        assertTrue(profileController.contains(
                ").include(request, response);"
        ));
    }

    private void assertServletRoute(
            Class<?> controllerClass,
            String expectedRoute
    ) {
        WebServlet servlet = controllerClass.getAnnotation(
                WebServlet.class
        );

        assertNotNull(servlet);
        assertTrue(
                Arrays.asList(servlet.urlPatterns())
                        .contains(expectedRoute)
        );
    }

    private String read(String relativePath) throws IOException {
        return Files.readString(
                Path.of(relativePath),
                StandardCharsets.UTF_8
        );
    }
}
