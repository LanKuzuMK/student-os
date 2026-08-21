package com.studentos.service;
import com.studentos.dao.UserDAO;
import com.studentos.dao.EmailVerificationDAO;
import com.studentos.model.User;
import com.studentos.util.DBConnection;
import com.studentos.util.InputValidator;
import com.studentos.util.VerificationCodeUtil;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    private UserDAO userDAO = new UserDAO();
    private final EmailVerificationDAO verificationDAO = new EmailVerificationDAO();
    private final EmailService emailService = new EmailService();

    public User login(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null || "BANNED".equals(user.getStatus())) {
            return null;
        }

        try {
            return BCrypt.checkpw(password, user.getPasswordHash()) ? user : null;
        } catch (IllegalArgumentException exception) {
            // Legacy malformed hashes are rejected safely; InitDB repairs the known demo records at startup.
            return null;
        }
    }

    public User registerUser(String email, String password, String firstName, String lastName) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        if (!InputValidator.isValidEmail(normalizedEmail) || password == null || password.length() < 8) {
            return null;
        }

        String insertUser = "INSERT INTO users (email, password_hash, role) VALUES (?, ?, 'STUDENT') "
                + "RETURNING id, email, role, status, created_at";
        String insertProfile = "INSERT INTO profiles (user_id, first_name, last_name) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement userStatement = connection.prepareStatement(insertUser)) {
                userStatement.setString(1, normalizedEmail);
                userStatement.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
                try (ResultSet result = userStatement.executeQuery()) {
                    if (!result.next()) {
                        connection.rollback();
                        return null;
                    }
                    try (PreparedStatement profileStatement = connection.prepareStatement(insertProfile)) {
                        profileStatement.setInt(1, result.getInt("id"));
                        profileStatement.setString(2, firstName);
                        profileStatement.setString(3, lastName);
                        profileStatement.executeUpdate();
                    }

                    User user = new User();
                    user.setId(result.getInt("id"));
                    user.setEmail(result.getString("email"));
                    user.setRole(result.getString("role"));
                    user.setStatus(result.getString("status"));
                    user.setCreatedAt(result.getTimestamp("created_at"));
                    connection.commit();
                    return user;
                }
            } catch (SQLException exception) {
                connection.rollback();
                return null;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException exception) {
            return null;
        }
    }

    public boolean startEmailVerification(String email) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        if (!InputValidator.isValidEmail(normalizedEmail) || userDAO.findByEmail(normalizedEmail) != null || !emailService.isConfigured()) {
            return false;
        }
        String code = VerificationCodeUtil.generateSixDigitCode();
        if (!verificationDAO.createOrReplace(normalizedEmail, code)) {
            return false;
        }
        if (emailService.sendVerificationCode(normalizedEmail, code)) {
            return true;
        }
        verificationDAO.delete(normalizedEmail);
        return false;
    }

    public boolean verifyEmailCode(String email, String code) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase();
        return InputValidator.isValidEmail(normalizedEmail)
                && VerificationCodeUtil.isSixDigitCode(code)
                && verificationDAO.consumeIfValid(normalizedEmail, code);
    }
}
