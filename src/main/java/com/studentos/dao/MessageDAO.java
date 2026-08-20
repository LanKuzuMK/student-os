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
                + "CASE WHEN m.sender_id = ? THEN receiver.email ELSE sender.email END AS counterpart_email "
                + "FROM messages m "
                + "JOIN users sender ON sender.id = m.sender_id "
                + "JOIN users receiver ON receiver.id = m.receiver_id "
                + "WHERE (m.sender_id = ? AND COALESCE(m.sender_deleted, FALSE) = FALSE) "
                + "OR (m.receiver_id = ? AND COALESCE(m.receiver_deleted, FALSE) = FALSE) "
                + "ORDER BY m.id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message();
                    message.setId(rs.getInt("id"));
                    message.setSenderId(rs.getInt("sender_id"));
                    message.setReceiverId(rs.getInt("receiver_id"));
                    message.setContent(rs.getString("content"));
                    message.setCounterpartEmail(rs.getString("counterpart_email"));
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to load messages: " + e.getMessage());
        }
        return messages;
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
        String sql = "UPDATE messages SET "
                + "sender_deleted = CASE WHEN sender_id = ? THEN TRUE ELSE sender_deleted END, "
                + "receiver_deleted = CASE WHEN receiver_id = ? THEN TRUE ELSE receiver_deleted END "
                + "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, counterpartId);
            ps.setInt(5, counterpartId);
            ps.setInt(6, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Unable to clear conversation: " + e.getMessage());
            return false;
        }
    }
}
