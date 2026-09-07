package com.thanhdat.servletmvc.daos.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                phone,
                images,
                role,
                is_active,
                created_at,
                updated_at
            FROM users
            WHERE username = ?
            LIMIT 1
            """;

    private static final String FIND_BY_EMAIL_SQL = """
            SELECT
                user_id,
                username,
                password_hash,
                full_name,
                email,
                phone,
                images,
                role,
                is_active,
                created_at,
                updated_at
            FROM users
            WHERE email = ?
            LIMIT 1
            """;

    private static final String EXISTS_BY_USERNAME_SQL = """
            SELECT 1
            FROM users
            WHERE username = ?
            LIMIT 1
            """;

    private static final String EXISTS_BY_EMAIL_SQL = """
            SELECT 1
            FROM users
            WHERE email = ?
            LIMIT 1
            """;

    private static final String INSERT_INACTIVE_SQL = """
            INSERT INTO users (
                username,
                password_hash,
                full_name,
                email,
                role,
                is_active
            )
            VALUES (?, ?, ?, ?, 'USER', FALSE)
            """;

    private static final String ACTIVATE_BY_ID_SQL = """
            UPDATE users
            SET is_active = TRUE
            WHERE user_id = ?
              AND is_active = FALSE
            """;

    private static final String UPDATE_PASSWORD_HASH_SQL = """
            UPDATE users
            SET password_hash = ?
            WHERE user_id = ?
              AND is_active = TRUE
            """;

    @Override
    public Optional<User> findByUsername(String username) {
        return findUser(
                FIND_BY_USERNAME_SQL,
                username,
                "Không thể truy vấn người dùng theo username."
        );
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findUser(
                FIND_BY_EMAIL_SQL,
                email,
                "Không thể truy vấn người dùng theo email."
        );
    }

    @Override
    public boolean existsByUsername(String username) {
        return exists(
                EXISTS_BY_USERNAME_SQL,
                username,
                "Không thể kiểm tra username."
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return exists(
                EXISTS_BY_EMAIL_SQL,
                email,
                "Không thể kiểm tra email."
        );
    }

    @Override
    public int insertInactive(User user) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                INSERT_INACTIVE_SQL,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPasswordHash());
            statement.setString(3, user.getFullName());
            statement.setString(4, user.getEmail());

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new DataAccessException(
                        "Không thể tạo tài khoản chưa kích hoạt."
                );
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

            throw new DataAccessException(
                    "Không lấy được ID tài khoản vừa tạo."
            );
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể tạo tài khoản chưa kích hoạt.",
                    exception
            );
        }
    }

    @Override
    public boolean activateById(int userId) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                ACTIVATE_BY_ID_SQL
                        )
        ) {
            statement.setInt(1, userId);

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể kích hoạt tài khoản.",
                    exception
            );
        }
    }

    @Override
    public boolean updatePasswordHash(
            int userId,
            String passwordHash
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                UPDATE_PASSWORD_HASH_SQL
                        )
        ) {
            statement.setString(1, passwordHash);
            statement.setInt(2, userId);

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể cập nhật mật khẩu.",
                    exception
            );
        }
    }

    private Optional<User> findUser(
            String sql,
            String value,
            String errorMessage
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, value);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException(
                    errorMessage,
                    exception
            );
        }
    }

    private boolean exists(
            String sql,
            String value,
            String errorMessage
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, value);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException exception) {
            throw new DataAccessException(
                    errorMessage,
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
        user.setPhone(resultSet.getString("phone"));
        user.setImage(resultSet.getString("images"));
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
