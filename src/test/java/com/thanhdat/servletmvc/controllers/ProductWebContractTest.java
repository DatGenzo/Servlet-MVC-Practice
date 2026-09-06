package com.thanhdat.servletmvc.controllers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.thanhdat.servletmvc.filters.AdminAuthorizationFilter;

import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;

public class ProductWebContractTest {

    @Test
    public void shouldExposeRequiredPublicProductRoutes() {
        assertServletRoute(HomeController.class, "/home");
        assertServletRoute(ProductListController.class, "/product");
        assertServletRoute(
                ProductDetailController.class,
                "/product/detail"
        );
    }

    @Test
    public void shouldExposeAdminCrudUnderProtectedPath() {
        assertServletRoute(
                ProductAdminListController.class,
                "/admin/products"
        );
        assertServletRoute(
                ProductCreateController.class,
                "/admin/products/create"
        );
        assertServletRoute(
                ProductEditController.class,
                "/admin/products/edit"
        );
        assertServletRoute(
                ProductDeleteController.class,
                "/admin/products/delete"
        );

        WebFilter filter = AdminAuthorizationFilter.class
                .getAnnotation(WebFilter.class);

        assertNotNull(filter);
        assertTrue(
                Arrays.asList(filter.urlPatterns())
                        .contains("/admin/*")
        );
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
                controllerClass.getSimpleName()
                        + " thiếu route "
                        + expectedRoute,
                Arrays.asList(servlet.urlPatterns())
                        .contains(expectedRoute)
        );
    }
}
