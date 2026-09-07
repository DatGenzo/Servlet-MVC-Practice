package com.thanhdat.servletmvc.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.junit.Test;

import com.thanhdat.servletmvc.daos.UserProfileDao;
import com.thanhdat.servletmvc.exceptions.ValidationException;
import com.thanhdat.servletmvc.models.User;
import com.thanhdat.servletmvc.services.impl.UserProfileServiceImpl;

public class UserProfileValidationTest {

    @Test
    public void shouldNormalizeAndUpdateValidProfile() {
        FakeUserProfileDao dao = new FakeUserProfileDao();
        UserProfileService service =
                new UserProfileServiceImpl(dao);

        User updated = service.updateProfile(
                7,
                "  Nguyễn   Văn A  ",
                "+84 901.234.567",
                "profiles/7/avatar.png"
        );

        assertEquals("Nguyễn Văn A", updated.getFullName());
        assertEquals("+84901234567", updated.getPhone());
        assertEquals(
                "profiles/7/avatar.png",
                updated.getImage()
        );
        assertNull(updated.getPasswordHash());
    }

    @Test
    public void shouldRejectInvalidFullNameAndPhone() {
        UserProfileService service =
                new UserProfileServiceImpl(
                        new FakeUserProfileDao()
                );

        assertValidation(() -> service.updateProfile(
                7,
                "A",
                "0901234567",
                null
        ));

        assertValidation(() -> service.updateProfile(
                7,
                "Nguyễn Văn A",
                "phone-invalid",
                null
        ));
    }

    @Test
    public void shouldRejectImageOutsideProfileDirectory() {
        UserProfileService service =
                new UserProfileServiceImpl(
                        new FakeUserProfileDao()
                );

        assertValidation(() -> service.updateProfile(
                7,
                "Nguyễn Văn A",
                null,
                "../outside.png"
        ));
    }

    private void assertValidation(Runnable operation) {
        try {
            operation.run();
            fail("Dữ liệu Profile không hợp lệ vẫn được chấp nhận.");
        } catch (ValidationException expected) {
            // Expected validation failure.
        }
    }

    private static final class FakeUserProfileDao
            implements UserProfileDao {

        private final User user;

        private FakeUserProfileDao() {
            user = new User();
            user.setId(7);
            user.setUsername("profile_test");
            user.setPasswordHash("must-not-leak");
            user.setFullName("Old Name");
            user.setEmail("profile@local.test");
            user.setRole("USER");
            user.setActive(true);
        }

        @Override
        public Optional<User> findById(int userId) {
            return userId == user.getId()
                    ? Optional.of(user)
                    : Optional.empty();
        }

        @Override
        public Optional<User> updateProfile(
                int userId,
                String fullName,
                String phone,
                String image
        ) {
            if (userId != user.getId()) {
                return Optional.empty();
            }

            user.setFullName(fullName);
            user.setPhone(phone);
            user.setImage(image);
            return Optional.of(user);
        }
    }
}
