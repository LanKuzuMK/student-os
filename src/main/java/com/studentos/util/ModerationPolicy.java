package com.studentos.util;

/** Allow-lists public moderation filter values before they reach persistence. */
public final class ModerationPolicy {
    private ModerationPolicy() { }
    public static boolean isKnownReportStatus(String value) {
        return "OPEN".equals(value) || "RESOLVED".equals(value) || "DISMISSED".equals(value);
    }
    public static boolean isKnownTargetType(String value) {
        return "SKILL".equals(value) || "JOB".equals(value) || "SERVICE".equals(value) || "MESSAGE".equals(value);
    }
}
