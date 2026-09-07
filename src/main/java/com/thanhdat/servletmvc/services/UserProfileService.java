package com.thanhdat.servletmvc.services;

import com.thanhdat.servletmvc.models.User;

public interface UserProfileService {

    User getById(int userId);

    User updateProfile(
            int userId,
            String fullName,
            String phone,
            String image
    );
}
