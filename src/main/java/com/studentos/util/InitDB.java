package com.studentos.util;

import java.sql.Connection;
import java.sql.Statement;

public class InitDB {
    public static void init() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            try {
                stmt.executeQuery("SELECT 1 FROM users LIMIT 1");
                return; // Already initialized
            } catch (Exception e) {
                // Table doesn't exist, proceed with initialization
            }
            
            System.out.println("Initializing PostgreSQL Schema...");
            
            String schema = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, email VARCHAR(255) UNIQUE NOT NULL, password_hash VARCHAR(255) NOT NULL, role VARCHAR(50) DEFAULT 'STUDENT', status VARCHAR(50) DEFAULT 'ACTIVE', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE IF NOT EXISTS profiles (user_id INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE, first_name VARCHAR(100), last_name VARCHAR(100), avatar_url TEXT, bio TEXT, university VARCHAR(255), major VARCHAR(255), updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE IF NOT EXISTS tasks (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, title VARCHAR(255) NOT NULL, description TEXT, status VARCHAR(50) DEFAULT 'TODO', priority VARCHAR(50) DEFAULT 'MEDIUM', due_date TIMESTAMP, category VARCHAR(100), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE IF NOT EXISTS user_skills (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, skill_name VARCHAR(100) NOT NULL, skill_level VARCHAR(50) DEFAULT 'INTERMEDIATE', type VARCHAR(50) DEFAULT 'TEACH', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE IF NOT EXISTS jobs (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, title VARCHAR(255) NOT NULL, description TEXT, budget DECIMAL(10,2), deadline TIMESTAMP, status VARCHAR(50) DEFAULT 'OPEN', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE IF NOT EXISTS services (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, title VARCHAR(255) NOT NULL, description TEXT, starting_price DECIMAL(10,2), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE IF NOT EXISTS messages (id SERIAL PRIMARY KEY, sender_id INTEGER REFERENCES users(id) ON DELETE CASCADE, receiver_id INTEGER REFERENCES users(id) ON DELETE CASCADE, content TEXT NOT NULL, is_read INTEGER DEFAULT 0, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE IF NOT EXISTS goals (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES users(id) ON DELETE CASCADE, title VARCHAR(255) NOT NULL, description TEXT, progress INTEGER DEFAULT 0);";
            
            for (String sql : schema.split(";")) {
                if (sql.trim().length() > 0) stmt.execute(sql);
            }
            
            System.out.println("Seeding Database...");
            
            String passHash = ".o1QzK467I7tP0dI2J.c.D.6lX.R37mXnE.Sg.0l3o/uJ.IeX3c6"; // 'password'
            
            String seed = "INSERT INTO users (email, password_hash, role) VALUES ('admin@example.com', '" + passHash + "', 'ADMIN') ON CONFLICT DO NOTHING;" +
            "INSERT INTO users (email, password_hash, role) VALUES ('student@example.com', '" + passHash + "', 'STUDENT') ON CONFLICT DO NOTHING;" +
            "INSERT INTO users (email, password_hash, role) VALUES ('mong@example.com', '" + passHash + "', 'STUDENT') ON CONFLICT DO NOTHING;" +
            "INSERT INTO profiles (user_id, first_name, last_name, bio, major) VALUES (2, 'Demo', 'Student', 'Just a demo user', 'Computer Science') ON CONFLICT DO NOTHING;" +
            "INSERT INTO tasks (user_id, title, description, status, priority) VALUES (3, 'Master Java', 'Finish the Java Web course', 'IN_PROGRESS', 'HIGH');" +
            "INSERT INTO user_skills (user_id, skill_name, skill_level, type) VALUES (3, 'Java', 'ADVANCED', 'TEACH');" +
            "INSERT INTO jobs (user_id, title, description, budget) VALUES (2, 'Logo Design', 'I need a logo for my startup.', 50.00);";
            
            for (String sql : seed.split(";")) {
                if (sql.trim().length() > 0) stmt.execute(sql);
            }
            
            System.out.println("PostgreSQL Initialization Complete!");
            
        } catch (Exception e) {
            System.err.println("Database initialization skipped or failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
