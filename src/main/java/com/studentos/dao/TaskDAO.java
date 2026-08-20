package com.studentos.dao;

import com.studentos.model.Task;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {
    public List<Task> getTasksByUserId(int userId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Task task = new Task();
                    task.setId(rs.getInt("id"));
                    task.setUserId(rs.getInt("user_id"));
                    task.setTitle(rs.getString("title"));
                    task.setDescription(rs.getString("description"));
                    task.setStatus(rs.getString("status"));
                    task.setPriority(rs.getString("priority"));
                    tasks.add(task);
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to load tasks: " + e.getMessage());
        }
        return tasks;
    }

    public boolean createTask(Task task) {
        String sql = "INSERT INTO tasks (user_id, title, description, status, priority) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, task.getUserId());
            ps.setString(2, task.getTitle());
            ps.setString(3, task.getDescription());
            ps.setString(4, task.getStatus() != null ? task.getStatus() : "TODO");
            ps.setString(5, task.getPriority() != null ? task.getPriority() : "MEDIUM");
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Unable to create task: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTaskStatus(int taskId, int userId, String status) {
        String sql = "UPDATE tasks SET status = ? WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, taskId);
            ps.setInt(3, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Unable to update task status: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteCompletedTask(int taskId, int userId) {
        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ? AND status = 'COMPLETED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Unable to delete completed task: " + e.getMessage());
            return false;
        }
    }
}
