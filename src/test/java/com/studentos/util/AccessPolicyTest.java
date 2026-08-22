package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccessPolicyTest {
    @Test void onlyAdministratorsReceiveFullAdministratorAccess() {
        assertTrue(AccessPolicy.isAdminRole("ADMIN"));
        assertFalse(AccessPolicy.isAdminRole("MODERATOR"));
        assertFalse(AccessPolicy.isAdminRole("STUDENT"));
    }

    @Test void moderatorsAreStaffButStudentsAreNot() {
        assertTrue(AccessPolicy.isStaffRole("ADMIN"));
        assertTrue(AccessPolicy.isStaffRole("MODERATOR"));
        assertFalse(AccessPolicy.isStaffRole("STUDENT"));
    }

    @Test void loginRoutesKeepModeratorSeparateFromAdministrator() {
        assertEquals("/admin", AccessPolicy.postLoginPath("ADMIN"));
        assertEquals("/admin/reports", AccessPolicy.postLoginPath("MODERATOR"));
        assertEquals("/dashboard", AccessPolicy.postLoginPath("STUDENT"));
    }
}
