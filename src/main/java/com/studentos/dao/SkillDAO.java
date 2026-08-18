package com.studentos.dao;
import com.studentos.model.Skill;
import com.studentos.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkillDAO {
    public List<Skill> getSkillsByUserId(int userId) {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT * FROM user_skills WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Skill s = new Skill();
                s.setId(rs.getInt("id"));
                s.setUserId(rs.getInt("user_id"));
                s.setSkillName(rs.getString("skill_name"));
                s.setSkillLevel(rs.getString("skill_level"));
                s.setType(rs.getString("type"));
                skills.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return skills;
    }

    public List<Skill> getAllSkills() {
        List<Skill> skills = new ArrayList<>();
        String sql = "SELECT * FROM user_skills";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Skill s = new Skill();
                s.setId(rs.getInt("id"));
                s.setUserId(rs.getInt("user_id"));
                s.setSkillName(rs.getString("skill_name"));
                s.setSkillLevel(rs.getString("skill_level"));
                s.setType(rs.getString("type"));
                skills.add(s);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return skills;
    }

    public boolean addSkill(Skill skill) {
        String sql = "INSERT INTO user_skills (user_id, skill_name, skill_level, type) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, skill.getUserId());
            ps.setString(2, skill.getSkillName());
            ps.setString(3, skill.getSkillLevel());
            ps.setString(4, skill.getType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
