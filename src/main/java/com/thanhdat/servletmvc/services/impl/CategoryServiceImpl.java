package com.thanhdat.servletmvc.services.impl;

import java.util.List;
import java.util.Optional;

import com.thanhdat.servletmvc.daos.CategoryDao;
import com.thanhdat.servletmvc.daos.impl.CategoryDaoImpl;
import com.thanhdat.servletmvc.exceptions.DuplicateResourceException;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.services.CategoryService;

public class CategoryServiceImpl
        implements CategoryService {

    private static final int MAX_NAME_LENGTH = 100;

    private final CategoryDao categoryDao;

    public CategoryServiceImpl() {
        this(new CategoryDaoImpl());
    }

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public List<Category> getAll() {
        return categoryDao.findAll();
    }

    @Override
    public List<Category> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return categoryDao.findAll();
        }

        String normalizedKeyword = keyword.trim();

        if (normalizedKeyword.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(
                    "Từ khóa tìm kiếm không được vượt quá "
                            + MAX_NAME_LENGTH
                            + " ký tự."
            );
        }

        return categoryDao.searchByName(
                normalizedKeyword
        );
    }

    @Override
    public Category getById(int id) {
        validateId(id);

        return categoryDao.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Không tìm thấy Category có ID "
                                        + id + "."
                        )
                );
    }

    @Override
    public Category create(Category category) {
        if (category == null) {
            throw new ValidationException(
                    "Dữ liệu Category không hợp lệ."
            );
        }

        String normalizedName =
                normalizeAndValidateName(
                        category.getName()
                );

        ensureNameIsAvailable(normalizedName, null);

        category.setName(normalizedName);

        int generatedId =
                categoryDao.insert(category);

        category.setId(generatedId);

        return category;
    }

    @Override
    public Category update(Category category) {
        if (category == null) {
            throw new ValidationException(
                    "Dữ liệu Category không hợp lệ."
            );
        }

        validateId(category.getId());

        Category existingCategory =
                getById(category.getId());

        String normalizedName =
                normalizeAndValidateName(
                        category.getName()
                );

        ensureNameIsAvailable(
                normalizedName,
                category.getId()
        );

        existingCategory.setName(normalizedName);

        if (category.getIcon() != null
                && !category.getIcon().isBlank()) {
            existingCategory.setIcon(
                    category.getIcon()
            );
        }

        boolean updated =
                categoryDao.update(existingCategory);

        if (!updated) {
            throw new ResourceNotFoundException(
                    "Category không còn tồn tại."
            );
        }

        return existingCategory;
    }

    @Override
    public void delete(int id) {
        validateId(id);

        getById(id);

        boolean deleted =
                categoryDao.deleteById(id);

        if (!deleted) {
            throw new ResourceNotFoundException(
                    "Category không còn tồn tại."
            );
        }
    }

    private void ensureNameIsAvailable(
            String name,
            Integer excludedId
    ) {
        Optional<Category> existingCategory =
                categoryDao.findByName(name);

        if (existingCategory.isEmpty()) {
            return;
        }

        if (excludedId == null
                || existingCategory.get().getId()
                        != excludedId) {

            throw new DuplicateResourceException(
                    "Tên Category đã tồn tại."
            );
        }
    }

    private String normalizeAndValidateName(
            String name
    ) {
        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Tên Category không được để trống."
            );
        }

        String normalizedName = name.trim();

        if (normalizedName.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(
                    "Tên Category không được vượt quá "
                            + MAX_NAME_LENGTH
                            + " ký tự."
            );
        }

        return normalizedName;
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new ValidationException(
                    "ID Category không hợp lệ."
            );
        }
    }
}
