package com.studentos.dao;

import com.studentos.model.Message;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {
    public List<Message> getMessagesForUser(int userId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT m.id, m.sender_id, m.receiver_id, m.content, "
                + "CASE WHEN m.sender_id = ? THEN receiver.email ELSE sender.email END AS counterpart_email, "
                + "CASE WHEN m.sender_id = ? THEN m.receiver_id ELSE m.sender_id END AS counterpart_id "
                + "FROM messages m "
                + "JOIN users sender ON sender.id = m.sender_id "
                + "JOIN users receiver ON receiver.id = m.receiver_id "
                + "WHERE (m.sender_id = ? AND COALESCE(m.sender_deleted, FALSE) = FALSE) "
                + "OR (m.receiver_id = ? AND COALESCE(m.receiver_deleted, FALSE) = FALSE) "
                + "ORDER BY m.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, userId); ps.setInt(3, userId); ps.setInt(4, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message();
                    message.setId(rs.getInt("id"));
                    message.setSenderId(rs.getInt("sender_id"));
                    message.setReceiverId(rs.getInt("receiver_id"));
                    message.setContent(rs.getString("content"));
                    message.setCounterpartEmail(rs.getString("counterpart_email"));
                    message.setCounterpartId(rs.getInt("counterpart_id"));
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to load messages: " + e.getMessage());
        }
        return messages;
    }

    public int countUnreadMessagesForUser(int userId) {
        String sql = "SELECT COUNT(*) FROM messages WHERE receiver_id = ? "
                + "AND COALESCE(receiver_deleted, FALSE) = FALSE AND is_read = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.err.println("Unable to count unread messages: " + e.getMessage());
            return 0;
        }
    }

    public boolean markMessagesReadForUser(int userId) {
        String sql = "UPDATE messages SET is_read = TRUE WHERE receiver_id = ? "
                + "AND COALESCE(receiver_deleted, FALSE) = FALSE AND is_read = FALSE";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Unable to mark messages read: " + e.getMessage());
            return false;
        }
    }

    public boolean sendMessage(Message message) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, message.getSenderId());
            ps.setInt(2, message.getReceiverId());
            ps.setString(3, message.getContent());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Unable to send message: " + e.getMessage());
            return false;
        }
    }

    public boolean clearConversationForUser(int userId, int counterpartId) {
        String hideSql = "UPDATE messages SET "
                + "sender_deleted = CASE WHEN sender_id = ? THEN TRUE ELSE sender_deleted END, "
                + "receiver_deleted = CASE WHEN receiver_id = ? THEN TRUE ELSE receiver_deleted END "
                + "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
        String purgeSql = "DELETE FROM messages WHERE sender_deleted = TRUE AND receiver_deleted = TRUE "
                + "AND ((sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?))";

        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement hideStatement = conn.prepareStatement(hideSql);
                 PreparedStatement purgeStatement = conn.prepareStatement(purgeSql)) {
                hideStatement.setInt(1, userId);
                hideStatement.setInt(2, userId);
                hideStatement.setInt(3, userId);
                hideStatement.setInt(4, counterpartId);
                hideStatement.setInt(5, counterpartId);
                hideStatement.setInt(6, userId);
                int hiddenMessages = hideStatement.executeUpdate();

                purgeStatement.setInt(1, userId);
                purgeStatement.setInt(2, counterpartId);
                purgeStatement.setInt(3, counterpartId);
                purgeStatement.setInt(4, userId);
                purgeStatement.executeUpdate();

                conn.commit();
                return hiddenMessages > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            System.err.println("Unable to clear conversation: " + e.getMessage());
            return false;
        }
    }
}
