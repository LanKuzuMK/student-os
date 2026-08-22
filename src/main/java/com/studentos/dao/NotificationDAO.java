package com.studentos.dao;

import com.studentos.model.Notification;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Persistence for private, in-app account and activity notifications. */
public class NotificationDAO {
    public boolean create(int recipientId, String type, String title, String message, String actionUrl) {
        if (recipientId < 1 || type == null || title == null || message == null) return false;
        String sql = "INSERT INTO notifications (recipient_id, type, title, message, action_url) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, recipientId);
            statement.setString(2, type);
            statement.setString(3, title);
            statement.setString(4, message);
            statement.setString(5, actionUrl);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            System.err.println("Unable to create notification: " + exception.getMessage());
            return false;
        }
    }

    public List<Notification> getForRecipient(int recipientId, int limit) {
        String sql = "SELECT id, recipient_id, type, title, message, action_url, is_read, created_at "
                + "FROM notifications WHERE recipient_id = ? ORDER BY is_read ASC, created_at DESC LIMIT ?";
        List<Notification> notifications = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, recipientId);
            statement.setInt(2, Math.max(1, Math.min(limit, 100)));
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) notifications.add(map(result));
            }
        } catch (SQLException exception) {
            System.err.println("Unable to load notifications: " + exception.getMessage());
        }
        return notifications;
    }

    public int countUnreadForRecipient(int recipientId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM notifications WHERE recipient_id = ? AND is_read = FALSE")) {
            statement.setInt(1, recipientId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(1) : 0;
            }
        } catch (SQLException exception) {
            return 0;
        }
    }

    public boolean markRead(int notificationId, int recipientId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ? AND recipient_id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, notificationId);
            statement.setInt(2, recipientId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            return false;
        }
    }

    public boolean markAllRead(int recipientId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE notifications SET is_read = TRUE WHERE recipient_id = ? AND is_read = FALSE")) {
            statement.setInt(1, recipientId);
            return statement.executeUpdate() >= 0;
        } catch (SQLException exception) {
            return false;
        }
    }

    private Notification map(ResultSet result) throws SQLException {
        Notification notification = new Notification();
        notification.setId(result.getInt("id"));
        notification.setRecipientId(result.getInt("recipient_id"));
        notification.setType(result.getString("type"));
        notification.setTitle(result.getString("title"));
        notification.setMessage(result.getString("message"));
        notification.setActionUrl(result.getString("action_url"));
        notification.setRead(result.getBoolean("is_read"));
        notification.setCreatedAt(result.getTimestamp("created_at"));
        return notification;
    }
}
