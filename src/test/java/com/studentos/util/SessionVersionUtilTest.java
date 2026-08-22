package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionVersionUtilTest {
    @Test void acceptsOnlyCurrentAccountVersion() {
        assertTrue(SessionVersionUtil.isCurrent(4, 4));
        assertFalse(SessionVersionUtil.isCurrent(3, 4));
        assertFalse(SessionVersionUtil.isCurrent(null, 4));
    }
}
