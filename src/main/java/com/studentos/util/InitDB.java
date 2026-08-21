package com.studentos.util;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Creates the PostgreSQL tables required by Student OS without altering existing data.
 */
public final class InitDB {
    private InitDB() {
    }

    public static void init() throws Exception {
        String[] statements = {
                "CREATE TABLE IF NOT EXISTS users ("
                        + "id SERIAL PRIMARY KEY, "
                        + "email VARCHAR(255) UNIQUE NOT NULL, "
                        + "password_hash VARCHAR(255) NOT NULL, "
                        + "role VARCHAR(50) NOT NULL DEFAULT 'STUDENT', "
                        + "status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE', "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                        + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE TABLE IF NOT EXISTS email_verifications ("
                        + "email VARCHAR(255) PRIMARY KEY, "
                        + "code_hash VARCHAR(255) NOT NULL, "
                        + "expires_at TIMESTAMP NOT NULL, "
                        + "attempts INTEGER NOT NULL DEFAULT 0, "
                        + "issued_at TIMESTAMP NOT NULL"
                        + ")",
                "CREATE TABLE IF NOT EXISTS profiles ("
                        + "user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE, "
                        + "first_name VARCHAR(100), "
                        + "last_name VARCHAR(100), "
                        + "avatar_url VARCHAR(500), "
                        + "bio TEXT, "
                        + "university VARCHAR(255), "
                        + "major VARCHAR(255), "
                        + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE TABLE IF NOT EXISTS tasks ("
                        + "id SERIAL PRIMARY KEY, "
                        + "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "title VARCHAR(255) NOT NULL, "
                        + "description TEXT, "
                        + "status VARCHAR(50) NOT NULL DEFAULT 'TODO', "
                        + "priority VARCHAR(50) NOT NULL DEFAULT 'MEDIUM', "
                        + "due_date TIMESTAMP, "
                        + "category VARCHAR(100), "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                        + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE TABLE IF NOT EXISTS user_skills ("
                        + "id SERIAL PRIMARY KEY, "
                        + "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "skill_name VARCHAR(100) NOT NULL, "
                        + "skill_level VARCHAR(50) NOT NULL DEFAULT 'INTERMEDIATE', "
                        + "type VARCHAR(20) NOT NULL DEFAULT 'TEACH', "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE TABLE IF NOT EXISTS jobs ("
                        + "id SERIAL PRIMARY KEY, "
                        + "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "title VARCHAR(255) NOT NULL, "
                        + "description TEXT NOT NULL, "
                        + "budget DECIMAL(10,2), "
                        + "deadline TIMESTAMP, "
                        + "status VARCHAR(50) NOT NULL DEFAULT 'OPEN', "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE TABLE IF NOT EXISTS services ("
                        + "id SERIAL PRIMARY KEY, "
                        + "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "title VARCHAR(255) NOT NULL, "
                        + "description TEXT NOT NULL, "
                        + "starting_price DECIMAL(10,2), "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE TABLE IF NOT EXISTS messages ("
                        + "id SERIAL PRIMARY KEY, "
                        + "sender_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "receiver_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "content TEXT NOT NULL, "
                        + "is_read BOOLEAN NOT NULL DEFAULT FALSE, "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE TABLE IF NOT EXISTS goals ("
                        + "id SERIAL PRIMARY KEY, "
                        + "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "title VARCHAR(255) NOT NULL, "
                        + "description TEXT, "
                        + "progress INTEGER NOT NULL DEFAULT 0 CHECK (progress BETWEEN 0 AND 100), "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                        + "updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "ALTER TABLE profiles ADD COLUMN IF NOT EXISTS portfolio_url VARCHAR(500)",
                "ALTER TABLE profiles ADD COLUMN IF NOT EXISTS linkedin_url VARCHAR(500)",
                "ALTER TABLE profiles ADD COLUMN IF NOT EXISTS telegram_url VARCHAR(500)",
                "ALTER TABLE profiles ADD COLUMN IF NOT EXISTS avatar_data BYTEA",
                "ALTER TABLE profiles ADD COLUMN IF NOT EXISTS avatar_content_type VARCHAR(100)",
                "CREATE TABLE IF NOT EXISTS profile_links ("
                        + "id SERIAL PRIMARY KEY, "
                        + "user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "label VARCHAR(80) NOT NULL, "
                        + "url VARCHAR(500) NOT NULL, "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE INDEX IF NOT EXISTS idx_profile_links_user_id ON profile_links(user_id)",
                "CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id)",
                "CREATE INDEX IF NOT EXISTS idx_user_skills_user_id ON user_skills(user_id)",
                "CREATE INDEX IF NOT EXISTS idx_jobs_created_at ON jobs(created_at DESC)",
                "ALTER TABLE messages ALTER COLUMN is_read DROP DEFAULT",
                "ALTER TABLE messages ALTER COLUMN is_read TYPE BOOLEAN USING CASE "
                        + "WHEN LOWER(is_read::text) IN ('1', 'true', 't', 'yes', 'y') THEN TRUE ELSE FALSE END",
                "ALTER TABLE messages ALTER COLUMN is_read SET DEFAULT FALSE",
                "ALTER TABLE messages ALTER COLUMN is_read SET NOT NULL",
                "ALTER TABLE messages ADD COLUMN IF NOT EXISTS sender_deleted BOOLEAN NOT NULL DEFAULT FALSE",
                "ALTER TABLE messages ADD COLUMN IF NOT EXISTS receiver_deleted BOOLEAN NOT NULL DEFAULT FALSE",
                "CREATE INDEX IF NOT EXISTS idx_messages_receiver_id ON messages(receiver_id)",
                "CREATE INDEX IF NOT EXISTS idx_goals_user_id ON goals(user_id)"
        };

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
            rotateAdministratorIfRequested(connection);
        }
    }

    private static void rotateAdministratorIfRequested(Connection connection) throws Exception {
        String email = System.getenv("ADMIN_ROTATION_EMAIL");
        String password = System.getenv("ADMIN_ROTATION_PASSWORD");
        if (email == null || email.isBlank() || password == null || password.length() < 12) {
            return;
        }

        String sql = "UPDATE users SET password_hash = ?, status = 'ACTIVE', updated_at = CURRENT_TIMESTAMP "
                + "WHERE LOWER(email) = LOWER(?) AND role = 'ADMIN'";
        try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, BCryptUtil.hash(password));
            statement.setString(2, email.trim());
            int updated = statement.executeUpdate();
            System.out.println(updated == 1
                    ? "Administrator credential rotation completed. Remove the rotation secrets from the deployment environment."
                    : "Administrator credential rotation skipped because no matching administrator account was found.");
        }
    }
}
