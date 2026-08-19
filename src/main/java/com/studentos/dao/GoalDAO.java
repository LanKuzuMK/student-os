package com.studentos.dao;

import com.studentos.model.Goal;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {
    public List<Goal> getGoalsByUserId(int userId) {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT id, user_id, title, description, progress FROM goals WHERE user_id = ? ORDER BY id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Goal goal = new Goal();
                    goal.setId(resultSet.getInt("id"));
                    goal.setUserId(resultSet.getInt("user_id"));
                    goal.setTitle(resultSet.getString("title"));
                    goal.setDescription(resultSet.getString("description"));
                    goal.setProgress(resultSet.getInt("progress"));
                    goals.add(goal);
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to load goals: " + e.getMessage());
        }
        return goals;
    }

    public boolean createGoal(Goal goal) {
        String sql = "INSERT INTO goals (user_id, title, description, progress) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, goal.getUserId());
            statement.setString(2, goal.getTitle());
            statement.setString(3, goal.getDescription());
            statement.setInt(4, goal.getProgress());
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Unable to create goal: " + e.getMessage());
            return false;
        }
    }
}
