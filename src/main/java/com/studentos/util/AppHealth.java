package com.studentos.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** Performs a minimal read-only dependency check for staff diagnostics. */
public final class AppHealth {
    private AppHealth() { }

    public static boolean databaseAvailable() {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet result = statement.executeQuery()) {
            return result.next();
        } catch (Exception ignored) {
            return false;
        }
    }
}
