package com.studentos.dao;

import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/** Persistence for student reports, reversible content visibility, and admin audit records. */
public class ModerationDAO {
    public boolean createReport(int reporterId, String targetType, int targetId, String reason, String details) {
        if (!isReportableTarget(targetType, targetId, reporterId) || hasOpenDuplicate(reporterId, targetType, targetId)) {
            return false;
        }
        String sql = "INSERT INTO moderation_reports (reporter_id, target_type, target_id, reason, details) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reporterId);
            statement.setString(2, targetType);
            statement.setInt(3, targetId);
            statement.setString(4, reason);
            statement.setString(5, details);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            System.err.println("Unable to create moderation report: " + exception.getMessage());
            return false;
        }
    }

    public List<Map<String, Object>> getReports() {
        String sql = "SELECT r.id, r.target_type, r.target_id, r.reason, r.details, r.status, r.created_at, r.reviewed_at, r.resolution_note, "
                + "reporter.email AS reporter_email, reviewer.email AS reviewer_email, assignee.email AS assigned_email, "
                + "COALESCE(sk.skill_name, j.title, svc.title, LEFT(m.content, 120), '[removed content]') AS target_summary, "
                + "COALESCE(sku.email, ju.email, svcu.email, sender.email, '') AS target_owner_email "
                + "FROM moderation_reports r "
                + "JOIN users reporter ON reporter.id = r.reporter_id "
                + "LEFT JOIN users reviewer ON reviewer.id = r.reviewed_by "
                + "LEFT JOIN users assignee ON assignee.id = r.assigned_to "
                + "LEFT JOIN user_skills sk ON r.target_type = 'SKILL' AND sk.id = r.target_id "
                + "LEFT JOIN users sku ON sku.id = sk.user_id "
                + "LEFT JOIN jobs j ON r.target_type = 'JOB' AND j.id = r.target_id "
                + "LEFT JOIN users ju ON ju.id = j.user_id "
                + "LEFT JOIN services svc ON r.target_type = 'SERVICE' AND svc.id = r.target_id "
                + "LEFT JOIN users svcu ON svcu.id = svc.user_id "
                + "LEFT JOIN messages m ON r.target_type = 'MESSAGE' AND m.id = r.target_id "
                + "LEFT JOIN users sender ON sender.id = m.sender_id "
                + "ORDER BY CASE WHEN r.status = 'OPEN' THEN 0 ELSE 1 END, r.created_at DESC";
        return fetchRows(sql, "id", "target_type", "target_id", "reason", "details", "status", "created_at", "reviewed_at",
                "resolution_note", "reporter_email", "reviewer_email", "assigned_email", "target_summary", "target_owner_email");
    }

    public List<Map<String, Object>> getReports(String status, String targetType, String search, int limit, int offset) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase();
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (Map<String, Object> report : getReports()) {
            boolean statusMatches = status == null || status.isBlank() || status.equals(report.get("status"));
            boolean typeMatches = targetType == null || targetType.isBlank() || targetType.equals(report.get("target_type"));
            String searchable = String.valueOf(report.get("reporter_email")) + " " + report.get("reason") + " "
                    + String.valueOf(report.get("details")) + " " + String.valueOf(report.get("target_summary"));
            if (statusMatches && typeMatches && searchable.toLowerCase().contains(normalizedSearch)) filtered.add(report);
        }
        int start = Math.min(Math.max(offset, 0), filtered.size());
        int end = Math.min(start + Math.max(1, Math.min(limit, 100)), filtered.size());
        return new ArrayList<>(filtered.subList(start, end));
    }

    public int countReports(String status, String targetType, String search) {
        String normalizedSearch = search == null ? "" : search.trim().toLowerCase();
        int count = 0;
        for (Map<String, Object> report : getReports()) {
            boolean statusMatches = status == null || status.isBlank() || status.equals(report.get("status"));
            boolean typeMatches = targetType == null || targetType.isBlank() || targetType.equals(report.get("target_type"));
            String searchable = String.valueOf(report.get("reporter_email")) + " " + report.get("reason") + " "
                    + String.valueOf(report.get("details")) + " " + String.valueOf(report.get("target_summary"));
            if (statusMatches && typeMatches && searchable.toLowerCase().contains(normalizedSearch)) count++;
        }
        return count;
    }

    public boolean assignReport(int reportId, int staffId) {
        String sql = "UPDATE moderation_reports SET assigned_to = ? WHERE id = ? AND status = 'OPEN'";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, staffId);
            statement.setInt(2, reportId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) { return false; }
    }

    public Integer getReporterId(int reportId) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT reporter_id FROM moderation_reports WHERE id = ?")) {
            statement.setInt(1, reportId);
            try (ResultSet result = statement.executeQuery()) { return result.next() ? result.getInt(1) : null; }
        } catch (SQLException exception) { return null; }
    }

    public boolean setContentVisibility(String targetType, int targetId, boolean hidden) {
        return setContentVisibility(targetType, targetId, hidden, hidden ? "Hidden by StudentOS moderation" : null);
    }

    public boolean setContentVisibility(String targetType, int targetId, boolean hidden, String note) {
        String table = contentTable(targetType);
        if (table == null || targetId < 1) {
            return false;
        }
        String sql = "UPDATE " + table + " SET moderation_status = ?, moderation_note = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, hidden ? "HIDDEN" : "VISIBLE");
            statement.setString(2, note);
            statement.setInt(3, targetId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            System.err.println("Unable to update content visibility: " + exception.getMessage());
            return false;
        }
    }

    public boolean resolveReport(int reportId, int adminId, String status, String note) {
        if ((!"RESOLVED".equals(status) && !"DISMISSED".equals(status)) || reportId < 1) {
            return false;
        }
        String sql = "UPDATE moderation_reports SET status = ?, reviewed_by = ?, reviewed_at = CURRENT_TIMESTAMP, resolution_note = ? "
                + "WHERE id = ? AND status = 'OPEN'";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status);
            statement.setInt(2, adminId);
            statement.setString(3, note);
            statement.setInt(4, reportId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) {
            System.err.println("Unable to resolve moderation report: " + exception.getMessage());
            return false;
        }
    }

    public void recordAudit(int adminId, String action, String targetType, int targetId, String reason) {
        String sql = "INSERT INTO moderation_audit_log (admin_id, action, target_type, target_id, reason) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, adminId);
            statement.setString(2, action);
            statement.setString(3, targetType);
            statement.setInt(4, targetId);
            statement.setString(5, reason);
            statement.executeUpdate();
        } catch (SQLException exception) {
            System.err.println("Unable to record moderation audit event: " + exception.getMessage());
        }
    }

    public List<Map<String, Object>> getAuditEntries(int limit) {
        String sql = "SELECT a.id, a.action, a.target_type, a.target_id, a.reason, a.created_at, u.email AS admin_email "
                + "FROM moderation_audit_log a LEFT JOIN users u ON u.id = a.admin_id ORDER BY a.created_at DESC LIMIT " + Math.max(1, Math.min(limit, 500));
        return fetchRows(sql, "id", "action", "target_type", "target_id", "reason", "created_at", "admin_email");
    }

    public List<Map<String, Object>> getAuditEntries(String query, String action, int limit, int offset) {
        StringBuilder sql = new StringBuilder("SELECT a.id, a.action, a.target_type, a.target_id, a.reason, a.created_at, u.email AS admin_email FROM moderation_audit_log a LEFT JOIN users u ON u.id = a.admin_id WHERE 1 = 1");
        List<Object> parameters = new ArrayList<>();
        appendAuditFilters(sql, parameters, query, action);
        sql.append(" ORDER BY a.created_at DESC LIMIT ? OFFSET ?");
        parameters.add(Math.max(1, Math.min(limit, 100))); parameters.add(Math.max(0, offset));
        return fetchRows(sql.toString(), parameters, "id", "action", "target_type", "target_id", "reason", "created_at", "admin_email");
    }

    public int countAuditEntries(String query, String action) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM moderation_audit_log a LEFT JOIN users u ON u.id = a.admin_id WHERE 1 = 1");
        List<Object> parameters = new ArrayList<>();
        appendAuditFilters(sql, parameters, query, action);
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            bind(statement, parameters);
            try (ResultSet result = statement.executeQuery()) { return result.next() ? result.getInt(1) : 0; }
        } catch (SQLException exception) { return 0; }
    }

    private boolean hasOpenDuplicate(int reporterId, String targetType, int targetId) {
        String sql = "SELECT 1 FROM moderation_reports WHERE reporter_id = ? AND target_type = ? AND target_id = ? AND status = 'OPEN'";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, reporterId);
            statement.setString(2, targetType);
            statement.setInt(3, targetId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException exception) {
            return true;
        }
    }

    private boolean isReportableTarget(String targetType, int targetId, int reporterId) {
        String sql;
        switch (targetType) {
            case "SKILL" -> sql = "SELECT user_id AS owner_id FROM user_skills WHERE id = ?";
            case "JOB" -> sql = "SELECT user_id AS owner_id FROM jobs WHERE id = ?";
            case "SERVICE" -> sql = "SELECT user_id AS owner_id FROM services WHERE id = ?";
            case "MESSAGE" -> sql = "SELECT sender_id AS owner_id, receiver_id FROM messages WHERE id = ?";
            default -> { return false; }
        }
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, targetId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return false;
                }
                int ownerId = result.getInt("owner_id");
                if ("MESSAGE".equals(targetType)) {
                    return ownerId != reporterId && result.getInt("receiver_id") == reporterId;
                }
                return ownerId != reporterId;
            }
        } catch (SQLException exception) {
            return false;
        }
    }

    private String contentTable(String targetType) {
        return switch (targetType) {
            case "SKILL" -> "user_skills";
            case "JOB" -> "jobs";
            case "SERVICE" -> "services";
            default -> null;
        };
    }

    private List<Map<String, Object>> fetchRows(String sql, String... columns) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql); ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (String column : columns) {
                    row.put(column, result.getObject(column));
                }
                rows.add(row);
            }
        } catch (SQLException exception) {
            System.err.println("Unable to load moderation data: " + exception.getMessage());
        }
        return rows;
    }

    private List<Map<String, Object>> fetchRows(String sql, List<Object> parameters, String... columns) {
        List<Map<String, Object>> rows = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) { Map<String, Object> row = new LinkedHashMap<>(); for (String column : columns) row.put(column, result.getObject(column)); rows.add(row); }
            }
        } catch (SQLException exception) { System.err.println("Unable to load moderation data: " + exception.getMessage()); }
        return rows;
    }

    private void appendAuditFilters(StringBuilder sql, List<Object> parameters, String query, String action) {
        if (query != null && !query.isBlank()) { sql.append(" AND LOWER(COALESCE(u.email, '') || ' ' || a.action || ' ' || a.target_type || ' ' || COALESCE(a.reason, '')) LIKE ?"); parameters.add("%" + query.trim().toLowerCase() + "%"); }
        if (action != null && !action.isBlank()) { sql.append(" AND a.action = ?"); parameters.add(action); }
    }

    private void bind(PreparedStatement statement, List<Object> parameters) throws SQLException { for (int i = 0; i < parameters.size(); i++) statement.setObject(i + 1, parameters.get(i)); }
}
