package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModerationPolicyTest {
    @Test void acceptsOnlyKnownReportStatesAndTargets() {
        assertTrue(ModerationPolicy.isKnownReportStatus("OPEN"));
        assertTrue(ModerationPolicy.isKnownReportStatus("RESOLVED"));
        assertFalse(ModerationPolicy.isKnownReportStatus("ARCHIVED"));
        assertTrue(ModerationPolicy.isKnownTargetType("MESSAGE"));
        assertFalse(ModerationPolicy.isKnownTargetType("PROFILE"));
    }
}
