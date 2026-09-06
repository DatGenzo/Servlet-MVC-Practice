package com.thanhdat.servletmvc.daos;

import java.util.Optional;

import com.thanhdat.servletmvc.models.User;

public interface UserDao {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    int insertInactive(User user);

    boolean activateById(int userId);

    boolean updatePasswordHash(
            int userId,
            String passwordHash
    );
}