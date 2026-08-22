package com.studentos.model;

public class Skill {
    private int id;
    private int userId;
    private String skillName;
    private String skillLevel;
    private String type;
    private String ownerName;
    private String ownerEmail;
    private String university;
    private String major;
    private String availabilityStatus;
    private String collaborationPreferences;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getSkillLevel() { return skillLevel; }
    public void setSkillLevel(String skillLevel) { this.skillLevel = skillLevel; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public String getCollaborationPreferences() { return collaborationPreferences; }
    public void setCollaborationPreferences(String collaborationPreferences) { this.collaborationPreferences = collaborationPreferences; }
    public String getAvailabilityLabel() {
        return switch (availabilityStatus == null ? "" : availabilityStatus) {
            case "OPEN_TO_COLLABORATE" -> "Open to collaborate";
            case "LOOKING_FOR_TEAM" -> "Looking for a team";
            case "AVAILABLE_FOR_FREELANCE" -> "Available for freelance work";
            case "FOCUSED_ON_STUDY" -> "Focused on study";
            default -> "";
        };
    }
}
