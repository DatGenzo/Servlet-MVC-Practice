package com.thanhdat.servletmvc.daos;

import java.util.Optional;
import com.thanhdat.servletmvc.models.User;

public interface UserDao {
    Optional<User> findByUsername(String username);
}