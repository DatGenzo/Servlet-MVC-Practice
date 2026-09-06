package com.thanhdat.servletmvc.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.models.Product;
import com.thanhdat.servletmvc.services.impl.CategoryServiceImpl;
import com.thanhdat.servletmvc.services.impl.ProductServiceImpl;

public class CategoryProductConstraintIntegrationTest {

    @Test
    public void shouldProtectCategoryReferencedByProduct() {
        CategoryService categoryService =
                new CategoryServiceImpl();

        ProductService productService =
                new ProductServiceImpl();

        Category category = new Category();
        category.setName(
                "Category Constraint " + System.nanoTime()
        );

        int testCategoryId = 0;
        int testProductId = 0;

        try {
            Category createdCategory =
                    categoryService.create(category);

            testCategoryId = createdCategory.getId();

            assertFalse(
                    productService.hasProductsInCategory(
                            testCategoryId
                    )
            );

            Product product = new Product();
            product.setName(
                    "Constraint Product " + System.nanoTime()
            );
            product.setPrice(new BigDecimal("1000.00"));
            product.setQuantity(1);
            product.setDescription(
                    "Dùng để kiểm tra foreign key Category-Product."
            );
            product.setCategory(createdCategory);

            Product createdProduct =
                    productService.create(product);

            testProductId = createdProduct.getId();

            assertTrue(
                    productService.hasProductsInCategory(
                            testCategoryId
                    )
            );

            try {
                categoryService.delete(testCategoryId);
                fail("Category đang có Product vẫn bị xóa.");
            } catch (DataAccessException expected) {
                assertNotNull(expected.getMessage());
            }

            productService.delete(testProductId);
            testProductId = 0;

            assertFalse(
                    productService.hasProductsInCategory(
                            testCategoryId
                    )
            );

            categoryService.delete(testCategoryId);
            testCategoryId = 0;
        } finally {
            if (testProductId > 0) {
                try {
                    productService.delete(testProductId);
                } catch (ResourceNotFoundException ignored) {
                    // Test cleanup idempotent.
                }
            }

            if (testCategoryId > 0) {
                try {
                    categoryService.delete(testCategoryId);
                } catch (ResourceNotFoundException ignored) {
                    // Test cleanup idempotent.
                }
            }
        }
    }
}
