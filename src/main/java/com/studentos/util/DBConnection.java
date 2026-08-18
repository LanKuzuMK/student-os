package com.studentos.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.net.URI;

public class DBConnection {
    private static HikariDataSource dataSource;

    static {
        try {
            Class.forName("org.postgresql.Driver");
            HikariConfig config = new HikariConfig();
            
            String dbUrl = System.getenv("DATABASE_URL");
            if (dbUrl != null) dbUrl = dbUrl.trim();
            
            if (dbUrl == null || dbUrl.isEmpty()) {
                config.setJdbcUrl("jdbc:postgresql://localhost:5432/studentos");
                config.setUsername("postgres");
                config.setPassword("postgres");
            } else if (dbUrl.startsWith("postgresql://") || dbUrl.startsWith("postgres://")) {
                URI uri = new URI(dbUrl.replace("postgresql://", "http://").replace("postgres://", "http://"));
                String host = uri.getHost();
                int port = uri.getPort() != -1 ? uri.getPort() : 5432;
                String path = uri.getPath();
                String query = uri.getQuery() != null ? "?" + uri.getQuery() : "";
                String[] auth = uri.getUserInfo() != null ? uri.getUserInfo().split(":") : new String[]{"", ""};
                
                config.setJdbcUrl("jdbc:postgresql://" + host + ":" + port + path + query);
                config.setUsername(auth[0]);
                if (auth.length > 1) {
                    config.setPassword(auth[1]);
                }
            } else {
                config.setJdbcUrl(dbUrl);
            }

            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(30000);
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(30000);
            
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
