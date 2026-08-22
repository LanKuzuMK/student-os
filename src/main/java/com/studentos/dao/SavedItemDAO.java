package com.studentos.dao;

import com.studentos.model.SavedItem;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Private bookmarks that resolve only against content that is still public and eligible to view. */
public class SavedItemDAO {
    public boolean save(int ownerId, String targetType, int targetId) {
        if (ownerId < 1 || targetId < 1 || !isPublicTarget(ownerId, targetType, targetId)) return false;
        String sql = "INSERT INTO saved_items (owner_id, target_type, target_id) VALUES (?, ?, ?) ON CONFLICT (owner_id, target_type, target_id) DO NOTHING";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId); statement.setString(2, targetType); statement.setInt(3, targetId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) { System.err.println("Unable to save item: " + exception.getMessage()); return false; }
    }

    public boolean remove(int savedItemId, int ownerId) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM saved_items WHERE id = ? AND owner_id = ?")) {
            statement.setInt(1, savedItemId); statement.setInt(2, ownerId); return statement.executeUpdate() == 1;
        } catch (SQLException exception) { return false; }
    }

    public List<SavedItem> getForOwner(int ownerId, String type) {
        List<SavedItem> items = new ArrayList<>();
        String filter = type == null ? "" : " AND target_type = ?";
        String sql = "SELECT * FROM ("
                + "SELECT si.id, si.target_type, si.target_id, si.created_at, COALESCE(NULLIF(TRIM(CONCAT_WS(' ', p.first_name, p.last_name)), ''), u.email) AS title, "
                + "COALESCE(NULLIF(CONCAT_WS(' · ', p.major, p.university), ''), 'Public student profile') AS detail, '/profile/view?id=' || u.id AS target_url "
                + "FROM saved_items si JOIN users u ON u.id = si.target_id LEFT JOIN profiles p ON p.user_id = u.id WHERE si.owner_id = ? AND si.target_type = 'PROFILE' AND u.status = 'ACTIVE' "
                + "UNION ALL "
                + "SELECT si.id, si.target_type, si.target_id, si.created_at, s.skill_name AS title, COALESCE(s.skill_level, 'Student skill') || ' · ' || COALESCE(s.type, 'SKILL') AS detail, '/skills/discover' AS target_url "
                + "FROM saved_items si JOIN user_skills s ON s.id = si.target_id JOIN users u ON u.id = s.user_id WHERE si.owner_id = ? AND si.target_type = 'SKILL' AND u.status = 'ACTIVE' AND COALESCE(s.moderation_status, 'VISIBLE') = 'VISIBLE' "
                + "UNION ALL "
                + "SELECT si.id, si.target_type, si.target_id, si.created_at, s.skill_name AS title, 'Student service offer · ' || COALESCE(s.skill_level, 'Skill') AS detail, '/skills/discover' AS target_url "
                + "FROM saved_items si JOIN user_skills s ON s.id = si.target_id JOIN users u ON u.id = s.user_id WHERE si.owner_id = ? AND si.target_type = 'SERVICE' AND s.type = 'TEACH' AND u.status = 'ACTIVE' AND COALESCE(s.moderation_status, 'VISIBLE') = 'VISIBLE' "
                + "UNION ALL "
                + "SELECT si.id, si.target_type, si.target_id, si.created_at, j.title AS title, 'Budget: ' || COALESCE(j.budget::text, 'Not specified') AS detail, '/freelance' AS target_url "
                + "FROM saved_items si JOIN jobs j ON j.id = si.target_id JOIN users u ON u.id = j.user_id WHERE si.owner_id = ? AND si.target_type = 'JOB' AND u.status = 'ACTIVE' AND COALESCE(j.moderation_status, 'VISIBLE') = 'VISIBLE'"
                + ") saved WHERE 1 = 1" + filter + " ORDER BY created_at DESC";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, ownerId); statement.setInt(2, ownerId); statement.setInt(3, ownerId); statement.setInt(4, ownerId);
            if (type != null) statement.setString(5, type);
            try (ResultSet result = statement.executeQuery()) { while (result.next()) items.add(map(result)); }
        } catch (SQLException exception) { System.err.println("Unable to load saved items: " + exception.getMessage()); }
        return items;
    }

    public int countForOwner(int ownerId) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM saved_items WHERE owner_id = ?")) {
            statement.setInt(1, ownerId); try (ResultSet result = statement.executeQuery()) { return result.next() ? result.getInt(1) : 0; }
        } catch (SQLException exception) { return 0; }
    }

    private boolean isPublicTarget(int ownerId, String type, int targetId) {
        String sql = switch (type) {
            case "PROFILE" -> "SELECT 1 FROM users WHERE id = ? AND id <> ? AND status = 'ACTIVE'";
            case "SKILL" -> "SELECT 1 FROM user_skills s JOIN users u ON u.id = s.user_id WHERE s.id = ? AND u.status = 'ACTIVE' AND COALESCE(s.moderation_status, 'VISIBLE') = 'VISIBLE'";
            case "SERVICE" -> "SELECT 1 FROM user_skills s JOIN users u ON u.id = s.user_id WHERE s.id = ? AND s.type = 'TEACH' AND u.status = 'ACTIVE' AND COALESCE(s.moderation_status, 'VISIBLE') = 'VISIBLE'";
            case "JOB" -> "SELECT 1 FROM jobs j JOIN users u ON u.id = j.user_id WHERE j.id = ? AND u.status = 'ACTIVE' AND COALESCE(j.moderation_status, 'VISIBLE') = 'VISIBLE'";
            default -> null;
        };
        if (sql == null) return false;
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, targetId); if ("PROFILE".equals(type)) statement.setInt(2, ownerId);
            try (ResultSet result = statement.executeQuery()) { return result.next(); }
        } catch (SQLException exception) { return false; }
    }

    private SavedItem map(ResultSet result) throws SQLException {
        SavedItem item = new SavedItem(); item.setId(result.getInt("id")); item.setTargetType(result.getString("target_type")); item.setTargetId(result.getInt("target_id"));
        item.setTitle(result.getString("title")); item.setDetail(result.getString("detail")); item.setTargetUrl(result.getString("target_url")); item.setCreatedAt(result.getTimestamp("created_at")); return item;
    }
}
