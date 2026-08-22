package com.studentos.model;

/** One focused, non-destructive next step selected from the student's own current activity. */
public class DashboardAction {
    private final String eyebrow;
    private final String title;
    private final String description;
    private final String label;
    private final String url;

    public DashboardAction(String eyebrow, String title, String description, String label, String url) {
        this.eyebrow = eyebrow; this.title = title; this.description = description; this.label = label; this.url = url;
    }
    public String getEyebrow() { return eyebrow; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLabel() { return label; }
    public String getUrl() { return url; }
}
