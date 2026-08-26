package com.thanhdat.servletmvc.services;

import java.util.Optional;

import com.thanhdat.servletmvc.models.User;

public interface UserService {
    Optional<User> authenticate(
            String username,
            char[] rawPassword
    );
}