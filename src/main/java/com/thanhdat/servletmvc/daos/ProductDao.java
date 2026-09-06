package com.thanhdat.servletmvc.daos;

import java.util.List;
import java.util.Optional;

import com.thanhdat.servletmvc.models.Product;

public interface ProductDao {

    List<Product> findAll();

    Optional<Product> findById(int id);

    int insert(Product product);

    boolean update(Product product);

    boolean deleteById(int id);

    List<Product> findLatest(int limit);

    List<Product> findPage(int offset, int limit);

    long countAll();

    long countByCategoryId(int categoryId);
}
