package com.thanhdat.servletmvc.services;

import java.util.List;

import com.thanhdat.servletmvc.models.PageResult;
import com.thanhdat.servletmvc.models.Product;

public interface ProductService {

    List<Product> getAll();

    Product getById(int id);

    Product create(Product product);

    Product update(Product product);

    void delete(int id);

    List<Product> getLatest(int limit);

    PageResult<Product> getPage(int requestedPage);

    boolean hasProductsInCategory(int categoryId);
}
