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
    private String collaborationPreferences;public String getDisplayName() {
        String fullName = ((firstName == null ? "" : firstName.trim()) + " "
                + (lastName == null ? "" : lastName.trim())).trim();
        return fullName.isEmpty() ? email : fullName;
    }
}

