package com.studentos.dao;
import com.studentos.model.Job;
import com.studentos.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {
    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM jobs ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Job j = new Job();
                j.setId(rs.getInt("id"));
                j.setUserId(rs.getInt("user_id"));
                j.setTitle(rs.getString("title"));
                j.setDescription(rs.getString("description"));
                j.setBudget(rs.getDouble("budget"));
                j.setStatus(rs.getString("status"));
                j.setCreatedAt(rs.getTimestamp("created_at"));
                jobs.add(j);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return jobs;
    }

    public boolean createJob(Job job) {
        String sql = "INSERT INTO jobs (user_id, title, description, budget) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, job.getUserId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getDescription());
            ps.setDouble(4, job.getBudget());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
