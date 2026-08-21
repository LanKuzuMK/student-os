package com.studentos.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/** Run explicitly with the Brevo secrets injected; it sends only to the owner mailbox. */
@Tag("integration")
class EmailServiceIntegrationTest {
    @Test
    void sendsVerificationCodeToConfiguredOwnerMailbox() {
        EmailService service = new EmailService();
        String ownerEmail = System.getenv("EMAIL_FROM");
        assumeTrue(service.isConfigured() && ownerEmail != null && !ownerEmail.isBlank());
        assertTrue(service.sendVerificationCode(ownerEmail, "123456"));
    }
}
