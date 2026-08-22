package com.studentos.util;

/** Keeps public discovery filters small, explicit, and independent of request input. */
public final class DiscoveryPolicy {
    private DiscoveryPolicy() { }

    public static String skillType(String value) {
        return "TEACH".equals(value) || "LEARN".equals(value) ? value : null;
    }

    public static String level(String value) {
        return switch (value == null ? "" : value) {
            case "BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT" -> value;
            default -> null;
        };
    }

    public static String availability(String value) {
        return switch (value == null ? "" : value) {
            case "OPEN_TO_COLLABORATE", "LOOKING_FOR_TEAM", "AVAILABLE_FOR_FREELANCE", "FOCUSED_ON_STUDY" -> value;
            default -> null;
        };
    }

    public static String sort(String value) {
        return "NAME".equals(value) || "LEVEL".equals(value) ? value : "NEWEST";
    }
}
