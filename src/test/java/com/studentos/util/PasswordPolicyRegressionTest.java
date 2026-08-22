package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyRegressionTest {
    @Test void acceptsStrongPasswordAndRejectsWeakOrWhitespaceValues() {
        assertTrue(InputValidator.isValidPassword("StudentOS2026"));
        assertFalse(InputValidator.isValidPassword("short1"));
        assertFalse(InputValidator.isValidPassword("passwordonly"));
        assertFalse(InputValidator.isValidPassword("12345678"));
        assertFalse(InputValidator.isValidPassword("Student OS2026"));
    }
}
