package com.studentos.dao;

import com.studentos.model.Profile;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileDAO {
    private static final String PROFILE_SELECT = "SELECT u.id AS user_id, u.email, p.first_name, p.last_name, p.bio, "
            + "p.university, p.major, p.portfolio_url, p.linkedin_url, p.telegram_url, "
            + "(p.avatar_data IS NOT NULL) AS has_avatar "
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

    public boolean updateProfile(Profile profile, byte[] compressedAvatar) {
        String sql = "INSERT INTO profiles (user_id, first_name, last_name, bio, university, major, "
                + "portfolio_url, linkedin_url, telegram_url, avatar_data, avatar_content_type, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) "
                + "ON CONFLICT (user_id) DO UPDATE SET first_name = EXCLUDED.first_name, "
                + "last_name = EXCLUDED.last_name, bio = EXCLUDED.bio, university = EXCLUDED.university, "
                + "major = EXCLUDED.major, portfolio_url = EXCLUDED.portfolio_url, "
                + "linkedin_url = EXCLUDED.linkedin_url, telegram_url = EXCLUDED.telegram_url, "
                + "avatar_data = COALESCE(EXCLUDED.avatar_data, profiles.avatar_data), "
                + "avatar_content_type = COALESCE(EXCLUDED.avatar_content_type, profiles.avatar_content_type), "
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
            if (compressedAvatar == null) {
                ps.setNull(10, java.sql.Types.BINARY);
                ps.setNull(11, java.sql.Types.VARCHAR);
            } else {
                ps.setBytes(10, compressedAvatar);
                ps.setString(11, "image/jpeg");
            }
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("Unable to update profile: " + e.getMessage());
            return false;
        }
    }

    public Avatar getAvatar(int userId) {
        String sql = "SELECT avatar_data, avatar_content_type FROM profiles WHERE user_id = ? AND avatar_data IS NOT NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Avatar(rs.getBytes("avatar_data"), rs.getString("avatar_content_type"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Unable to load avatar: " + e.getMessage());
        }
        return null;
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
        profile.setHasAvatar(rs.getBoolean("has_avatar"));
        return profile;
    }

    public static final class Avatar {
        private final byte[] data;
        private final String contentType;

        public Avatar(byte[] data, String contentType) {
            this.data = data;
            this.contentType = contentType;
        }

        public byte[] getData() { return data; }
        public String getContentType() { return contentType; }
    }
}
