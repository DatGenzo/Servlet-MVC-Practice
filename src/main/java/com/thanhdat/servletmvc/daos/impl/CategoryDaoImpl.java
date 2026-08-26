package com.thanhdat.servletmvc.daos.impl;

import java.util.List;
import java.util.Optional;

import com.thanhdat.servletmvc.config.JpaConfig;
import com.thanhdat.servletmvc.daos.CategoryDao;
import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.Category;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public List<Category> findAll() {
        EntityManager entityManager = JpaConfig.getEntityManager();

        try {
            return entityManager
                .createNamedQuery("Category.findAll", Category.class)
                .getResultList();

        } catch (RuntimeException exception) {
            throw new DataAccessException(
                "Không thể lấy danh sách Category bằng JPA.",
                exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Optional<Category> findById(int id) {
        EntityManager entityManager = JpaConfig.getEntityManager();

        try {
            Category category = entityManager.find(
                Category.class,
                id
            );

            return Optional.ofNullable(category);

        } catch (RuntimeException exception) {
            throw new DataAccessException(
                "Không thể tìm Category theo ID bằng JPA.",
                exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Optional<Category> findByName(String name) {
        EntityManager entityManager = JpaConfig.getEntityManager();

        try {
            List<Category> categories = entityManager
                .createQuery(
                    """
                    SELECT c
                    FROM Category c
                    WHERE LOWER(c.name) = LOWER(:name)
                    """,
                    Category.class
                )
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();

            if (categories.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(categories.get(0));

        } catch (RuntimeException exception) {
            throw new DataAccessException(
                "Không thể tìm Category theo tên bằng JPA.",
                exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public List<Category> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }

        EntityManager entityManager = JpaConfig.getEntityManager();

        try {
            return entityManager
                .createQuery(
                    """
                    SELECT c
                    FROM Category c
                    WHERE LOWER(c.name) LIKE LOWER(:keyword)
                    ORDER BY c.id
                    """,
                    Category.class
                )
                .setParameter(
                    "keyword",
                    "%" + keyword.trim() + "%"
                )
                .getResultList();

        } catch (RuntimeException exception) {
            throw new DataAccessException(
                "Không thể tìm kiếm Category bằng JPA.",
                exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public int insert(Category category) {
        EntityManager entityManager = JpaConfig.getEntityManager();
        EntityTransaction transaction =
            entityManager.getTransaction();

        try {
            transaction.begin();

            entityManager.persist(category);
            entityManager.flush();

            transaction.commit();

            return category.getId();

        } catch (RuntimeException exception) {
            rollback(transaction);

            throw new DataAccessException(
                "Không thể thêm Category bằng JPA.",
                exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public boolean update(Category category) {
        EntityManager entityManager = JpaConfig.getEntityManager();
        EntityTransaction transaction =
            entityManager.getTransaction();

        try {
            transaction.begin();

            Category managedCategory = entityManager.find(
                Category.class,
                category.getId()
            );

            if (managedCategory == null) {
                transaction.rollback();
                return false;
            }

            managedCategory.setName(category.getName());
            managedCategory.setIcon(category.getIcon());

            entityManager.flush();
            transaction.commit();

            return true;

        } catch (RuntimeException exception) {
            rollback(transaction);

            throw new DataAccessException(
                "Không thể cập nhật Category bằng JPA.",
                exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public boolean deleteById(int id) {
        EntityManager entityManager = JpaConfig.getEntityManager();
        EntityTransaction transaction =
            entityManager.getTransaction();

        try {
            transaction.begin();

            Category category = entityManager.find(
                Category.class,
                id
            );

            if (category == null) {
                transaction.rollback();
                return false;
            }

            entityManager.remove(category);
            entityManager.flush();

            transaction.commit();

            return true;

        } catch (RuntimeException exception) {
            rollback(transaction);

            throw new DataAccessException(
                "Không thể xóa Category bằng JPA.",
                exception
            );
        } finally {
            entityManager.close();
        }
    }

    private void rollback(EntityTransaction transaction) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
    }
}