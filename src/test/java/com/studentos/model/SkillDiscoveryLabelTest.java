package com.studentos.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SkillDiscoveryLabelTest {
    @Test void presentsAvailabilityInStudentFriendlyLanguage() {
        Skill skill = new Skill();
        skill.setAvailabilityStatus("LOOKING_FOR_TEAM");
        assertEquals("Looking for a team", skill.getAvailabilityLabel());
        skill.setAvailabilityStatus("FOCUSED_ON_STUDY");
        assertEquals("Focused on study", skill.getAvailabilityLabel());
    }
}
