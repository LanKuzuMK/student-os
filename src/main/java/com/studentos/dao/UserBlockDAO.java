package com.studentos.dao;

import com.studentos.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/** Stores a user's own reversible direct-contact boundaries. */
public class UserBlockDAO {
    public boolean block(int blockerId, int blockedId) {
        if (blockerId < 1 || blockedId < 1 || blockerId == blockedId) return false;
        String sql = "INSERT INTO user_blocks (blocker_id, blocked_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blockerId); ps.setInt(2, blockedId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { System.err.println("Unable to block user: " + e.getMessage()); return false; }
    }

    public boolean unblock(int blockerId, int blockedId) {
        String sql = "DELETE FROM user_blocks WHERE blocker_id = ? AND blocked_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blockerId); ps.setInt(2, blockedId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { System.err.println("Unable to unblock user: " + e.getMessage()); return false; }
    }

    public boolean isContactBlocked(int senderId, int receiverId) {
        String sql = "SELECT 1 FROM user_blocks WHERE (blocker_id = ? AND blocked_id = ?) OR (blocker_id = ? AND blocked_id = ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId); ps.setInt(2, receiverId); ps.setInt(3, receiverId); ps.setInt(4, senderId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { return true; }
    }

    public Set<Integer> getBlockedUserIds(int blockerId) {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT blocked_id FROM user_blocks WHERE blocker_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, blockerId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) ids.add(rs.getInt(1)); }
        } catch (SQLException e) { System.err.println("Unable to load blocked users: " + e.getMessage()); }
        return ids;
    }
}
