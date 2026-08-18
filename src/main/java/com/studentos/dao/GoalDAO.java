package com.studentos.dao;
import com.studentos.model.Goal;
import com.studentos.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoalDAO {
    public List<Goal> getGoalsByUserId(int userId) {
        List<Goal> goals = new ArrayList<>();
        // Using tasks table as a quick hack for goals if goals table isn't created perfectly, 
        // but we DID create a goals table earlier. Wait, in InitDB we didn't add goals table!
        // Let's create it if not exists.
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS goals (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, title TEXT, description TEXT, progress INTEGER DEFAULT 0)");
        } catch (Exception e) {}

        String sql = "SELECT * FROM goals WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Goal g = new Goal();
                g.setId(rs.getInt("id"));
                g.setUserId(rs.getInt("user_id"));
                g.setTitle(rs.getString("title"));
                g.setDescription(rs.getString("description"));
                g.setProgress(rs.getInt("progress"));
                goals.add(g);
            }
        } catch (SQLException e) {}
        return goals;
    }

    public boolean createGoal(Goal goal) {
        String sql = "INSERT INTO goals (user_id, title, description, progress) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, goal.getUserId());
            ps.setString(2, goal.getTitle());
            ps.setString(3, goal.getDescription());
            ps.setInt(4, goal.getProgress());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {}
        return false;
    }
}
