package com.thanhdat.servletmvc.daos.impl;

import java.util.Optional;

import com.thanhdat.servletmvc.config.JpaConfig;
import com.thanhdat.servletmvc.daos.UserProfileDao;
import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class UserProfileDaoImpl implements UserProfileDao {

    @Override
    public Optional<User> findById(int userId) {
        EntityManager entityManager =
                JpaConfig.getEntityManager();

        try {
            return Optional.ofNullable(
                    entityManager.find(User.class, userId)
            );
        } catch (RuntimeException exception) {
            throw dataAccess(
                    "Không thể tải Profile bằng JPA.",
                    exception
            );
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Optional<User> updateProfile(
            int userId,
            String fullName,
            String phone,
            String image
    ) {
        EntityManager entityManager =
                JpaConfig.getEntityManager();

        EntityTransaction transaction =
                entityManager.getTransaction();

        try {
            transaction.begin();

            User user = entityManager.find(
                    User.class,
                    userId
            );

            if (user == null) {
                transaction.rollback();
                return Optional.empty();
            }

            user.setFullName(fullName);
            user.setPhone(phone);
            user.setImage(image);

            entityManager.flush();
            entityManager.refresh(user);
            transaction.commit();

            return Optional.of(user);
        } catch (RuntimeException exception) {
            rollback(transaction);

            throw dataAccess(
                    "Không thể cập nhật Profile bằng JPA.",
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

    private DataAccessException dataAccess(
            String message,
            RuntimeException cause
    ) {
        return new DataAccessException(message, cause);
    }
}
