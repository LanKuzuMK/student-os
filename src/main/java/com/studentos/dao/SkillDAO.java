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
                + "LEFT JOIN profiles p ON p.user_id = us.user_id ORDER BY us.created_at DESC";
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
}
