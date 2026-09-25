package com.studentos.dao;

import com.studentos.model.Profile;
import com.studentos.model.ProfileLink;
import com.studentos.model.ProfileProject;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProfileDAO {
    private static final String PROFILE_SELECT = "SELECT u.id AS user_id, u.email, p.first_name, p.last_name, p.bio, "
            + "p.university, p.major, p.portfolio_url, p.linkedin_url, p.telegram_url, p.availability_status, p.collaboration_preferences, "
            + "FROM users u LEFT JOIN profiles p ON p.user_id = u.id WHERE u.id = ?";

    public Profile getByUserId(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(PROFILE_SELECT)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapProfile(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("Unable to load profile: " + e.getMessage());
            return null;
        }
    }

    public boolean updateProfile(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, bio, university, major, "
                + "portfolio_url, linkedin_url, telegram_url, availability_status, collaboration_preferences, updated_at) "
                + "VALUES (?, CURRENT_TIMESTAMP) "
                + "ON CONFLICT (user_id) DO UPDATE SET first_name = EXCLUDED.first_name, "
                + "last_name = EXCLUDED.last_name, bio = EXCLUDED.bio, university = EXCLUDED.university, "
                + "major = EXCLUDED.major, portfolio_url = EXCLUDED.portfolio_url, "
                + "linkedin_url = EXCLUDED.linkedin_url, telegram_url = EXCLUDED.telegram_url, availability_status = EXCLUDED.availability_status, "
                + "collaboration_preferences = EXCLUDED.collaboration_preferences, "
                
                + "updated_at = CURRENT_TIMESTAMP";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, profile.getUserId());
            ps.setString(2, profile.getFirstName());
            ps.setString(3, profile.getLastName());
            ps.setString(4, profile.getBio());
            ps.setString(5, profile.getUniversity());
            ps.setString(6, profile.getMajor());
            ps.setString(7, profile.getPortfolioUrl());
            ps.setString(8, profile.getLinkedinUrl());
            ps.setString(9, profile.getTelegramUrl());
            ps.setString(10, profile.getAvailabilityStatus());
            ps.setString(11, profile.getCollaborationPreferences());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Unable to update profile: " + e.getMessage());
            return false;
        }
    }
    
    

    public List<ProfileLink> getLinksByUserId(int userId) {
        String sql = "SELECT id, user_id, label, url FROM profile_links WHERE user_id = ? ORDER BY id ASC";
        List<ProfileLink> links = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProfileLink link = new ProfileLink();
                    link.setId(rs.getInt("id"));
                    link.setUserId(rs.getInt("user_id"));
                    link.setLabel(rs.getString("label"));
                    link.setUrl(rs.getString("url"));
                    links.add(link);
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to load profile links: " + e.getMessage());
        }
        return links;
    }

    public boolean addLink(int userId, String label, String url) {
        String sql = "INSERT INTO profile_links (user_id, label, url) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, label);
            ps.setString(3, url);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Unable to add profile link: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteLink(int linkId, int userId) {
        String sql = "DELETE FROM profile_links WHERE id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, linkId);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Unable to delete profile link: " + e.getMessage());
            return false;
        }
    }

    public List<ProfileProject> getProjectsByUserId(int userId) {
        String sql = "SELECT id, user_id, title, description, url FROM profile_projects WHERE user_id = ? ORDER BY id DESC";
        List<ProfileProject> projects = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProfileProject project = new ProfileProject();
                    project.setId(rs.getInt("id"));
                    project.setUserId(rs.getInt("user_id"));
                    project.setTitle(rs.getString("title"));
                    project.setDescription(rs.getString("description"));
                    project.setUrl(rs.getString("url"));
                    projects.add(project);
                }
            }
        } catch (SQLException e) { System.err.println("Unable to load profile projects: " + e.getMessage()); }
        return projects;
    }

    public boolean addProject(int userId, String title, String description, String url) {
        String sql = "INSERT INTO profile_projects (user_id, title, description, url) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setString(4, url);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { System.err.println("Unable to add profile project: " + e.getMessage()); return false; }
    }

    public boolean deleteProject(int projectId, int userId) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement("DELETE FROM profile_projects WHERE id = ? AND user_id = ?")) {
            ps.setInt(1, projectId);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) { System.err.println("Unable to delete profile project: " + e.getMessage()); return false; }
    }

    

    private Profile mapProfile(ResultSet rs) throws SQLException {
        Profile profile = new Profile();
        profile.setUserId(rs.getInt("user_id"));
        profile.setEmail(rs.getString("email"));
        profile.setFirstName(rs.getString("first_name"));
        profile.setLastName(rs.getString("last_name"));
        profile.setBio(rs.getString("bio"));
        profile.setUniversity(rs.getString("university"));
        profile.setMajor(rs.getString("major"));
        profile.setPortfolioUrl(rs.getString("portfolio_url"));
        profile.setLinkedinUrl(rs.getString("linkedin_url"));
        profile.setTelegramUrl(rs.getString("telegram_url"));
        profile.setAvailabilityStatus(rs.getString("availability_status"));
        profile.setCollaborationPreferences(rs.getString("collaboration_preferences"));
        
        return profile;
    }
}

