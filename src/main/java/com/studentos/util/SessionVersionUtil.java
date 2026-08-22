package com.studentos.util;

/** Keeps session invalidation decisions explicit and independently testable. */
public final class SessionVersionUtil {
    private SessionVersionUtil() { }
    public static boolean isCurrent(Integer sessionVersion, int accountVersion) {
        return sessionVersion != null && sessionVersion == accountVersion;
    }
}
