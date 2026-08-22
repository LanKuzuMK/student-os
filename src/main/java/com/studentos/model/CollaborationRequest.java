package com.studentos.model;

import java.sql.Timestamp;

/** A structured proposal exchanged between two active StudentOS students. */
public class CollaborationRequest {
    private int id;
    private int requesterId;
    private int recipientId;
    private String requestType;
    private String title;
    private String description;
    private String expectedCommitment;
    private String status;
    private String responseNote;
    private Timestamp createdAt;
    private Timestamp respondedAt;
    private String counterpartName;
    private String counterpartEmail;
    private boolean incoming;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRequesterId() { return requesterId; }
    public void setRequesterId(int requesterId) { this.requesterId = requesterId; }
    public int getRecipientId() { return recipientId; }
    public void setRecipientId(int recipientId) { this.recipientId = recipientId; }
    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getExpectedCommitment() { return expectedCommitment; }
    public void setExpectedCommitment(String expectedCommitment) { this.expectedCommitment = expectedCommitment; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getResponseNote() { return responseNote; }
    public void setResponseNote(String responseNote) { this.responseNote = responseNote; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getRespondedAt() { return respondedAt; }
    public void setRespondedAt(Timestamp respondedAt) { this.respondedAt = respondedAt; }
    public String getCounterpartName() { return counterpartName; }
    public void setCounterpartName(String counterpartName) { this.counterpartName = counterpartName; }
    public String getCounterpartEmail() { return counterpartEmail; }
    public void setCounterpartEmail(String counterpartEmail) { this.counterpartEmail = counterpartEmail; }
    public boolean isIncoming() { return incoming; }
    public void setIncoming(boolean incoming) { this.incoming = incoming; }
    public String getRequestTypeLabel() {
        return switch (requestType == null ? "" : requestType) {
            case "STUDY_GROUP" -> "Study group";
            case "PROJECT" -> "Project collaboration";
            case "SKILL_EXCHANGE" -> "Skill exchange";
            case "FREELANCE" -> "Freelance opportunity";
            default -> "Other collaboration";
        };
    }
}
