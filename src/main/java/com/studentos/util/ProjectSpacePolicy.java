package com.studentos.util;

/** Explicit allow-lists for private Project Space roles and workflow states. */
public final class ProjectSpacePolicy {
    private ProjectSpacePolicy() { }
    public static String projectStatus(String value) { return switch (value == null ? "" : value) { case "ACTIVE", "ON_HOLD", "COMPLETED" -> value; default -> null; }; }
    public static String memberRole(String value) { return "CONTRIBUTOR".equals(value) ? value : null; }
    public static String milestoneStatus(String value) { return switch (value == null ? "" : value) { case "TODO", "IN_PROGRESS", "COMPLETED" -> value; default -> null; }; }
    public static String taskStatus(String value) { return switch (value == null ? "" : value) { case "TODO", "IN_PROGRESS", "BLOCKED", "COMPLETED" -> value; default -> null; }; }
    public static String priority(String value) { return switch (value == null ? "" : value) { case "LOW", "MEDIUM", "HIGH" -> value; default -> null; }; }
}
