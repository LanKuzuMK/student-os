package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProjectSpacePolicyTest {
    @Test void acceptsOnlyContributorRoleForOwnerManagedAdds() {
        assertEquals("CONTRIBUTOR", ProjectSpacePolicy.memberRole("CONTRIBUTOR"));
        assertNull(ProjectSpacePolicy.memberRole("OWNER"));
    }

    @Test void acceptsOnlyKnownSharedTaskStatesAndPriorities() {
        assertEquals("BLOCKED", ProjectSpacePolicy.taskStatus("BLOCKED"));
        assertEquals("COMPLETED", ProjectSpacePolicy.taskStatus("COMPLETED"));
        assertNull(ProjectSpacePolicy.taskStatus("DELETED"));
        assertEquals("HIGH", ProjectSpacePolicy.priority("HIGH"));
        assertNull(ProjectSpacePolicy.priority("URGENT"));
    }

    @Test void acceptsOnlyKnownMilestoneStates() {
        assertEquals("IN_PROGRESS", ProjectSpacePolicy.milestoneStatus("IN_PROGRESS"));
        assertNull(ProjectSpacePolicy.milestoneStatus("CLOSED"));
    }
}
