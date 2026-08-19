package com.studentos.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Lazily creates the PostgreSQL connection pool from environment variables.
 * DATABASE_URL is the primary production setting and accepts a Neon PostgreSQL URL.
 */
public final class DBConnection {
    private static volatile HikariDataSource dataSource;

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    private static HikariDataSource getDataSource() throws SQLException {
        HikariDataSource current = dataSource;
        if (current != null) {
            return current;
        }

        synchronized (DBConnection.class) {
            if (dataSource == null) {
                try {
                    dataSource = createDataSource();
                } catch (Exception e) {
                    throw new SQLException("Unable to initialize the PostgreSQL connection pool", e);
                }
            }
            return dataSource;
        }
    }

    private static HikariDataSource createDataSource() throws Exception {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.postgresql.Driver");

        String databaseUrl = firstNonBlank(
                System.getenv("DATABASE_URL"),
                System.getenv("JDBC_DATABASE_URL"),
                System.getenv("DB_URL"));

        if (databaseUrl == null) {
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/studentos");
            config.setUsername(firstNonBlank(System.getenv("DB_USERNAME"), "postgres"));
            config.setPassword(firstNonBlank(System.getenv("DB_PASSWORD"), "postgres"));
        } else if (databaseUrl.startsWith("jdbc:postgresql:")) {
            config.setJdbcUrl(databaseUrl);
            setExplicitCredentials(config);
        } else if (databaseUrl.startsWith("postgresql://") || databaseUrl.startsWith("postgres://")) {
            configurePostgresUrl(config, databaseUrl);
        } else {
            throw new IllegalArgumentException("DATABASE_URL must be a PostgreSQL connection URL");
        }

        config.setMaximumPoolSize(intFromEnvironment("DB_POOL_SIZE", 5));
        config.setMinimumIdle(0);
        config.setConnectionTimeout(30_000);
        config.setValidationTimeout(5_000);
        config.setIdleTimeout(60_000);
        config.setMaxLifetime(1_800_000);
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("student-os-postgres");

        return new HikariDataSource(config);
    }

    private static void configurePostgresUrl(HikariConfig config, String databaseUrl) throws Exception {
        URI uri = new URI(databaseUrl);
        String host = uri.getHost();
        String path = uri.getRawPath();
        if (host == null || path == null || path.length() <= 1) {
            throw new IllegalArgumentException("DATABASE_URL must include a host and database name");
        }

        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String query = normaliseJdbcQuery(uri.getRawQuery());
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
        if (!query.isBlank()) {
            jdbcUrl += "?" + query;
        }
        config.setJdbcUrl(jdbcUrl);

        String rawUserInfo = uri.getRawUserInfo();
        if (rawUserInfo != null && !rawUserInfo.isBlank()) {
            int separator = rawUserInfo.indexOf(':');
            String rawUser = separator >= 0 ? rawUserInfo.substring(0, separator) : rawUserInfo;
            String rawPassword = separator >= 0 ? rawUserInfo.substring(separator + 1) : "";
            config.setUsername(URLDecoder.decode(rawUser, StandardCharsets.UTF_8));
            config.setPassword(URLDecoder.decode(rawPassword, StandardCharsets.UTF_8));
        } else {
            setExplicitCredentials(config);
        }
    }

    private static String normaliseJdbcQuery(String rawQuery) {
        String query = rawQuery == null ? "" : rawQuery.replace("channel_binding=", "channelBinding=");
        if (!query.toLowerCase().contains("sslmode=")) {
            query = query.isBlank() ? "sslmode=require" : query + "&sslmode=require";
        }
        return query;
    }

    private static void setExplicitCredentials(HikariConfig config) {
        String username = firstNonBlank(System.getenv("DB_USERNAME"), System.getenv("PGUSER"));
        String password = firstNonBlank(System.getenv("DB_PASSWORD"), System.getenv("PGPASSWORD"));
        if (username != null) {
            config.setUsername(username);
        }
        if (password != null) {
            config.setPassword(password);
        }
    }

    private static int intFromEnvironment(String name, int defaultValue) {
        try {
            return Integer.parseInt(System.getenv(name));
        } catch (Exception ignored) {
            return defaultValue;
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}
