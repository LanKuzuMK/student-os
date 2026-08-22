package com.studentos.model;

public class Profile {
    private int userId;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String university;
    private String major;
    private String portfolioUrl;
    private String linkedinUrl;
    private String telegramUrl;
    private String availabilityStatus;
    private String collaborationPreferences;
    private boolean hasAvatar;

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }
    public String getTelegramUrl() { return telegramUrl; }
    public void setTelegramUrl(String telegramUrl) { this.telegramUrl = telegramUrl; }
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
    public boolean isHasAvatar() { return hasAvatar; }
    public void setHasAvatar(boolean hasAvatar) { this.hasAvatar = hasAvatar; }

    public String getDisplayName() {
        String fullName = ((firstName == null ? "" : firstName.trim()) + " "
                + (lastName == null ? "" : lastName.trim())).trim();
        return fullName.isEmpty() ? email : fullName;
    }
}
