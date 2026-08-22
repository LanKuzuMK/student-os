package com.studentos.model;

import java.sql.Timestamp;

/** A private student team workspace; membership is required for every read and write. */
public class ProjectSpace {
    private int id;
    private int ownerId;
    private String title;
    private String description;
    private String status;
    private Timestamp createdAt;
    private String memberRole;
    private int memberCount;
    private int activeTaskCount;
    private int milestoneProgress;

    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getOwnerId() { return ownerId; } public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; } public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public String getMemberRole() { return memberRole; } public void setMemberRole(String memberRole) { this.memberRole = memberRole; }
    public int getMemberCount() { return memberCount; } public void setMemberCount(int memberCount) { this.memberCount = memberCount; }
    public int getActiveTaskCount() { return activeTaskCount; } public void setActiveTaskCount(int activeTaskCount) { this.activeTaskCount = activeTaskCount; }
    public int getMilestoneProgress() { return milestoneProgress; } public void setMilestoneProgress(int milestoneProgress) { this.milestoneProgress = milestoneProgress; }
    public boolean isOwner() { return "OWNER".equals(memberRole); }
}
