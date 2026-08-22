package com.studentos.dao;

import com.studentos.model.Skill;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {
    public List<Skill> getSkillsByUserId(int userId) {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT * FROM user_skills WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    skills.add(mapSkill(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to load skills: " + e.getMessage());
        }
        return skills;
    }

    public List<Skill> getAllSkills() {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT us.*, u.email AS owner_email, "
                + "COALESCE(NULLIF(TRIM(CONCAT_WS(' ', p.first_name, p.last_name)), ''), u.email) AS owner_name "
                + "FROM user_skills us JOIN users u ON u.id = us.user_id "
                + "LEFT JOIN profiles p ON p.user_id = us.user_id WHERE COALESCE(us.moderation_status, 'VISIBLE') = 'VISIBLE' ORDER BY us.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Skill skill = mapSkill(rs);
                skill.setOwnerName(rs.getString("owner_name"));
                skill.setOwnerEmail(rs.getString("owner_email"));
                skills.add(skill);
            }
        } catch (SQLException e) {
            System.err.println("Unable to load community skills: " + e.getMessage());
        }
        return skills;
    }

    public List<Skill> searchPublicSkills(String query, String type, String level, String availability, String sort, int limit, int offset) {
        List<Skill> skills = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT us.*, u.email AS owner_email, "
                + "COALESCE(NULLIF(TRIM(CONCAT_WS(' ', p.first_name, p.last_name)), ''), u.email) AS owner_name, "
                + "p.university, p.major, p.availability_status, p.collaboration_preferences "
                + "FROM user_skills us JOIN users u ON u.id = us.user_id "
                + "LEFT JOIN profiles p ON p.user_id = us.user_id "
                + "WHERE COALESCE(us.moderation_status, 'VISIBLE') = 'VISIBLE' AND u.status = 'ACTIVE'");
        List<String> textParameters = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            sql.append(" AND (LOWER(us.skill_name) LIKE ? OR LOWER(COALESCE(p.first_name, '')) LIKE ? OR LOWER(COALESCE(p.last_name, '')) LIKE ? "
                    + "OR LOWER(COALESCE(p.university, '')) LIKE ? OR LOWER(COALESCE(p.major, '')) LIKE ? OR LOWER(COALESCE(p.collaboration_preferences, '')) LIKE ?)");
            String term = "%" + query.trim().toLowerCase() + "%";
            for (int i = 0; i < 6; i++) textParameters.add(term);
        }
        if (type != null) { sql.append(" AND us.type = ?"); textParameters.add(type); }
        if (level != null) { sql.append(" AND us.skill_level = ?"); textParameters.add(level); }
        if (availability != null) { sql.append(" AND p.availability_status = ?"); textParameters.add(availability); }
        sql.append(orderBy(sort)).append(" LIMIT ? OFFSET ?");
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            for (String parameter : textParameters) ps.setString(index++, parameter);
            ps.setInt(index++, Math.max(1, Math.min(limit, 48)));
            ps.setInt(index, Math.max(0, offset));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) skills.add(mapPublicSkill(rs));
            }
        } catch (SQLException e) { System.err.println("Unable to search community skills: " + e.getMessage()); }
        return skills;
    }

    public int countPublicSkills(String query, String type, String level, String availability) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM user_skills us JOIN users u ON u.id = us.user_id "
                + "LEFT JOIN profiles p ON p.user_id = us.user_id WHERE COALESCE(us.moderation_status, 'VISIBLE') = 'VISIBLE' AND u.status = 'ACTIVE'");
        List<String> textParameters = new ArrayList<>();
        if (query != null && !query.isBlank()) {
            sql.append(" AND (LOWER(us.skill_name) LIKE ? OR LOWER(COALESCE(p.first_name, '')) LIKE ? OR LOWER(COALESCE(p.last_name, '')) LIKE ? "
                    + "OR LOWER(COALESCE(p.university, '')) LIKE ? OR LOWER(COALESCE(p.major, '')) LIKE ? OR LOWER(COALESCE(p.collaboration_preferences, '')) LIKE ?)");
            String term = "%" + query.trim().toLowerCase() + "%";
            for (int i = 0; i < 6; i++) textParameters.add(term);
        }
        if (type != null) { sql.append(" AND us.type = ?"); textParameters.add(type); }
        if (level != null) { sql.append(" AND us.skill_level = ?"); textParameters.add(level); }
        if (availability != null) { sql.append(" AND p.availability_status = ?"); textParameters.add(availability); }
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;
            for (String parameter : textParameters) ps.setString(index++, parameter);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException e) { return 0; }
    }

    public boolean addSkill(Skill skill) {
        String sql = "INSERT INTO user_skills (user_id, skill_name, skill_level, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, skill.getUserId());
            ps.setString(2, skill.getSkillName());
            ps.setString(3, skill.getSkillLevel());
            ps.setString(4, skill.getType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Unable to add skill: " + e.getMessage());
            return false;
        }
    }

    private Skill mapSkill(ResultSet rs) throws SQLException {
        Skill skill = new Skill();
        skill.setId(rs.getInt("id"));
        skill.setUserId(rs.getInt("user_id"));
        skill.setSkillName(rs.getString("skill_name"));
        skill.setSkillLevel(rs.getString("skill_level"));
        skill.setType(rs.getString("type"));
        return skill;
    }

    private Skill mapPublicSkill(ResultSet rs) throws SQLException {
        Skill skill = mapSkill(rs);
        skill.setOwnerName(rs.getString("owner_name"));
        skill.setOwnerEmail(rs.getString("owner_email"));
        skill.setUniversity(rs.getString("university"));
        skill.setMajor(rs.getString("major"));
        skill.setAvailabilityStatus(rs.getString("availability_status"));
        skill.setCollaborationPreferences(rs.getString("collaboration_preferences"));
        return skill;
    }

    private String orderBy(String sort) {
        return switch (sort) {
            case "NAME" -> " ORDER BY LOWER(us.skill_name) ASC, us.created_at DESC";
            case "LEVEL" -> " ORDER BY CASE us.skill_level WHEN 'EXPERT' THEN 4 WHEN 'ADVANCED' THEN 3 WHEN 'INTERMEDIATE' THEN 2 ELSE 1 END DESC, us.created_at DESC";
            default -> " ORDER BY us.created_at DESC";
        };
    }
}
