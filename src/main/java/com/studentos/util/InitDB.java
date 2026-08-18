package com.studentos.util;

import java.sql.Connection;
import java.sql.Statement;

public class InitDB {
    public static void init() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            String schema = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, email VARCHAR(255) UNIQUE NOT NULL, password_hash VARCHAR(255) NOT NULL, role VARCHAR(50) DEFAULT 'STUDENT', status VARCHAR(50) DEFAULT 'ACTIVE', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);" +
                            "CREATE TABLE IF NOT EXISTS skills (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES users(id), name VARCHAR(100) NOT NULL, category VARCHAR(50) NOT NULL, proficiency VARCHAR(20) NOT NULL);" +
                            "CREATE TABLE IF NOT EXISTS tasks (id SERIAL PRIMARY KEY, user_id INTEGER REFERENCES users(id), title VARCHAR(200) NOT NULL, description TEXT, status VARCHAR(20) DEFAULT 'PENDING', due_date TIMESTAMP);" +
                            "CREATE TABLE IF NOT EXISTS jobs (id SERIAL PRIMARY KEY, client_id INTEGER REFERENCES users(id), title VARCHAR(200) NOT NULL, description TEXT, budget DECIMAL(10,2), status VARCHAR(20) DEFAULT 'OPEN');" +
                            "CREATE TABLE IF NOT EXISTS messages (id SERIAL PRIMARY KEY, sender_id INTEGER REFERENCES users(id), receiver_id INTEGER REFERENCES users(id), content TEXT NOT NULL, sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, read_status BOOLEAN DEFAULT FALSE);";
            
            stmt.executeUpdate(schema);
        }
    }
}
