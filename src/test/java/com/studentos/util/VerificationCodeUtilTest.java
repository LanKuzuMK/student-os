package com.studentos.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VerificationCodeUtilTest {
    @Test
    void generatedCodeUsesExactlySixDigits() {
        assertTrue(VerificationCodeUtil.isSixDigitCode(VerificationCodeUtil.generateSixDigitCode()));
    }

    @Test
    void rejectsMalformedCodes() {
        assertFalse(VerificationCodeUtil.isSixDigitCode("12345"));
        assertFalse(VerificationCodeUtil.isSixDigitCode("12345a"));
        assertFalse(VerificationCodeUtil.isSixDigitCode(null));
    }
}
