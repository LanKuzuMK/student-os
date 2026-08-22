package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SavedItemPolicyTest {
    @Test void acceptsOnlyPublicBookmarkTargetTypes() {
        assertEquals("PROFILE", SavedItemPolicy.targetType("PROFILE"));
        assertEquals("SKILL", SavedItemPolicy.targetType("SKILL"));
        assertEquals("SERVICE", SavedItemPolicy.targetType("SERVICE"));
        assertEquals("JOB", SavedItemPolicy.targetType("JOB"));
        assertNull(SavedItemPolicy.targetType("MESSAGE"));
        assertNull(SavedItemPolicy.targetType("ADMIN"));
    }
}
