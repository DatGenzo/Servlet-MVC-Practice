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
import com.thanhdat.servletmvc.daos.UserOtpDao;
import com.thanhdat.servletmvc.exceptions.DataAccessException;
import com.thanhdat.servletmvc.models.OtpPurpose;
import com.thanhdat.servletmvc.models.UserOtp;

public class UserOtpDaoImpl implements UserOtpDao {

    private static final String INVALIDATE_ACTIVE_SQL = """
            UPDATE user_otps
            SET consumed_at = CURRENT_TIMESTAMP
            WHERE user_id = ?
              AND purpose = ?
              AND consumed_at IS NULL
            """;

    private static final String INSERT_SQL = """
            INSERT INTO user_otps (
                user_id,
                purpose,
                otp_hash,
                expires_at,
                attempt_count,
                consumed_at
            )
            VALUES (?, ?, ?, ?, 0, NULL)
            """;

    private static final String FIND_LATEST_ACTIVE_SQL = """
            SELECT
                otp_id,
                user_id,
                purpose,
                otp_hash,
                expires_at,
                attempt_count,
                consumed_at,
                created_at
            FROM user_otps
            WHERE user_id = ?
              AND purpose = ?
              AND consumed_at IS NULL
            ORDER BY created_at DESC, otp_id DESC
            LIMIT 1
            """;

    private static final String INCREMENT_ATTEMPT_SQL = """
            UPDATE user_otps
            SET attempt_count = attempt_count + 1
            WHERE otp_id = ?
              AND consumed_at IS NULL
              AND expires_at > CURRENT_TIMESTAMP
              AND attempt_count < ?
            """;

    private static final String CONSUME_IF_USABLE_SQL = """
            UPDATE user_otps
            SET consumed_at = CURRENT_TIMESTAMP
            WHERE otp_id = ?
              AND consumed_at IS NULL
              AND expires_at > CURRENT_TIMESTAMP
              AND attempt_count < ?
            """;

    @Override
    public long replaceActiveOtp(
            int userId,
            OtpPurpose purpose,
            String otpHash,
            LocalDateTime expiresAt
    ) {
        try (Connection connection =
                DBConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {
                invalidateActive(
                        connection,
                        userId,
                        purpose
                );

                long otpId = insertOtp(
                        connection,
                        userId,
                        purpose,
                        otpHash,
                        expiresAt
                );

                connection.commit();

                return otpId;
            } catch (SQLException exception) {
                rollback(connection, exception);

                throw new DataAccessException(
                        "Không thể thay thế OTP hiện hành.",
                        exception
                );
            }
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể truy cập dữ liệu OTP.",
                    exception
            );
        }
    }

    @Override
    public Optional<UserOtp> findLatestActive(
            int userId,
            OtpPurpose purpose
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                FIND_LATEST_ACTIVE_SQL
                        )
        ) {
            statement.setInt(1, userId);
            statement.setString(2, purpose.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapUserOtp(resultSet));
                }

                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể tìm OTP hiện hành.",
                    exception
            );
        }
    }

    @Override
    public boolean incrementAttempt(
            long otpId,
            int maxAttempts
    ) {
        validateMaxAttempts(maxAttempts);

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                INCREMENT_ATTEMPT_SQL
                        )
        ) {
            statement.setLong(1, otpId);
            statement.setInt(2, maxAttempts);

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể tăng số lần nhập sai OTP.",
                    exception
            );
        }
    }

    @Override
    public boolean consumeIfUsable(
            long otpId,
            int maxAttempts
    ) {
        validateMaxAttempts(maxAttempts);

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                CONSUME_IF_USABLE_SQL
                        )
        ) {
            statement.setLong(1, otpId);
            statement.setInt(2, maxAttempts);

            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            throw new DataAccessException(
                    "Không thể đánh dấu OTP đã sử dụng.",
                    exception
            );
        }
    }

    private void invalidateActive(
            Connection connection,
            int userId,
            OtpPurpose purpose
    ) throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement(
                        INVALIDATE_ACTIVE_SQL
                )) {

            statement.setInt(1, userId);
            statement.setString(2, purpose.name());
            statement.executeUpdate();
        }
    }

    private long insertOtp(
            Connection connection,
            int userId,
            OtpPurpose purpose,
            String otpHash,
            LocalDateTime expiresAt
    ) throws SQLException {
        try (PreparedStatement statement =
                connection.prepareStatement(
                        INSERT_SQL,
                        Statement.RETURN_GENERATED_KEYS
                )) {

            statement.setInt(1, userId);
            statement.setString(2, purpose.name());
            statement.setString(3, otpHash);
            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(expiresAt)
            );

            int affectedRows = statement.executeUpdate();

            if (affectedRows != 1) {
                throw new SQLException(
                        "Insert OTP không ảnh hưởng đúng một dòng."
                );
            }

            try (ResultSet generatedKeys =
                    statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                }
            }

            throw new SQLException(
                    "Không lấy được otp_id vừa tạo."
            );
        }
    }

    private UserOtp mapUserOtp(ResultSet resultSet)
            throws SQLException {

        UserOtp userOtp = new UserOtp();

        userOtp.setId(resultSet.getLong("otp_id"));
        userOtp.setUserId(resultSet.getInt("user_id"));
        userOtp.setPurpose(
                OtpPurpose.valueOf(
                        resultSet.getString("purpose")
                )
        );
        userOtp.setOtpHash(
                resultSet.getString("otp_hash")
        );
        userOtp.setExpiresAt(
                toLocalDateTime(
                        resultSet.getTimestamp("expires_at")
                )
        );
        userOtp.setAttemptCount(
                resultSet.getInt("attempt_count")
        );
        userOtp.setConsumedAt(
                toLocalDateTime(
                        resultSet.getTimestamp("consumed_at")
                )
        );
        userOtp.setCreatedAt(
                toLocalDateTime(
                        resultSet.getTimestamp("created_at")
                )
        );

        return userOtp;
    }

    private LocalDateTime toLocalDateTime(
            Timestamp timestamp
    ) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }

    private void validateMaxAttempts(int maxAttempts) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException(
                    "Số lần thử tối đa phải lớn hơn 0."
            );
        }
    }

    private void rollback(
            Connection connection,
            SQLException originalException
    ) {
        try {
            connection.rollback();
        } catch (SQLException rollbackException) {
            originalException.addSuppressed(
                    rollbackException
            );
        }
    }
}