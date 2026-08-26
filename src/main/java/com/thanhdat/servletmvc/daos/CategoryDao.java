package com.thanhdat.servletmvc.daos;

import java.util.List;
import java.util.Optional;

import com.thanhdat.servletmvc.models.Category;

public interface CategoryDao {

    List<Category> findAll();

    Optional<Category> findById(int id);

    Optional<Category> findByName(String name);

    List<Category> searchByName(String keyword);

    int insert(Category category);

    boolean update(Category category);

    boolean deleteById(int id);
}