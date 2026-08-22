package com.studentos.dao;

import com.studentos.model.CollaborationRequest;
import com.studentos.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Owner-scoped persistence for collaboration proposals and recipient responses. */
public class CollaborationRequestDAO {
    public boolean create(CollaborationRequest request) {
        if (request.getRequesterId() < 1 || request.getRecipientId() < 1 || request.getRequesterId() == request.getRecipientId()
                || !isActiveRecipient(request.getRecipientId()) || hasOpenDuplicate(request)) return false;
        String sql = "INSERT INTO collaboration_requests (requester_id, recipient_id, request_type, title, description, expected_commitment) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, request.getRequesterId());
            statement.setInt(2, request.getRecipientId());
            statement.setString(3, request.getRequestType());
            statement.setString(4, request.getTitle());
            statement.setString(5, request.getDescription());
            statement.setString(6, request.getExpectedCommitment());
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) { System.err.println("Unable to create collaboration request: " + exception.getMessage()); return false; }
    }

    public List<CollaborationRequest> getForUser(int userId) {
        String sql = "SELECT r.*, "
                + "CASE WHEN r.recipient_id = ? THEN requester.email ELSE recipient.email END AS counterpart_email, "
                + "CASE WHEN r.recipient_id = ? THEN COALESCE(NULLIF(TRIM(CONCAT_WS(' ', requester_profile.first_name, requester_profile.last_name)), ''), requester.email) "
                + "ELSE COALESCE(NULLIF(TRIM(CONCAT_WS(' ', recipient_profile.first_name, recipient_profile.last_name)), ''), recipient.email) END AS counterpart_name "
                + "FROM collaboration_requests r JOIN users requester ON requester.id = r.requester_id JOIN users recipient ON recipient.id = r.recipient_id "
                + "LEFT JOIN profiles requester_profile ON requester_profile.user_id = requester.id LEFT JOIN profiles recipient_profile ON recipient_profile.user_id = recipient.id "
                + "WHERE r.requester_id = ? OR r.recipient_id = ? "
                + "ORDER BY CASE WHEN r.status = 'PENDING' AND r.recipient_id = ? THEN 0 WHEN r.status = 'PENDING' THEN 1 ELSE 2 END, r.created_at DESC";
        List<CollaborationRequest> requests = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId); statement.setInt(2, userId); statement.setInt(3, userId); statement.setInt(4, userId); statement.setInt(5, userId);
            try (ResultSet result = statement.executeQuery()) { while (result.next()) requests.add(map(result, userId)); }
        } catch (SQLException exception) { System.err.println("Unable to load collaboration requests: " + exception.getMessage()); }
        return requests;
    }

    public Integer getRequesterId(int requestId) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT requester_id FROM collaboration_requests WHERE id = ?")) {
            statement.setInt(1, requestId);
            try (ResultSet result = statement.executeQuery()) { return result.next() ? result.getInt(1) : null; }
        } catch (SQLException exception) { return null; }
    }

    public boolean respond(int requestId, int recipientId, String status, String responseNote) {
        String sql = "UPDATE collaboration_requests SET status = ?, response_note = ?, responded_at = CURRENT_TIMESTAMP "
                + "WHERE id = ? AND recipient_id = ? AND status = 'PENDING'";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status); statement.setString(2, responseNote); statement.setInt(3, requestId); statement.setInt(4, recipientId);
            return statement.executeUpdate() == 1;
        } catch (SQLException exception) { return false; }
    }

    public boolean cancel(int requestId, int requesterId) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE collaboration_requests SET status = 'CANCELLED', responded_at = CURRENT_TIMESTAMP WHERE id = ? AND requester_id = ? AND status = 'PENDING'")) {
            statement.setInt(1, requestId); statement.setInt(2, requesterId); return statement.executeUpdate() == 1;
        } catch (SQLException exception) { return false; }
    }

    private boolean isActiveRecipient(int recipientId) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM users WHERE id = ? AND status = 'ACTIVE'")) {
            statement.setInt(1, recipientId);
            try (ResultSet result = statement.executeQuery()) { return result.next(); }
        } catch (SQLException exception) { return false; }
    }

    private boolean hasOpenDuplicate(CollaborationRequest request) {
        String sql = "SELECT 1 FROM collaboration_requests WHERE requester_id = ? AND recipient_id = ? AND title = ? AND status = 'PENDING'";
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, request.getRequesterId()); statement.setInt(2, request.getRecipientId()); statement.setString(3, request.getTitle());
            try (ResultSet result = statement.executeQuery()) { return result.next(); }
        } catch (SQLException exception) { return true; }
    }

    private CollaborationRequest map(ResultSet result, int userId) throws SQLException {
        CollaborationRequest request = new CollaborationRequest();
        request.setId(result.getInt("id")); request.setRequesterId(result.getInt("requester_id")); request.setRecipientId(result.getInt("recipient_id"));
        request.setRequestType(result.getString("request_type")); request.setTitle(result.getString("title")); request.setDescription(result.getString("description"));
        request.setExpectedCommitment(result.getString("expected_commitment")); request.setStatus(result.getString("status")); request.setResponseNote(result.getString("response_note"));
        request.setCreatedAt(result.getTimestamp("created_at")); request.setRespondedAt(result.getTimestamp("responded_at")); request.setCounterpartEmail(result.getString("counterpart_email"));
        request.setCounterpartName(result.getString("counterpart_name")); request.setIncoming(result.getInt("recipient_id") == userId);
        return request;
    }
}
