package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InputValidatorTest {
    @Test
    void acceptsOrdinaryEmailAndRejectsInvalidAddresses() {
        assertTrue(InputValidator.isValidEmail("student@example.edu"));
        assertFalse(InputValidator.isValidEmail("invalid-address"));
        assertFalse(InputValidator.isValidEmail(null));
    }

    @Test
    void acceptsFiniteNonNegativeBudgetWithinTheSupportedRange() {
        assertEquals(120.5, InputValidator.parseNonNegativeBudget("120.50"));
        assertNull(InputValidator.parseNonNegativeBudget("-1"));
        assertNull(InputValidator.parseNonNegativeBudget("not-a-number"));
        assertNull(InputValidator.parseNonNegativeBudget("Infinity"));
    }

    @Test
    void trimsAndLimitsSubmittedText() {
        assertEquals("hello", InputValidator.trimToLength("  hello  ", 10));
        assertEquals("abc", InputValidator.trimToLength("abcdef", 3));
        assertNull(InputValidator.trimToLength("   ", 3));
    }

    @Test
    void acceptsAReasonablePasswordAndRejectsWeakOrMalformedValues() {
        assertTrue(InputValidator.isValidPassword("StudentOS9"));
        assertFalse(InputValidator.isValidPassword("short7"));
        assertFalse(InputValidator.isValidPassword("abcdefgh"));
        assertFalse(InputValidator.isValidPassword("12345678"));
        assertFalse(InputValidator.isValidPassword("Pass word9"));
    }
}
