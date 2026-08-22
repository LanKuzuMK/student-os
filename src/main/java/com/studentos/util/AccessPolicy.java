package com.studentos.util;

/** Central role decisions used by sign-in and administrator route guards. */
public final class AccessPolicy {
    private AccessPolicy() { }

    public static boolean isAdminRole(String role) { return "ADMIN".equals(role); }
    public static boolean isStaffRole(String role) { return isAdminRole(role) || "MODERATOR".equals(role); }

    public static String postLoginPath(String role) {
        return isAdminRole(role) ? "/admin" : "MODERATOR".equals(role) ? "/admin/reports" : "/dashboard";
    }
}
