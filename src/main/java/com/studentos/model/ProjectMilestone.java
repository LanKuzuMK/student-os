package com.studentos.model;

import java.sql.Date;

public class ProjectMilestone {
    private int id; private int projectId; private String title; private String description; private String status; private Date dueDate;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getProjectId() { return projectId; } public void setProjectId(int projectId) { this.projectId = projectId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public Date getDueDate() { return dueDate; } public void setDueDate(Date dueDate) { this.dueDate = dueDate; }
}
