package com.studentos.util;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthAttemptLimiterTest {
    @Test
    void blocksRepeatedFailuresAndAllowsAConfirmedSuccessToClearTheCounter() {
        AuthAttemptLimiter limiter = new AuthAttemptLimiter(3, Duration.ofMinutes(10));

        assertTrue(limiter.isAllowed("login", "127.0.0.1", "student@example.edu"));
        limiter.recordFailure("login", "127.0.0.1", "student@example.edu");
        limiter.recordFailure("login", "127.0.0.1", "student@example.edu");
        limiter.recordFailure("login", "127.0.0.1", "student@example.edu");

        assertFalse(limiter.isAllowed("login", "127.0.0.1", "student@example.edu"));

        limiter.recordSuccess("login", "127.0.0.1", "student@example.edu");
        assertTrue(limiter.isAllowed("login", "127.0.0.1", "student@example.edu"));
    }

    @Test
    void keepsSeparateSensitiveEndpointScopesIndependent() {
        AuthAttemptLimiter limiter = new AuthAttemptLimiter(1, Duration.ofMinutes(10));
        limiter.recordFailure("login", "127.0.0.1", "student@example.edu");

        assertFalse(limiter.isAllowed("login", "127.0.0.1", "student@example.edu"));
        assertTrue(limiter.isAllowed("password-reset", "127.0.0.1", "student@example.edu"));
    }
}
