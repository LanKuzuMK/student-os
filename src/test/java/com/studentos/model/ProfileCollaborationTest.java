package com.studentos.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfileCollaborationTest {
    @Test void presentsFriendlyAvailabilityLabels() {
        Profile profile = new Profile();
        profile.setAvailabilityStatus("OPEN_TO_COLLABORATE");
        assertEquals("Open to collaborate", profile.getAvailabilityLabel());
        profile.setAvailabilityStatus("AVAILABLE_FOR_FREELANCE");
        assertEquals("Available for freelance work", profile.getAvailabilityLabel());
    }
}
