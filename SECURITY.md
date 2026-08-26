# Security Policy

## Supported Project Scope

StudentOS is a university project with a hosted pilot environment. Security reports are welcome when they concern authentication, role access, session behavior, request validation, data ownership, password recovery, message safety, or exposed sensitive information.

## Reporting a Concern

Please report a potential security issue privately to the project owner or supervising team member. Do **not** post passwords, reset codes, database connection strings, API keys, production account details, or detailed bypass steps in a public issue, commit, pull request, screenshot, or chat message.

Provide only the minimum information needed to reproduce the issue safely:

1. A short description of the observed behavior.
2. The general page or feature area affected.
3. Safe reproduction steps that do not require sharing credentials or sensitive data.
4. The expected secure behavior and the actual behavior.

## Current Security Practices

- BCrypt password verification.
- Server-side authentication, role, ownership, and protected-route checks.
- CSRF validation for relevant state-changing requests.
- Request-forgery protection for sign-in, registration, verification, password-reset, and logout requests.
- Generic in-memory abuse throttling for sign-in, registration, and password-reset initiation.
- Browser security headers, including Content Security Policy, frame protection, content-type protection, referrer policy, permissions policy, and transport-security guidance.
- Parameterized database access through JDBC and DAO classes.
- Persistent session revocation on logout, password changes, account-status changes, and related authentication updates.
- Read-only staff health diagnostics that do not expose secret connection details.
- Safe error handling that avoids showing infrastructure secrets to users.

## Responsible Disclosure

Please allow the project team time to investigate and correct a report before sharing technical details publicly. The team will document resolved, non-sensitive improvements in public project documentation when appropriate.
