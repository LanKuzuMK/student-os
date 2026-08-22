package com.studentos.model;
import java.sql.Timestamp;
public class User {
    private int id;
    private String email;
    private String passwordHash;
    private String role;
    private String status;
    private int authVersion;
    private Timestamp createdAt;
    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; } public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRole() { return role; } public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; } public void setStatus(String status) { this.status = status; }
    public int getAuthVersion() { return authVersion; } public void setAuthVersion(int authVersion) { this.authVersion = authVersion; }
    public Timestamp getCreatedAt() { return createdAt; } public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
