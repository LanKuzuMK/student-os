package com.studentos.util;

/** Shared normalization and validation for browser-submitted student input. */
public final class InputValidator {
    private InputValidator() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.length() <= 255
                && email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    public static String trimToLength(String value, int maximumLength) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized.substring(0, Math.min(normalized.length(), maximumLength));
    }

    public static Double parseNonNegativeBudget(String value) {
        try {
            double budget = Double.parseDouble(value);
            return Double.isFinite(budget) && budget >= 0 && budget <= 1_000_000 ? budget : null;
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 128 || password.chars().anyMatch(Character::isWhitespace)) {
            return false;
        }
        boolean hasLetter = password.chars().anyMatch(Character::isLetter);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        return hasLetter && hasDigit;
    }
}
