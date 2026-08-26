package com.thanhdat.servletmvc.services;

import java.util.List;

import com.thanhdat.servletmvc.models.Category;

public interface CategoryService {

    List<Category> getAll();

    List<Category> search(String keyword);

    Category getById(int id);

    Category create(Category category);

    Category update(Category category);

    void delete(int id);
}