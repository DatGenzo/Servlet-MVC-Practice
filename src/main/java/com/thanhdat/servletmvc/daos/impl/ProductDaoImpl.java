package com.thanhdat.servletmvc.daos.impl;

import java.util.List;
import java.util.Optional;

import com.thanhdat.servletmvc.config.JpaConfig;
import com.thanhdat.servletmvc.daos.ProductDao;
import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.Category;
import com.thanhdat.servletmvc.models.Product;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ProductDaoImpl implements ProductDao {

    private static final String SELECT_WITH_CATEGORY = """
            SELECT p
            FROM Product p
            JOIN FETCH p.category
            """;

    private static final String NEWEST_ORDER = """
            ORDER BY p.createdAt DESC, p.id DESC
            """;

    @Override
    public List<Product> findAll() {
        EntityManager entityManager =
                JpaConfig.getEntityManager();

        try {
            return entityManager.createQuery(
                    SELECT_WITH_CATEGORY + NEWEST_ORDER,
                    Product.class
            ).getResultList();
        } catch (RuntimeException exception) {
            throw dataAccess(
                    "Không thể lấy danh sách Product.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Optional<Product> findById(int id) {
        EntityManager entityManager =
                JpaConfig.getEntityManager();

        try {
            List<Product> products = entityManager
                    .createQuery(
                            SELECT_WITH_CATEGORY
                                    + "WHERE p.id = :id",
                            Product.class
                    )
                    .setParameter("id", id)
                    .setMaxResults(1)
                    .getResultList();

            return products.isEmpty()
                    ? Optional.empty()
                    : Optional.of(products.get(0));
        } catch (RuntimeException exception) {
            throw dataAccess(
                    "Không thể tìm Product theo ID.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public int insert(Product product) {
        EntityManager entityManager =
                JpaConfig.getEntityManager();

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            Category managedCategory = entityManager.find(
                    Category.class,
                    product.getCategory().getId()
            );

            if (managedCategory == null) {
                transaction.rollback();
                return 0;
            }

            product.setCategory(managedCategory);

            entityManager.persist(product);
            entityManager.flush();
            transaction.commit();

            return product.getId();
        } catch (RuntimeException exception) {
            rollback(transaction);

            throw dataAccess(
                    "Không thể thêm Product.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public boolean update(Product product) {
        EntityManager entityManager =
                JpaConfig.getEntityManager();

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            Product managedProduct = entityManager.find(
                    Product.class,
                    product.getId()
            );

            Category managedCategory = entityManager.find(
                    Category.class,
                    product.getCategory().getId()
            );

            if (managedProduct == null
                    || managedCategory == null) {
                transaction.rollback();
                return false;
            }

            managedProduct.setName(product.getName());
            managedProduct.setPrice(product.getPrice());
            managedProduct.setQuantity(product.getQuantity());
            managedProduct.setDescription(
                    product.getDescription()
            );
            managedProduct.setCategory(managedCategory);

            entityManager.flush();
            transaction.commit();

            return true;
        } catch (RuntimeException exception) {
            rollback(transaction);

            throw dataAccess(
                    "Không thể cập nhật Product.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public boolean deleteById(int id) {
        EntityManager entityManager =
                JpaConfig.getEntityManager();

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            Product product = entityManager.find(
                    Product.class,
                    id
            );

            if (product == null) {
                transaction.rollback();
                return false;
            }

            entityManager.remove(product);
            entityManager.flush();
            transaction.commit();

            return true;
        } catch (RuntimeException exception) {
            rollback(transaction);

            throw dataAccess(
                    "Không thể xóa Product.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Product> findLatest(int limit) {
        if (limit <= 0) {
            return List.of();
        }

        EntityManager entityManager =
                JpaConfig.getEntityManager();

        try {
            return entityManager
                    .createQuery(
                            SELECT_WITH_CATEGORY + NEWEST_ORDER,
                            Product.class
                    )
                    .setMaxResults(limit)
                    .getResultList();
        } catch (RuntimeException exception) {
            throw dataAccess(
                    "Không thể lấy Product mới nhất.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Product> findPage(
            int offset,
            int limit
    ) {
        if (offset < 0 || limit <= 0) {
            return List.of();
        }

        EntityManager entityManager =
                JpaConfig.getEntityManager();

        try {
            return entityManager
                    .createQuery(
                            SELECT_WITH_CATEGORY + NEWEST_ORDER,
                            Product.class
                    )
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        } catch (RuntimeException exception) {
            throw dataAccess(
                    "Không thể phân trang Product.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public long countAll() {
        EntityManager entityManager =
                JpaConfig.getEntityManager();

        try {
            return entityManager.createQuery(
                    "SELECT COUNT(p) FROM Product p",
                    Long.class
            ).getSingleResult();
        } catch (RuntimeException exception) {
            throw dataAccess(
                    "Không thể đếm Product.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public long countByCategoryId(int categoryId) {
        EntityManager entityManager =
                JpaConfig.getEntityManager();

        try {
            return entityManager.createQuery(
                    """
                    SELECT COUNT(p)
                    FROM Product p
                    WHERE p.category.id = :categoryId
                    """,
                    Long.class
            )
                    .setParameter("categoryId", categoryId)
                    .getSingleResult();
        } catch (RuntimeException exception) {
            throw dataAccess(
                    "Không thể đếm Product theo Category.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    private DataAccessException dataAccess(
            String message,
            RuntimeException cause
    ) {
        return new DataAccessException(message, cause);
    }

    private void rollback(EntityTransaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}
