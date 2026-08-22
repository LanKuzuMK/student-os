package com.studentos.model;

/** A compact public project card owned and managed by one StudentOS profile. */
public class ProfileProject {
    private int id;
    private int userId;
    private String title;
    private String description;
    private String url;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
