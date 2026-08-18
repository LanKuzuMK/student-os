package com.studentos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class DBConnection {
    // For SQLite, we don't strictly need HikariCP for a local demo.
    // We'll use standard JDBC connection to a local sqlite file.
    
    private static String getDbUrl() {
        // Find absolute path to database/studentos.db
        // In Tomcat, user.dir is often Tomcat's bin dir.
        // It's safer to use a temp dir or absolute path.
        String tempDir = System.getProperty("java.io.tmpdir");
        return "jdbc:sqlite:" + tempDir + File.separator + "studentos.db";
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(getDbUrl());
        } catch (ClassNotFoundException e) {
            throw new SQLException(e);
        }
    }
}
