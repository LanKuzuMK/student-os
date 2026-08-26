# Student OS Tier 4 Readiness Upgrade

## Purpose

This document records a focused Student OS readiness upgrade that improves security and operational discipline without changing the visible student or administrator experience. It does not claim that the project is invulnerable or fully production-certified.

## Controls added

| Area | Control | Intended protection |
| --- | --- | --- |
| Authentication abuse | Server-side attempt limiter for sign-in, registration, and password-reset initiation. | Reduces repeated credential and code-delivery abuse while preserving generic responses. |
| Request integrity | CSRF tokens are required for sign-in, registration, verification, password-reset, and logout POST requests. | Prevents cross-site request forgery on sensitive account actions. |
| Registration privacy | The temporary registration session keeps a BCrypt password hash rather than the raw password. | Reduces exposure if server-side session data is inspected or logged incorrectly. |
| Account status | Login accepts only active accounts. | Prevents a disabled or unverified account from receiving a new authenticated session. |
| Browser hardening | Global Content Security Policy, frame protection, content-type protection, referrer policy, permissions policy, and HSTS headers. | Reduces browser-side attack surface and clickjacking risk. |
| Regression protection | Automated test coverage for authentication-attempt throttling. | Helps prevent future changes from silently removing the throttle behavior. |

## Operator checklist before a real-user release

| Check | Owner action |
| --- | --- |
| Environment secrets | Confirm that `DATABASE_URL`, email-provider keys, and session-related values are present only in hosting secrets—not source files, screenshots, or support messages. |
| HTTPS | Confirm that the public Student OS address loads only through HTTPS before relying on secure cookies and HSTS. |
| Release build | Run `mvn test` and `mvn package`; review the required GitHub checks before merging or releasing. |
| Account-flow check | Test sign-in, logout, registration verification, password reset, password change, protected-route rejection, and administrator role boundaries using safe temporary accounts. |
| Recovery | Verify that a recent encrypted database backup exists and that a restore rehearsal can be completed in a separate test database. |
| Monitoring | Review application error logs and failed-authentication patterns after release. Keep logs free of passwords, reset codes, connection strings, and raw session tokens. |
| Incident response | If a security issue is suspected, rotate affected secrets, revoke sessions or disable affected accounts, preserve safe evidence, and report privately through the process in `SECURITY.md`. |

## Important boundaries

The request limiter is intentionally lightweight and in-memory. It resets after an application restart and is not shared between multiple application instances. A future multi-instance deployment should move this control to a shared store or gateway-level rate limiter.

The browser policy starts with compatibility-safe allowances for the existing server-rendered user interface. The project should progressively remove inline event handlers and tighten the policy before a larger public rollout.

Hosted protection against denial-of-service attacks, infrastructure compromise, database provider outages, and independent penetration testing remain operator and platform responsibilities. These controls improve readiness; they do not guarantee that Student OS cannot be bypassed.

## Safe project claim

> Student OS uses layered application security controls and is being strengthened through automated checks, controlled releases, secure session handling, request protection, and operational review. It remains a university project that requires continued testing before any broader production use.

