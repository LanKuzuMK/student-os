package com.studentos.util;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Small in-memory throttle for sensitive anonymous endpoints. It intentionally
 * returns generic denials so authentication responses do not reveal account state.
 */
public final class AuthAttemptLimiter {
    private static final int DEFAULT_MAX_FAILURES = 8;
    private static final Duration DEFAULT_WINDOW = Duration.ofMinutes(15);

    private final int maxFailures;
    private final Duration window;
    private final ConcurrentMap<String, AttemptWindow> attempts = new ConcurrentHashMap<>();

    public AuthAttemptLimiter() {
        this(DEFAULT_MAX_FAILURES, DEFAULT_WINDOW);
    }

    AuthAttemptLimiter(int maxFailures, Duration window) {
        if (maxFailures < 1 || window == null || window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("A positive limit and window are required");
        }
        this.maxFailures = maxFailures;
        this.window = window;
    }

    public boolean isAllowed(String scope, String remoteAddress, String identifier) {
        Instant now = Instant.now();
        return isAllowed(key(scope, "ip", remoteAddress), now)
                && isAllowed(key(scope, "identity", identifier), now);
    }

    public void recordFailure(String scope, String remoteAddress, String identifier) {
        Instant now = Instant.now();
        recordFailure(key(scope, "ip", remoteAddress), now);
        recordFailure(key(scope, "identity", identifier), now);
    }

    public void recordSuccess(String scope, String remoteAddress, String identifier) {
        attempts.remove(key(scope, "ip", remoteAddress));
        attempts.remove(key(scope, "identity", identifier));
    }

    private boolean isAllowed(String key, Instant now) {
        AttemptWindow counter = attempts.get(key);
        if (counter == null) {
            return true;
        }
        synchronized (counter) {
            if (expired(counter, now)) {
                attempts.remove(key, counter);
                return true;
            }
            return counter.failures < maxFailures;
        }
    }

    private void recordFailure(String key, Instant now) {
        attempts.compute(key, (ignored, existing) -> {
            if (existing == null || expired(existing, now)) {
                return new AttemptWindow(now, 1);
            }
            synchronized (existing) {
                existing.failures++;
            }
            return existing;
        });
    }

    private boolean expired(AttemptWindow counter, Instant now) {
        return !now.isBefore(counter.startedAt.plus(window));
    }

    private String key(String scope, String kind, String value) {
        String normalizedScope = scope == null ? "auth" : scope.trim().toLowerCase();
        String normalizedValue = value == null || value.isBlank() ? "unknown" : value.trim().toLowerCase();
        return normalizedScope + ":" + kind + ":" + normalizedValue;
    }

    private static final class AttemptWindow {
        private final Instant startedAt;
        private int failures;

        private AttemptWindow(Instant startedAt, int failures) {
            this.startedAt = startedAt;
            this.failures = failures;
        }
    }
}
