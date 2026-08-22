package com.studentos.dao;

import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;

/** Stores only hashes of browser login tokens so a deployment restart does not end valid sessions. */
public class AuthSessionDAO {
    public record SessionIdentity(int userId, int authVersion) { }

    public void create(int userId, int authVersion, String tokenHash, Instant expiresAt) {
        String sql = "INSERT INTO auth_sessions (user_id, auth_version, token_hash, expires_at) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setInt(2, authVersion);
            statement.setString(3, tokenHash);
            statement.setTimestamp(4, Timestamp.from(expiresAt));
            statement.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Could not create an authenticated session", e);
        }
    }

    public SessionIdentity findActive(String tokenHash) {
        String sql = "SELECT user_id, auth_version FROM auth_sessions "
                + "WHERE token_hash = ? AND revoked_at IS NULL AND expires_at > CURRENT_TIMESTAMP";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tokenHash);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? new SessionIdentity(result.getInt("user_id"), result.getInt("auth_version")) : null;
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not validate an authenticated session", e);
        }
    }

    public void touch(String tokenHash) {
        update("UPDATE auth_sessions SET last_seen_at = CURRENT_TIMESTAMP WHERE token_hash = ? AND revoked_at IS NULL", tokenHash);
    }

    public void revoke(String tokenHash) {
        update("UPDATE auth_sessions SET revoked_at = CURRENT_TIMESTAMP WHERE token_hash = ? AND revoked_at IS NULL", tokenHash);
    }

    public void revokeAllForUser(int userId) {
        String sql = "UPDATE auth_sessions SET revoked_at = CURRENT_TIMESTAMP WHERE user_id = ? AND revoked_at IS NULL";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Could not revoke authenticated sessions", e);
        }
    }

    private void update(String sql, String tokenHash) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tokenHash);
            statement.executeUpdate();
        } catch (Exception e) {
            throw new IllegalStateException("Could not update an authenticated session", e);
        }
    }
}
