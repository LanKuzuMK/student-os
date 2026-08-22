package com.studentos.util;

/** Allow-list for saved public content types. */
public final class SavedItemPolicy {
    private SavedItemPolicy() { }
    public static String targetType(String value) {
        return switch (value == null ? "" : value) {
            case "PROFILE", "SKILL", "SERVICE", "JOB" -> value;
            default -> null;
        };
    }
}
