package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersistentSessionManagerTest {
    @Test void generatesDistinctOpaqueTokens() {
        String first = PersistentSessionManager.generateToken();
        String second = PersistentSessionManager.generateToken();

        assertNotEquals(first, second);
        assertEquals(43, first.length());
        assertTrue(first.matches("[A-Za-z0-9_-]+"));
    }

    @Test void hashesTokenBeforePersistence() {
        String token = "known-browser-token";
        String hash = PersistentSessionManager.hashToken(token);

        assertNotEquals(token, hash);
        assertEquals(64, hash.length());
        assertEquals(hash, PersistentSessionManager.hashToken(token));
        assertNotEquals(hash, PersistentSessionManager.hashToken(token + "-changed"));
    }
}
