package com.studentos.dao;

import com.studentos.model.User;
import com.studentos.util.DBConnection;
import java.sql.*;
import java.util.*;

/**
 * AdminDAO — all privileged data operations for the admin dashboard.
 * Every mutating method is owner-safe: it targets a specific row by primary key
 * and never touches the calling admin's own account.
 */
public class AdminDAO {

    // ── Real platform stats ──────────────────────────────────────────────────

    public Map<String, Integer> getPlatformStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String[] queries = {
            "SELECT COUNT(*) FROM users",
            "SELECT COUNT(*) FROM users WHERE status = 'BANNED'",
            "SELECT COUNT(*) FROM user_skills",
            "SELECT COUNT(*) FROM jobs",
            "SELECT COUNT(*) FROM services",
            "SELECT COUNT(*) FROM messages",
            "SELECT COUNT(*) FROM goals",
            "SELECT COUNT(*) FROM tasks",
            "SELECT COUNT(*) FROM moderation_reports WHERE status = 'OPEN'"
        };
        String[] keys = {"totalUsers","bannedUsers","totalSkills","totalJobs",
                         "totalServices","totalMessages","totalGoals","totalTasks","openReports"};
        try (Connection conn = DBConnection.getConnection()) {
            for (int i = 0; i < queries.length; i++) {
                try (PreparedStatement ps = conn.prepareStatement(queries[i]);
                     ResultSet rs = ps.executeQuery()) {
                    stats.put(keys[i], rs.next() ? rs.getInt(1) : 0);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    // ── User list ────────────────────────────────────────────────────────────

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, email, role, status, created_at FROM users ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setRole(rs.getString("role"));
                u.setStatus(rs.getString("status"));
                u.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // ── Ban / unban ──────────────────────────────────────────────────────────

    public boolean banUser(int targetId, int adminId) {
        if (targetId == adminId) return false;
        String sql = "UPDATE users SET status = 'BANNED' WHERE id = ? AND id != ?";
        return executeUpdate(sql, targetId, adminId);
    }

    public boolean unbanUser(int targetId, int adminId) {
        if (targetId == adminId) return false;
        String sql = "UPDATE users SET status = 'ACTIVE' WHERE id = ? AND id != ?";
        return executeUpdate(sql, targetId, adminId);
    }

    // ── Delete account ───────────────────────────────────────────────────────

    public boolean deleteUser(int targetId, int adminId) {
        if (targetId == adminId) return false;
        // CASCADE on FK handles profiles, skills, jobs, messages, goals, tasks
        String sql = "DELETE FROM users WHERE id = ? AND id != ?";
        return executeUpdate(sql, targetId, adminId);
    }

    // ── Role promotion / demotion ────────────────────────────────────────────

    public boolean setRole(int targetId, String newRole, int adminId) {
        if (targetId == adminId) return false;
        if (!"ADMIN".equals(newRole) && !"STUDENT".equals(newRole)) return false;
        String sql = "UPDATE users SET role = ? WHERE id = ? AND id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setInt(2, targetId);
            ps.setInt(3, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Password reset ───────────────────────────────────────────────────────

    public boolean resetPassword(int targetId, String newHash, int adminId) {
        if (targetId == adminId) return false;
        String sql = "UPDATE users SET password_hash = ? WHERE id = ? AND id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, targetId);
            ps.setInt(3, adminId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    // ── Content moderation ───────────────────────────────────────────────────

    public List<Map<String, Object>> getAllSkills() {
        return fetchRows(
            "SELECT us.id, u.email, us.skill_name, us.skill_level, us.type, us.moderation_status, us.moderation_note, us.created_at " +
            "FROM user_skills us JOIN users u ON us.user_id = u.id ORDER BY us.id DESC",
            "id","email","skill_name","skill_level","type","moderation_status","moderation_note","created_at");
    }

    public List<Map<String, Object>> getAllJobs() {
        return fetchRows(
            "SELECT j.id, u.email, j.title, j.description, j.status, j.moderation_status, j.moderation_note, j.created_at " +
            "FROM jobs j JOIN users u ON j.user_id = u.id ORDER BY j.id DESC",
            "id","email","title","description","status","moderation_status","moderation_note","created_at");
    }

    public List<Map<String, Object>> getAllServices() {
        return fetchRows(
            "SELECT s.id, u.email, s.title, s.description, s.moderation_status, s.moderation_note, s.created_at " +
            "FROM services s JOIN users u ON s.user_id = u.id ORDER BY s.id DESC",
            "id","email","title","description","moderation_status","moderation_note","created_at");
    }

    public boolean deleteSkill(int id) {
        return executeUpdateSingle("DELETE FROM user_skills WHERE id = ?", id);
    }

    public boolean deleteJob(int id) {
        return executeUpdateSingle("DELETE FROM jobs WHERE id = ?", id);
    }

    public boolean deleteService(int id) {
        return executeUpdateSingle("DELETE FROM services WHERE id = ?", id);
    }

    // ── Message overview ─────────────────────────────────────────────────────

    public List<Map<String, Object>> getRecentMessages(int limit) {
        return fetchRows(
            "SELECT m.id, s.email AS sender, r.email AS receiver, " +
            "LEFT(m.content, 80) AS preview, m.created_at " +
            "FROM messages m " +
            "JOIN users s ON m.sender_id = s.id " +
            "JOIN users r ON m.receiver_id = r.id " +
            "ORDER BY m.created_at DESC LIMIT " + limit,
            "id","sender","receiver","preview","created_at");
    }

    public boolean deleteMessage(int id) {
        return executeUpdateSingle("DELETE FROM messages WHERE id = ?", id);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private boolean executeUpdate(String sql, int p1, int p2) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p1); ps.setInt(2, p2);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private boolean executeUpdateSingle(String sql, int p1) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p1);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private List<Map<String, Object>> fetchRows(String sql, String... cols) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (String col : cols) row.put(col, rs.getObject(col));
                rows.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }
}
