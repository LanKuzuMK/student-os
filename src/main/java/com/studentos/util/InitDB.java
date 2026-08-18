package com.studentos.util;

import java.sql.Connection;
import java.sql.Statement;

public class InitDB {
    public static void init() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            try {
                stmt.executeQuery("SELECT 1 FROM users LIMIT 1");
                return; 
            } catch (Exception e) {}
            
            String schema = "CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT UNIQUE, password_hash TEXT, role TEXT DEFAULT 'STUDENT', status TEXT DEFAULT 'ACTIVE', created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE profiles (user_id INTEGER PRIMARY KEY, first_name TEXT, last_name TEXT, avatar_url TEXT, bio TEXT, university TEXT, major TEXT, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, title TEXT, description TEXT, status TEXT DEFAULT 'TODO', priority TEXT DEFAULT 'MEDIUM', due_date DATETIME, category TEXT, created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE user_skills (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, skill_name TEXT, skill_level TEXT DEFAULT 'INTERMEDIATE', type TEXT DEFAULT 'TEACH', created_at DATETIME DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE jobs (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, title TEXT, description TEXT, budget REAL, deadline DATETIME, status TEXT DEFAULT 'OPEN', created_at DATETIME DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE services (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, title TEXT, description TEXT, starting_price REAL, created_at DATETIME DEFAULT CURRENT_TIMESTAMP);" +
            "CREATE TABLE messages (id INTEGER PRIMARY KEY AUTOINCREMENT, sender_id INTEGER, receiver_id INTEGER, content TEXT, is_read INTEGER DEFAULT 0, created_at DATETIME DEFAULT CURRENT_TIMESTAMP);";
            
            for (String sql : schema.split(";")) {
                if (sql.trim().length() > 0) stmt.execute(sql);
            }
            
            String passHash = ".o1QzK467I7tP0dI2J.c.D.6lX.R37mXnE.Sg.0l3o/uJ.IeX3c6";
            
            String seed = "INSERT INTO users (id, email, password_hash, role) VALUES (1, 'admin@example.com', '" + passHash + "', 'ADMIN');" +
            "INSERT INTO users (id, email, password_hash, role) VALUES (2, 'student@example.com', '" + passHash + "', 'STUDENT');" +
            "INSERT INTO users (id, email, password_hash, role) VALUES (3, 'mong@example.com', '" + passHash + "', 'STUDENT');" +
            "INSERT INTO profiles (user_id, first_name, last_name, bio, major) VALUES (2, 'Demo', 'Student', 'Just a demo user', 'Computer Science');" +
            "INSERT INTO tasks (user_id, title, description, status, priority) VALUES (3, 'Master Java', 'Finish the Java Web course', 'IN_PROGRESS', 'HIGH');" +
            "INSERT INTO user_skills (user_id, skill_name, skill_level, type) VALUES (3, 'Java', 'ADVANCED', 'TEACH');" +
            "INSERT INTO jobs (user_id, title, description, budget) VALUES (2, 'Logo Design', 'I need a logo for my startup.', 50.00);";
            
            for (String sql : seed.split(";")) {
                if (sql.trim().length() > 0) stmt.execute(sql);
            }
        } catch (Exception e) {}
    }
}
