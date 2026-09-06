package com.thanhdat.servletmvc.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

import com.thanhdat.servletmvc.daos.impl.UserDaoImpl;
import com.thanhdat.servletmvc.models.User;

public class UserDaoIntegrationTest {

    @Test
    public void shouldFindAndCheckExistingAdmin() {
        UserDao userDao = new UserDaoImpl();

        Optional<User> byUsername =
                userDao.findByUsername("admin");

        Optional<User> byEmail =
                userDao.findByEmail("admin@local.test");

        assertTrue(byUsername.isPresent());
        assertTrue(byEmail.isPresent());

        User admin = byUsername.get();

        assertEquals("admin", admin.getUsername());
        assertEquals("admin@local.test", admin.getEmail());
        assertEquals("ADMIN", admin.getRole());
        assertTrue(admin.isActive());

        assertEquals(
                admin.getId(),
                byEmail.get().getId()
        );

        assertTrue(userDao.existsByUsername("admin"));
        assertTrue(
                userDao.existsByEmail("admin@local.test")
        );

        assertFalse(
                userDao.existsByUsername(
                        "__auth01_missing_user__"
                )
        );

        assertFalse(
                userDao.existsByEmail(
                        "__auth01_missing_email__@local.test"
                )
        );
    }
}