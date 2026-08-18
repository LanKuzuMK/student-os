package com.studentos.model;
public class Goal {
    private int id;
    private int userId;
    private String title;
    private String description;
    private int progress;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; } public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; } public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; } public void setDescription(String description) { this.description = description; }
    public int getProgress() { return progress; } public void setProgress(int progress) { this.progress = progress; }
}
