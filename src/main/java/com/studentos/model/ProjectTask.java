package com.studentos.model;

public class ProjectTask {
    private int id; private int projectId; private String title; private String description; private String status; private String priority; private Integer assigneeId; private String assigneeName;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getProjectId() { return projectId; } public void setProjectId(int projectId) { this.projectId = projectId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public String getPriority() { return priority; } public void setPriority(String priority) { this.priority = priority; }
    public Integer getAssigneeId() { return assigneeId; } public void setAssigneeId(Integer assigneeId) { this.assigneeId = assigneeId; }
    public String getAssigneeName() { return assigneeName; } public void setAssigneeName(String assigneeName) { this.assigneeName = assigneeName; }
}
