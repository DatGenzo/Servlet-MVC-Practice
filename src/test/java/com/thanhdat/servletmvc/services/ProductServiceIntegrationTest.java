package com.thanhdat.servletmvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.models.PageResult;
import com.thanhdat.servletmvc.models.Product;
import com.thanhdat.servletmvc.services.impl.CategoryServiceImpl;
import com.thanhdat.servletmvc.services.impl.ProductServiceImpl;

public class ProductServiceIntegrationTest {

    @Test
    public void shouldCrudQueryLatestPageAndDetailProduct() {
        CategoryService categoryService =
                new CategoryServiceImpl();

        ProductService productService =
                new ProductServiceImpl();

        List<Category> categories = categoryService.getAll();

        assertFalse(categories.isEmpty());

        Category category = categories.get(0);
        long suffix = System.nanoTime();

        Product product = new Product();
        product.setName("Product Integration " + suffix);
        product.setPrice(new BigDecimal("125000.50"));
        product.setQuantity(7);
        product.setDescription("Product được tạo bởi integration test.");
        product.setCategory(category);

        int testProductId = 0;

        try {
            Product created = productService.create(product);
            int createdProductId = created.getId();
            testProductId = createdProductId;

            assertTrue(testProductId > 0);

            Product detail = productService.getById(
                    testProductId
            );

            assertEquals(product.getName(), detail.getName());
            assertEquals(
                    0,
                    product.getPrice().compareTo(detail.getPrice())
            );
            assertEquals(7, detail.getQuantity());
            assertEquals(category.getId(), detail.getCategory().getId());
            assertNotNull(detail.getCategory().getName());
            assertNotNull(detail.getCreatedAt());

            Product submittedUpdate = new Product();
            submittedUpdate.setId(testProductId);
            submittedUpdate.setName(
                    "Product Updated " + suffix
            );
            submittedUpdate.setPrice(
                    new BigDecimal("150000.00")
            );
            submittedUpdate.setQuantity(11);
            submittedUpdate.setDescription(
                    "Mô tả Product sau cập nhật."
            );
            submittedUpdate.setCategory(category);

            productService.update(submittedUpdate);

            Product updated = productService.getById(
                    testProductId
            );

            assertEquals(submittedUpdate.getName(), updated.getName());
            assertEquals(
                    0,
                    submittedUpdate.getPrice().compareTo(
                            updated.getPrice()
                    )
            );
            assertEquals(11, updated.getQuantity());
            assertEquals(
                    submittedUpdate.getDescription(),
                    updated.getDescription()
            );

            List<Product> latest = productService.getLatest(10);

            assertEquals(10, latest.size());
            assertTrue(
                    latest.stream().anyMatch(
                            item -> item.getId() == createdProductId
                    )
            );
            assertNewestOrder(latest);

            PageResult<Product> firstPage =
                    productService.getPage(1);

            assertEquals(1, firstPage.getPage());
            assertEquals(6, firstPage.getPageSize());
            assertEquals(6, firstPage.getItems().size());
            assertTrue(firstPage.getTotalItems() >= 14);
            assertTrue(firstPage.getTotalPages() >= 3);
            assertTrue(firstPage.isHasNext());
            assertFalse(firstPage.isHasPrevious());
            assertNewestOrder(firstPage.getItems());

            PageResult<Product> lastPage =
                    productService.getPage(Integer.MAX_VALUE);

            assertEquals(
                    lastPage.getTotalPages(),
                    lastPage.getPage()
            );
            assertTrue(lastPage.getItems().size() <= 6);
            assertFalse(lastPage.isHasNext());

            productService.delete(testProductId);
            testProductId = 0;

            try {
                productService.getById(created.getId());
                fail("Product đã xóa vẫn còn được tìm thấy.");
            } catch (ResourceNotFoundException expected) {
                assertNotNull(expected.getMessage());
            }
        } finally {
            if (testProductId > 0) {
                productService.delete(testProductId);
            }
        }
    }

    private void assertNewestOrder(List<Product> products) {
        for (int index = 1; index < products.size(); index++) {
            Product previous = products.get(index - 1);
            Product current = products.get(index);

            LocalDateTime previousCreatedAt =
                    previous.getCreatedAt();

            LocalDateTime currentCreatedAt =
                    current.getCreatedAt();

            assertNotNull(previousCreatedAt);
            assertNotNull(currentCreatedAt);

            int timeComparison = previousCreatedAt.compareTo(
                    currentCreatedAt
            );

            assertTrue(
                    timeComparison > 0
                            || (timeComparison == 0
                            && previous.getId() > current.getId())
            );
        }
    }
}
