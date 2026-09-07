package com.thanhdat.servletmvc.daos;

import java.util.Optional;

import com.thanhdat.servletmvc.models.User;

public interface UserProfileDao {

    Optional<User> findById(int userId);

    Optional<User> updateProfile(
            int userId,
            String fullName,
            String phone,
            String image
    );
}
