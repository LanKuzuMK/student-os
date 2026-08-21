package com.studentos.util;

import java.security.SecureRandom;

/** Generates short-lived numeric verification codes without predictable randomness. */
public final class VerificationCodeUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private VerificationCodeUtil() {
    }

    public static String generateSixDigitCode() {
        return String.format("%06d", RANDOM.nextInt(1_000_000));
    }

    public static boolean isSixDigitCode(String value) {
        return value != null && value.matches("\\d{6}");
    }
}
