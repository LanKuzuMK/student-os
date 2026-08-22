package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscoveryPolicyTest {
    @Test void acceptsOnlySupportedPublicDiscoveryFilters() {
        assertEquals("TEACH", DiscoveryPolicy.skillType("TEACH"));
        assertNull(DiscoveryPolicy.skillType("ADMIN"));
        assertEquals("ADVANCED", DiscoveryPolicy.level("ADVANCED"));
        assertNull(DiscoveryPolicy.level("MASTER"));
        assertEquals("LOOKING_FOR_TEAM", DiscoveryPolicy.availability("LOOKING_FOR_TEAM"));
        assertNull(DiscoveryPolicy.availability("PRIVATE"));
    }

    @Test void limitsSortingToKnownStableOrders() {
        assertEquals("NEWEST", DiscoveryPolicy.sort(null));
        assertEquals("NAME", DiscoveryPolicy.sort("NAME"));
        assertEquals("LEVEL", DiscoveryPolicy.sort("LEVEL"));
        assertEquals("NEWEST", DiscoveryPolicy.sort("DROP TABLE"));
    }
}
