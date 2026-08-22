package com.studentos.util;

/** Explicit allow-lists for collaboration proposal and response values. */
public final class CollaborationPolicy {
    private CollaborationPolicy() { }
    public static String requestType(String value) {
        return switch (value == null ? "" : value) {
            case "STUDY_GROUP", "PROJECT", "SKILL_EXCHANGE", "FREELANCE", "OTHER" -> value;
            default -> null;
        };
    }
    public static String responseStatus(String value) {
        return "ACCEPTED".equals(value) || "DECLINED".equals(value) ? value : null;
    }
}
