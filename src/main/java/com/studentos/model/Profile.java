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
    public boolean isHasAvatar() { return hasAvatar; }
    public void setHasAvatar(boolean hasAvatar) { this.hasAvatar = hasAvatar; }

    public String getDisplayName() {
        String fullName = ((firstName == null ? "" : firstName.trim()) + " "
                + (lastName == null ? "" : lastName.trim())).trim();
        return fullName.isEmpty() ? email : fullName;
    }
}
