# Student OS Enterprise-Readiness Evidence

## Scope

Student OS is a university project with a hosted pilot environment. This record maps the controls implemented in the repository to the evidence an external assessor would expect. It is not an ISO 27001, SOC 2, or penetration-test certificate.

## Implemented technical evidence

| Control objective | Student OS evidence | Verification approach |
| --- | --- | --- |
| Protect account actions | BCrypt password hashing, secure persistent sessions, account-status checks, CSRF checks, and generic authentication responses. | Run the authentication and authorization test suite; verify protected pages reject unauthenticated requests. |
| Limit account abuse | Server-side throttling of sign-in, registration, and reset initiation. | Run `AuthAttemptLimiterTest`; confirm repeated failure responses stay generic. |
| Reduce browser attack surface | Content Security Policy, anti-framing header, MIME sniffing protection, referrer policy, permissions policy, HSTS, and no-store headers for sensitive responses. | Inspect public HTTPS response headers with a browser developer tool or `curl -I`. |
| Protect data access | JDBC prepared statements plus server-side role and ownership checks. | Review DAO/query implementations and test cross-user access denial. |
| Detect build and dependency risk | CodeQL analysis, Dependabot configuration, container-build verification, and release provenance/SBOM for container publication. | Review successful GitHub Actions runs and dependency alerts before each release. |
| Detect dependency outages | Minimal `/health` readiness endpoint performs a read-only database check; Render is configured to use it. | Request `/health` while the database is available and during a controlled non-production outage test. |

## Evidence to retain for a future external audit

| Evidence | Frequency | Owner |
| --- | --- | --- |
| GitHub Actions success records and reviewed security alerts | Each merge and release | Project owner |
| Environment-secret inventory and access review | Monthly and after team changes | Project owner |
| Database backup confirmation and restore rehearsal record | Monthly | Hosting/database owner |
| Authentication, role, and data-ownership test results | Each release | Development team |
| Security incident log and response decisions | As needed | Project owner |
| External vulnerability scan or penetration-test report | Before a broader public launch | Independent assessor |

## External requirements that source code cannot complete

An accredited auditor must issue ISO 27001 or SOC 2 reports. A managed hosting, CDN, or web-application firewall provider must supply network-level DDoS controls. A monitoring provider must receive and deliver alerts. These services require the owner to choose providers, approve costs, configure accounts, and maintain evidence over time.

## Safe statement

> Student OS maintains repository evidence for layered security, controlled releases, and readiness checks. It is not yet independently certified or assessed for enterprise compliance.
