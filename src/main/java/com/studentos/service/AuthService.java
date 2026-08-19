package com.studentos.service;
import com.studentos.dao.UserDAO;
import com.studentos.model.User;
import com.studentos.util.DBConnection;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AuthService {
    private UserDAO userDAO = new UserDAO();

    public User login(String email, String password) {
        User user = userDAO.findByEmail(email);
        if (user == null) {
            return null;
        }

        try {
            return BCrypt.checkpw(password, user.getPasswordHash()) ? user : null;
        } catch (IllegalArgumentException exception) {
            // Legacy malformed hashes are rejected safely; InitDB repairs the known demo records at startup.
            return null;
        }
    }

    public User registerUser(String email, String password, String role, String firstName, String lastName) {
        if (userDAO.findByEmail(email) != null) {
            return null; // Email exists
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        newUser.setRole(role != null ? role : "STUDENT");
        
        if (userDAO.createUser(newUser)) {
            User savedUser = userDAO.findByEmail(email);
            // Optional: Insert into profiles using JDBC directly for simplicity
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO profiles (user_id, first_name, last_name) VALUES (?, ?, ?)")) {
                ps.setInt(1, savedUser.getId());
                ps.setString(2, firstName);
                ps.setString(3, lastName);
                ps.executeUpdate();
            } catch (Exception e) {}
            return savedUser;
        }
        return null;
    }
}
