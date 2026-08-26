package com.thanhdat.servletmvc.services.impl;

import java.util.Optional;

import com.thanhdat.servletmvc.daos.UserDao;
import com.thanhdat.servletmvc.daos.impl.UserDaoImpl;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.services.UserService;
import com.thanhdat.servletmvc.utils.PasswordUtils;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl() {
        this(new UserDaoImpl());
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public Optional<User> authenticate(
            String username,
            char[] rawPassword
    ) {
        if (username == null
                || username.isBlank()
                || rawPassword == null
                || rawPassword.length == 0) {
            return Optional.empty();
        }

        String normalizedUsername = username.trim();

        Optional<User> userOptional =
                userDao.findByUsername(normalizedUsername);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        if (!user.isActive()) {
            return Optional.empty();
        }

        boolean passwordMatches = PasswordUtils.matches(
                rawPassword,
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            return Optional.empty();
        }

        /*
         * Controller và Session không cần giữ passwordHash.
         * Xóa thông tin này trước khi trả User ra ngoài.
         */
        user.setPasswordHash(null);

        return Optional.of(user);
    }
}