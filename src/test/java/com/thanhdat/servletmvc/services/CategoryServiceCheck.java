package com.thanhdat.servletmvc.services;

import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.services.impl.CategoryServiceImpl;

public final class CategoryServiceCheck {

    private CategoryServiceCheck() {
    }

    public static void main(String[] args) {
        CategoryService categoryService =
                new CategoryServiceImpl();

        Category createdCategory = null;

        try {
            String uniqueName =
                    "Category Test "
                            + System.currentTimeMillis();

            createdCategory = categoryService.create(
                    new Category(uniqueName, null)
            );

            System.out.println(
                    "CREATE OK: " + createdCategory
            );

            Category foundCategory =
                    categoryService.getById(
                            createdCategory.getId()
                    );

            System.out.println(
                    "READ OK: " + foundCategory
            );

            foundCategory.setName(
                    uniqueName + " Updated"
            );

            Category updatedCategory =
                    categoryService.update(
                            foundCategory
                    );

            System.out.println(
                    "UPDATE OK: " + updatedCategory
            );

            int searchResultCount =
                    categoryService.search("Updated")
                            .size();

            System.out.println(
                    "SEARCH OK: "
                            + searchResultCount
                            + " kết quả"
            );

            categoryService.delete(
                    updatedCategory.getId()
            );

            createdCategory = null;

            System.out.println("DELETE OK");
            System.out.println(
                    "CATEGORY SERVICE CHECK PASSED"
            );
        } finally {
            /*
             * Nếu test lỗi giữa chừng, cố gắng xóa
             * Category do test vừa tạo.
             */
            if (createdCategory != null
                    && createdCategory.getId() > 0) {
                try {
                    categoryService.delete(
                            createdCategory.getId()
                    );
                } catch (RuntimeException exception) {
                    System.err.println(
                            "Không thể dọn Category test: "
                                    + exception.getMessage()
                    );
                }
            }
        }
    }
}