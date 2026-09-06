package com.thanhdat.servletmvc.services.impl;

import java.math.BigDecimal;
import java.util.List;

import com.thanhdat.servletmvc.daos.ProductDao;
import com.thanhdat.servletmvc.daos.impl.ProductDaoImpl;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.models.PageResult;
import com.thanhdat.servletmvc.models.Product;
import com.thanhdat.servletmvc.services.CategoryService;
import com.thanhdat.servletmvc.services.ProductService;

public class ProductServiceImpl implements ProductService {

    public static final int PRODUCTS_PER_PAGE = 6;

    private static final int MAX_NAME_LENGTH = 150;
    private static final int MAX_DESCRIPTION_LENGTH = 2000;

    private static final BigDecimal MAX_PRICE =
            new BigDecimal("9999999999999.99");

    private final ProductDao productDao;
    private final CategoryService categoryService;

    public ProductServiceImpl() {
        this(
                new ProductDaoImpl(),
                new CategoryServiceImpl()
        );
    }

    public ProductServiceImpl(
            ProductDao productDao,
            CategoryService categoryService
    ) {
        this.productDao = productDao;
        this.categoryService = categoryService;
    }

    @Override
    public List<Product> getAll() {
        return productDao.findAll();
    }

    @Override
    public Product getById(int id) {
        validateId(id);

        return productDao.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Không tìm thấy Product có ID "
                                        + id + "."
                        )
                );
    }

    @Override
    public Product create(Product product) {
        validateAndNormalize(product);

        int generatedId = productDao.insert(product);

        if (generatedId <= 0) {
            throw new ResourceNotFoundException(
                    "Category của Product không còn tồn tại."
            );
        }

        product.setId(generatedId);
        return product;
    }

    @Override
    public Product update(Product product) {
        if (product == null) {
            throw new ValidationException(
                    "Dữ liệu Product không hợp lệ."
            );
        }

        validateId(product.getId());

        Product existingProduct = getById(product.getId());

        validateAndNormalize(product);

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDescription(
                product.getDescription()
        );
        existingProduct.setCategory(product.getCategory());

        if (!productDao.update(existingProduct)) {
            throw new ResourceNotFoundException(
                    "Product hoặc Category không còn tồn tại."
            );
        }

        return existingProduct;
    }

    @Override
    public void delete(int id) {
        validateId(id);
        getById(id);

        if (!productDao.deleteById(id)) {
            throw new ResourceNotFoundException(
                    "Product không còn tồn tại."
            );
        }
    }

    @Override
    public List<Product> getLatest(int limit) {
        if (limit <= 0 || limit > 100) {
            throw new ValidationException(
                    "Số lượng Product cần lấy không hợp lệ."
            );
        }

        return productDao.findLatest(limit);
    }

    @Override
    public PageResult<Product> getPage(int requestedPage) {
        int safeRequestedPage = Math.max(1, requestedPage);
        long totalItems = productDao.countAll();

        int totalPages = Math.max(
                1,
                (int) Math.ceil(
                        (double) totalItems
                                / PRODUCTS_PER_PAGE
                )
        );

        int actualPage = Math.min(
                safeRequestedPage,
                totalPages
        );

        int offset = (actualPage - 1)
                * PRODUCTS_PER_PAGE;

        List<Product> products = productDao.findPage(
                offset,
                PRODUCTS_PER_PAGE
        );

        return new PageResult<>(
                products,
                actualPage,
                PRODUCTS_PER_PAGE,
                totalItems,
                totalPages
        );
    }

    @Override
    public boolean hasProductsInCategory(int categoryId) {
        if (categoryId <= 0) {
            throw new ValidationException(
                    "ID Category không hợp lệ."
            );
        }

        return productDao.countByCategoryId(categoryId) > 0;
    }

    private void validateAndNormalize(Product product) {
        if (product == null) {
            throw new ValidationException(
                    "Dữ liệu Product không hợp lệ."
            );
        }

        product.setName(normalizeName(product.getName()));
        validatePrice(product.getPrice());

        if (product.getQuantity() < 0) {
            throw new ValidationException(
                    "Số lượng Product không được âm."
            );
        }

        product.setDescription(
                normalizeDescription(product.getDescription())
        );

        if (product.getCategory() == null) {
            throw new ValidationException(
                    "Bạn phải chọn Category cho Product."
            );
        }

        int categoryId = product.getCategory().getId();

        if (categoryId <= 0) {
            throw new ValidationException(
                    "Category của Product không hợp lệ."
            );
        }

        Category category = categoryService.getById(categoryId);
        product.setCategory(category);
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new ValidationException(
                    "Tên Product không được để trống."
            );
        }

        String normalized = name.trim();

        if (normalized.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(
                    "Tên Product không được vượt quá "
                            + MAX_NAME_LENGTH
                            + " ký tự."
            );
        }

        return normalized;
    }

    private void validatePrice(BigDecimal price) {
        if (price == null) {
            throw new ValidationException(
                    "Giá Product không được để trống."
            );
        }

        if (price.signum() < 0) {
            throw new ValidationException(
                    "Giá Product không được âm."
            );
        }

        if (price.scale() > 2 || price.compareTo(MAX_PRICE) > 0) {
            throw new ValidationException(
                    "Giá Product không hợp lệ; tối đa 2 chữ số thập phân."
            );
        }
    }

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        String normalized = description.trim();

        if (normalized.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ValidationException(
                    "Mô tả Product không được vượt quá "
                            + MAX_DESCRIPTION_LENGTH
                            + " ký tự."
            );
        }

        return normalized;
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new ValidationException(
                    "ID Product không hợp lệ."
            );
        }
    }
}
