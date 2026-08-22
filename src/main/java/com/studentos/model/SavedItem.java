package com.studentos.model;

import java.sql.Timestamp;

/** A private bookmark owned by one student and resolved against currently public content. */
public class SavedItem {
    private int id;
    private String targetType;
    private int targetId;
    private String title;
    private String detail;
    private String targetUrl;
    private Timestamp createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public int getTargetId() { return targetId; }
    public void setTargetId(int targetId) { this.targetId = targetId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getTypeLabel() {
        return switch (targetType == null ? "" : targetType) {
            case "PROFILE" -> "Student profile";
            case "SKILL" -> "Skill";
            case "SERVICE" -> "Service offer";
            case "JOB" -> "Opportunity";
            default -> "Saved item";
        };
    }
}
