package com.studentos.dao;

import com.studentos.model.Job;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JobDAO {
    public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT j.*, u.email AS owner_email, "
                + "COALESCE(NULLIF(TRIM(CONCAT_WS(' ', p.first_name, p.last_name)), ''), u.email) AS owner_name "
                + "FROM jobs j JOIN users u ON u.id = j.user_id "
                + "LEFT JOIN profiles p ON p.user_id = j.user_id ORDER BY j.created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Job job = mapJob(rs);
                job.setOwnerName(rs.getString("owner_name"));
                job.setOwnerEmail(rs.getString("owner_email"));
                jobs.add(job);
            }
        } catch (SQLException e) {
            System.err.println("Unable to load freelance jobs: " + e.getMessage());
        }
        return jobs;
    }

    public boolean createJob(Job job) {
        String sql = "INSERT INTO jobs (user_id, title, description, budget) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, job.getUserId());
            ps.setString(2, job.getTitle());
            ps.setString(3, job.getDescription());
            ps.setDouble(4, job.getBudget());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Unable to create freelance job: " + e.getMessage());
            return false;
        }
    }

    private Job mapJob(ResultSet rs) throws SQLException {
        Job job = new Job();
        job.setId(rs.getInt("id"));
        job.setUserId(rs.getInt("user_id"));
        job.setTitle(rs.getString("title"));
        job.setDescription(rs.getString("description"));
        job.setBudget(rs.getDouble("budget"));
        job.setStatus(rs.getString("status"));
        job.setCreatedAt(rs.getTimestamp("created_at"));
        return job;
    }
}
