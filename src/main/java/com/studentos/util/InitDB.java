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
                "UPDATE users SET password_hash = '$2a$10$hAZHFcGYO4y32HJ.bPPGIemUljIzF2XGeW7HDCEJBwb4Ygnuv4Vny' "
                        + "WHERE email IN ('admin@example.com', 'student@example.com', 'mong@example.com', 'dara@example.com', 'sokha@example.com') "
                        + "AND (password_hash NOT LIKE '$2%' "
                        + "OR password_hash = '$2b$12$f92XfVIePffZBEmB82qgkO.fx5ejqoqZEDRovE0xg.X.1D10M8KuK')",
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
                "CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id)",
                "CREATE INDEX IF NOT EXISTS idx_user_skills_user_id ON user_skills(user_id)",
                "CREATE INDEX IF NOT EXISTS idx_jobs_created_at ON jobs(created_at DESC)",
                "ALTER TABLE messages ALTER COLUMN is_read TYPE BOOLEAN USING CASE "
                        + "WHEN LOWER(is_read::text) IN ('1', 'true', 't', 'yes', 'y') THEN TRUE ELSE FALSE END",
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
        }
    }
}
