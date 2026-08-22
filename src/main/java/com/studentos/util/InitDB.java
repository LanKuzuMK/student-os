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
                        + "auth_version INTEGER NOT NULL DEFAULT 1, "
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
                "CREATE TABLE IF NOT EXISTS password_reset_codes ("
                        + "email VARCHAR(255) PRIMARY KEY REFERENCES users(email) ON DELETE CASCADE, "
                        + "code_hash VARCHAR(255) NOT NULL, "
                        + "expires_at TIMESTAMP NOT NULL, "
                        + "attempts INTEGER NOT NULL DEFAULT 0, "
                        + "issued_at TIMESTAMP NOT NULL"
                        + ")",
                "ALTER TABLE users ADD COLUMN IF NOT EXISTS auth_version INTEGER NOT NULL DEFAULT 1",
                "CREATE TABLE IF NOT EXISTS notifications ("
                        + "id SERIAL PRIMARY KEY, recipient_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "type VARCHAR(40) NOT NULL, title VARCHAR(160) NOT NULL, message VARCHAR(1000) NOT NULL, "
                        + "action_url VARCHAR(500), is_read BOOLEAN NOT NULL DEFAULT FALSE, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE INDEX IF NOT EXISTS idx_notifications_recipient_created ON notifications(recipient_id, is_read, created_at DESC)",
                "CREATE TABLE IF NOT EXISTS collaboration_requests ("
                        + "id SERIAL PRIMARY KEY, requester_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "recipient_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, request_type VARCHAR(40) NOT NULL, "
                        + "title VARCHAR(120) NOT NULL, description VARCHAR(1000) NOT NULL, expected_commitment VARCHAR(120), "
                        + "status VARCHAR(20) NOT NULL DEFAULT 'PENDING', response_note VARCHAR(500), "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, responded_at TIMESTAMP, "
                        + "CHECK (requester_id <> recipient_id), CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'CANCELLED'))"
                        + ")",
                "CREATE UNIQUE INDEX IF NOT EXISTS uq_collaboration_pending_request ON collaboration_requests(requester_id, recipient_id, title) WHERE status = 'PENDING'",
                "CREATE INDEX IF NOT EXISTS idx_collaboration_recipient_status ON collaboration_requests(recipient_id, status, created_at DESC)",
                "CREATE INDEX IF NOT EXISTS idx_collaboration_requester_status ON collaboration_requests(requester_id, status, created_at DESC)",
                "CREATE TABLE IF NOT EXISTS saved_items (id SERIAL PRIMARY KEY, owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, target_type VARCHAR(20) NOT NULL, target_id INTEGER NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, CHECK (target_type IN ('PROFILE', 'SKILL', 'SERVICE', 'JOB')), UNIQUE(owner_id, target_type, target_id))",
                "CREATE INDEX IF NOT EXISTS idx_saved_items_owner_created ON saved_items(owner_id, created_at DESC)",
                "CREATE TABLE IF NOT EXISTS project_spaces (id SERIAL PRIMARY KEY, owner_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, title VARCHAR(120) NOT NULL, description VARCHAR(1000), status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, CHECK (status IN ('ACTIVE', 'ON_HOLD', 'COMPLETED')))",
                "CREATE TABLE IF NOT EXISTS project_space_members (project_id INTEGER NOT NULL REFERENCES project_spaces(id) ON DELETE CASCADE, user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, role VARCHAR(20) NOT NULL DEFAULT 'CONTRIBUTOR', joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY(project_id, user_id), CHECK (role IN ('OWNER', 'CONTRIBUTOR')))",
                "CREATE TABLE IF NOT EXISTS project_space_milestones (id SERIAL PRIMARY KEY, project_id INTEGER NOT NULL REFERENCES project_spaces(id) ON DELETE CASCADE, title VARCHAR(120) NOT NULL, description VARCHAR(500), status VARCHAR(20) NOT NULL DEFAULT 'TODO', due_date DATE, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, CHECK (status IN ('TODO', 'IN_PROGRESS', 'COMPLETED')))",
                "CREATE TABLE IF NOT EXISTS project_space_tasks (id SERIAL PRIMARY KEY, project_id INTEGER NOT NULL REFERENCES project_spaces(id) ON DELETE CASCADE, created_by INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, assignee_id INTEGER REFERENCES users(id) ON DELETE SET NULL, title VARCHAR(120) NOT NULL, description VARCHAR(1000), status VARCHAR(20) NOT NULL DEFAULT 'TODO', priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM', created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, CHECK (status IN ('TODO', 'IN_PROGRESS', 'BLOCKED', 'COMPLETED')), CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')))",
                "CREATE INDEX IF NOT EXISTS idx_project_space_members_user ON project_space_members(user_id, project_id)",
                "CREATE INDEX IF NOT EXISTS idx_project_space_tasks_project_status ON project_space_tasks(project_id, status)",
                "CREATE INDEX IF NOT EXISTS idx_project_space_milestones_project ON project_space_milestones(project_id, status)",
                "ALTER TABLE user_skills ADD COLUMN IF NOT EXISTS moderation_status VARCHAR(20) NOT NULL DEFAULT 'VISIBLE'",
                "ALTER TABLE user_skills ADD COLUMN IF NOT EXISTS moderation_note VARCHAR(1000)",
                "ALTER TABLE jobs ADD COLUMN IF NOT EXISTS moderation_status VARCHAR(20) NOT NULL DEFAULT 'VISIBLE'",
                "ALTER TABLE jobs ADD COLUMN IF NOT EXISTS moderation_note VARCHAR(1000)",
                "ALTER TABLE services ADD COLUMN IF NOT EXISTS moderation_status VARCHAR(20) NOT NULL DEFAULT 'VISIBLE'",
                "ALTER TABLE services ADD COLUMN IF NOT EXISTS moderation_note VARCHAR(1000)",
                "CREATE TABLE IF NOT EXISTS moderation_reports ("
                        + "id SERIAL PRIMARY KEY, reporter_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "target_type VARCHAR(20) NOT NULL, target_id INTEGER NOT NULL, reason VARCHAR(80) NOT NULL, details VARCHAR(1000), "
                        + "status VARCHAR(20) NOT NULL DEFAULT 'OPEN', assigned_to INTEGER REFERENCES users(id) ON DELETE SET NULL, reviewed_by INTEGER REFERENCES users(id) ON DELETE SET NULL, "
                        + "resolution_note VARCHAR(1000), created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, reviewed_at TIMESTAMP"
                        + ")",
                "ALTER TABLE moderation_reports ADD COLUMN IF NOT EXISTS assigned_to INTEGER REFERENCES users(id) ON DELETE SET NULL",
                "CREATE UNIQUE INDEX IF NOT EXISTS idx_open_moderation_report_per_reporter ON moderation_reports(reporter_id, target_type, target_id) WHERE status = 'OPEN'",
                "CREATE INDEX IF NOT EXISTS idx_moderation_reports_status ON moderation_reports(status, created_at DESC)",
                "CREATE TABLE IF NOT EXISTS moderation_audit_log ("
                        + "id SERIAL PRIMARY KEY, admin_id INTEGER REFERENCES users(id) ON DELETE SET NULL, action VARCHAR(80) NOT NULL, "
                        + "target_type VARCHAR(30) NOT NULL, target_id INTEGER NOT NULL, reason VARCHAR(1000), created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE INDEX IF NOT EXISTS idx_moderation_audit_created_at ON moderation_audit_log(created_at DESC)",
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
                "ALTER TABLE profiles ADD COLUMN IF NOT EXISTS availability_status VARCHAR(50)",
                "ALTER TABLE profiles ADD COLUMN IF NOT EXISTS collaboration_preferences VARCHAR(500)",
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
                "CREATE TABLE IF NOT EXISTS profile_projects ("
                        + "id SERIAL PRIMARY KEY, user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "title VARCHAR(120) NOT NULL, description VARCHAR(500), url VARCHAR(500) NOT NULL, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
                        + ")",
                "CREATE INDEX IF NOT EXISTS idx_profile_projects_user_id ON profile_projects(user_id)",
                "CREATE INDEX IF NOT EXISTS idx_profiles_availability ON profiles(availability_status)",
                "CREATE INDEX IF NOT EXISTS idx_user_skills_discovery ON user_skills(moderation_status, type, skill_level, created_at DESC)",
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
                "CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at DESC, id DESC)",
                "CREATE INDEX IF NOT EXISTS idx_users_email_search ON users(LOWER(email))",
                "CREATE INDEX IF NOT EXISTS idx_moderation_audit_action_created ON moderation_audit_log(action, created_at DESC)",
                "CREATE TABLE IF NOT EXISTS user_blocks ("
                        + "blocker_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "blocked_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE, "
                        + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                        + "PRIMARY KEY (blocker_id, blocked_id), CHECK (blocker_id <> blocked_id)"
                        + ")",
                "CREATE INDEX IF NOT EXISTS idx_user_blocks_blocked ON user_blocks(blocked_id, blocker_id)",
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
