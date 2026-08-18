package com.studentos.model;
import java.sql.Timestamp;
public class Job {
    private int id;
    private int userId;
    private String title;
    private String description;
    private double budget;
    private String status;
    private Timestamp createdAt;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; } public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public double getBudget() { return budget; } public void setBudget(double budget) { this.budget = budget; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public Timestamp getCreatedAt() { return createdAt; } public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
