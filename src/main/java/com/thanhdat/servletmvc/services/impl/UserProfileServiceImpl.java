package com.thanhdat.servletmvc.services.impl;

import java.util.Optional;
import java.util.regex.Pattern;

import com.thanhdat.servletmvc.daos.UserProfileDao;
import com.thanhdat.servletmvc.daos.impl.UserProfileDaoImpl;
import com.thanhdat.servletmvc.exceptions.ResourceNotFoundException;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.services.UserProfileService;

public class UserProfileServiceImpl
        implements UserProfileService {

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{8,15}$");

    private final UserProfileDao userProfileDao;

    public UserProfileServiceImpl() {
        this(new UserProfileDaoImpl());
    }

    public UserProfileServiceImpl(
            UserProfileDao userProfileDao
    ) {
        this.userProfileDao = userProfileDao;
    }

    @Override
    public User getById(int userId) {
        validateUserId(userId);

        User user = userProfileDao.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Không tìm thấy tài khoản."
                        )
                );

        return sanitize(user);
    }

    @Override
    public User updateProfile(
            int userId,
            String fullName,
            String phone,
            String image
    ) {
        validateUserId(userId);

        String normalizedFullName =
                normalizeFullName(fullName);

        String normalizedPhone = normalizePhone(phone);
        String normalizedImage = normalizeImage(image);

        Optional<User> updated = userProfileDao.updateProfile(
                userId,
                normalizedFullName,
                normalizedPhone,
                normalizedImage
        );

        User user = updated.orElseThrow(() ->
                new ResourceNotFoundException(
                        "Không tìm thấy tài khoản."
                )
        );

        return sanitize(user);
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new ValidationException(
                    "Phiên đăng nhập không hợp lệ."
            );
        }
    }

    private String normalizeFullName(String fullName) {
        if (fullName == null) {
            throw new ValidationException(
                    "Họ tên không được để trống."
            );
        }

        String normalized = fullName.trim()
                .replaceAll("\\s+", " ");

        if (normalized.length() < 2
                || normalized.length() > 100) {
            throw new ValidationException(
                    "Họ tên phải dài từ 2 đến 100 ký tự."
            );
        }

        return normalized;
    }

    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }

        String normalized = phone.trim()
                .replaceAll("[\\s.-]", "");

        if (!PHONE_PATTERN.matcher(normalized).matches()) {
            throw new ValidationException(
                    "Số điện thoại phải gồm 8-15 chữ số và có thể bắt đầu bằng dấu +."
            );
        }

        return normalized;
    }

    private String normalizeImage(String image) {
        if (image == null || image.isBlank()) {
            return null;
        }

        String normalized = image.trim();

        if (normalized.length() > 255
                || !normalized.startsWith("profiles/")) {
            throw new ValidationException(
                    "Đường dẫn ảnh Profile không hợp lệ."
            );
        }

        return normalized;
    }

    private User sanitize(User user) {
        user.setPasswordHash(null);
        return user;
    }
}
