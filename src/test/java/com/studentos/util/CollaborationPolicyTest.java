package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollaborationPolicyTest {
    @Test void acceptsOnlySupportedProposalTypes() {
        assertEquals("PROJECT", CollaborationPolicy.requestType("PROJECT"));
        assertEquals("STUDY_GROUP", CollaborationPolicy.requestType("STUDY_GROUP"));
        assertNull(CollaborationPolicy.requestType("ADMIN_TASK"));
    }

    @Test void acceptsOnlyRecipientDecisionStates() {
        assertEquals("ACCEPTED", CollaborationPolicy.responseStatus("ACCEPTED"));
        assertEquals("DECLINED", CollaborationPolicy.responseStatus("DECLINED"));
        assertNull(CollaborationPolicy.responseStatus("PENDING"));
        assertNull(CollaborationPolicy.responseStatus("CANCELLED"));
    }
}
