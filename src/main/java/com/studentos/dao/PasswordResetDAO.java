package com.studentos.dao;

import com.studentos.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Stores only BCrypt-hashed, short-lived password-reset codes. */
public class PasswordResetDAO {
    private static final long RESEND_COOLDOWN_SECONDS = 60;
    private static final long CODE_LIFETIME_MINUTES = 10;
    private static final int MAX_ATTEMPTS = 5;

    public boolean createOrReplace(String email, String plainCode) {
        String select = "SELECT issued_at FROM password_reset_codes WHERE email = ? FOR UPDATE";
        String insert = "INSERT INTO password_reset_codes (email, code_hash, expires_at, attempts, issued_at) VALUES (?, ?, ?, 0, ?)";
        String update = "UPDATE password_reset_codes SET code_hash = ?, expires_at = ?, attempts = 0, issued_at = ? WHERE email = ?";
        Instant now = Instant.now();
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement selectStatement = connection.prepareStatement(select)) {
                selectStatement.setString(1, email);
                try (ResultSet result = selectStatement.executeQuery()) {
                    if (result.next()) {
                        Timestamp issuedAt = result.getTimestamp("issued_at");
                        if (issuedAt != null && issuedAt.toInstant().plus(RESEND_COOLDOWN_SECONDS, ChronoUnit.SECONDS).isAfter(now)) {
                            connection.rollback();
                            return false;
                        }
                        try (PreparedStatement updateStatement = connection.prepareStatement(update)) {
                            updateStatement.setString(1, BCrypt.hashpw(plainCode, BCrypt.gensalt()));
                            updateStatement.setTimestamp(2, Timestamp.from(now.plus(CODE_LIFETIME_MINUTES, ChronoUnit.MINUTES)));
                            updateStatement.setTimestamp(3, Timestamp.from(now));
                            updateStatement.setString(4, email);
                            updateStatement.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
                            insertStatement.setString(1, email);
                            insertStatement.setString(2, BCrypt.hashpw(plainCode, BCrypt.gensalt()));
                            insertStatement.setTimestamp(3, Timestamp.from(now.plus(CODE_LIFETIME_MINUTES, ChronoUnit.MINUTES)));
                            insertStatement.setTimestamp(4, Timestamp.from(now));
                            insertStatement.executeUpdate();
                        }
                    }
                    connection.commit();
                    return true;
                }
            } catch (SQLException exception) {
                connection.rollback();
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            return false;
        }
    }

    public boolean consumeIfValid(String email, String plainCode) {
        String select = "SELECT code_hash, expires_at, attempts FROM password_reset_codes WHERE email = ? FOR UPDATE";
        String increment = "UPDATE password_reset_codes SET attempts = attempts + 1 WHERE email = ?";
        String delete = "DELETE FROM password_reset_codes WHERE email = ?";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement selectStatement = connection.prepareStatement(select)) {
                selectStatement.setString(1, email);
                try (ResultSet result = selectStatement.executeQuery()) {
                    if (!result.next()) {
                        connection.rollback();
                        return false;
                    }
                    Timestamp expiresAt = result.getTimestamp("expires_at");
                    int attempts = result.getInt("attempts");
                    boolean expiredOrLocked = expiresAt == null || !expiresAt.toInstant().isAfter(Instant.now()) || attempts >= MAX_ATTEMPTS;
                    boolean matches = false;
                    if (!expiredOrLocked) {
                        try {
                            matches = BCrypt.checkpw(plainCode, result.getString("code_hash"));
                        } catch (IllegalArgumentException ignored) {
                            matches = false;
                        }
                    }
                    if (matches) {
                        try (PreparedStatement deleteStatement = connection.prepareStatement(delete)) {
                            deleteStatement.setString(1, email);
                            deleteStatement.executeUpdate();
                        }
                        connection.commit();
                        return true;
                    }
                    if (expiredOrLocked || attempts + 1 >= MAX_ATTEMPTS) {
                        try (PreparedStatement deleteStatement = connection.prepareStatement(delete)) {
                            deleteStatement.setString(1, email);
                            deleteStatement.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement incrementStatement = connection.prepareStatement(increment)) {
                            incrementStatement.setString(1, email);
                            incrementStatement.executeUpdate();
                        }
                    }
                    connection.commit();
                    return false;
                }
            } catch (SQLException exception) {
                connection.rollback();
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            return false;
        }
    }

    public void delete(String email) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM password_reset_codes WHERE email = ?")) {
            statement.setString(1, email);
            statement.executeUpdate();
        } catch (SQLException ignored) {
            // Reset initiation always keeps its public response generic.
        }
    }
}
