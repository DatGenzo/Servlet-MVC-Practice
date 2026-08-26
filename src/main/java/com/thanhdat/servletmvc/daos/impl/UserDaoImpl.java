package com.thanhdat.servletmvc.daos.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import com.thanhdat.servletmvc.config.DBConnection;
import com.thanhdat.servletmvc.daos.UserDao;
import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.User;

public class UserDaoImpl implements UserDao {

    private static final String FIND_BY_USERNAME_SQL = """
            SELECT
                user_id,
                username,
                password_hash,
                full_name,
                email,
                role,
                is_active,
                created_at,
                updated_at
            FROM users
            WHERE username = ?
            LIMIT 1
            """;

    @Override
    public Optional<User> findByUsername(String username) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_BY_USERNAME_SQL
                        )
        ) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể truy vấn người dùng theo username.",
                    exception
            );
        }
    }

    private User mapUser(ResultSet resultSet)
            throws SQLException {

        User user = new User();

        user.setId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPasswordHash(
                resultSet.getString("password_hash")
        );
        user.setFullName(resultSet.getString("full_name"));
        user.setEmail(resultSet.getString("email"));
        user.setRole(resultSet.getString("role"));
        user.setActive(resultSet.getBoolean("is_active"));
        user.setCreatedAt(
                toLocalDateTime(
                        resultSet.getTimestamp("created_at")
                )
        );
        user.setUpdatedAt(
                toLocalDateTime(
                        resultSet.getTimestamp("updated_at")
                )
        );

        return user;
    }

    private LocalDateTime toLocalDateTime(
            Timestamp timestamp
    ) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }
}