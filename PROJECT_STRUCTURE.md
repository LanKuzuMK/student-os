# StudentOS — Detailed File Structure

> **Stack**: Java 17 · Jakarta EE Servlets · JSP/JSTL · JDBC · HikariCP · PostgreSQL · Apache Tomcat 10 · Docker · Render

---

```
student-os/
|
|-- .github/                              # CI/CD automation (GitHub Actions)
|   |-- dependabot.yml                    # Automated dependency update rules
|   L-- workflows/
|       |-- codeql.yml                    # Static code security analysis (SAST)
|       |-- container-verify.yml          # Docker image build verification on PRs
|       L-- publish-container.yml         # Publishes Docker image to GitHub Container Registry
|
|-- .mvn/                                 # Maven wrapper configuration
|   L-- wrapper/
|       |-- maven-wrapper.jar             # Embedded Maven bootstrapper
|       L-- maven-wrapper.properties      # Maven version pinning
|
|-- database/                             # Raw PostgreSQL scripts
|   |-- schema.sql                        # All table definitions, foreign keys, and indexes
|   L-- seed.sql                          # Seed data for local development and testing
|
|-- docs/                                 # Technical documentation and release history
|   |-- ENTERPRISE_READINESS_EVIDENCE.md  # Evidence log of hardening measures applied
|   |-- RENDER_CREDENTIAL_MANAGEMENT.md   # Guide for managing secrets on Render
|   |-- SECURITY_HARDENING_2026-08.md     # Security hardening report (August 2026)
|   |-- SIX_PRIORITY_RELEASE_2026-08-22.md # Six-feature priority release notes
|   |-- TIER4_READINESS_2026-08.md        # Tier-4 architecture readiness assessment
|   L-- releases/
|       |-- v1.0.0-mkvteam.md             # Release notes — initial launch
|       L-- v1.0.1-mkvteam.md             # Release notes — first patch
|
|-- src/
|   |-- main/
|   |   |
|   |   |-- java/com/studentos/           # === BACKEND JAVA SOURCE ===
|   |   |   |
|   |   |   |-- config/                   # Application lifecycle hooks
|   |   |   |   L-- AppContextListener.java   # Bootstraps DB tables on Tomcat startup via InitDB
|   |   |   |
|   |   |   |-- filter/                   # Servlet filters (request middleware pipeline)
|   |   |   |   |-- AuthFilter.java           # Validates session, role, and auth_version on protected routes
|   |   |   |   |-- LoggingFilter.java        # Logs method, path, and execution duration per request
|   |   |   |   |-- RequestTraceFilter.java   # Generates a unique trace ID per request for debugging
|   |   |   |   |-- SecurityHeadersFilter.java # Injects CSP, HSTS, X-Frame-Options, Referrer-Policy
|   |   |   |   L-- UnreadMessageFilter.java  # Injects unread message count into all authenticated views
|   |   |   |
|   |   |   |-- controller/               # Servlet controllers (HTTP routing layer)
|   |   |   |   |-- AdminController.java       # Admin dashboard: user management, moderation, reports, audit
|   |   |   |   |-- AuthController.java        # /auth/login, /register, /verify, /forgot, /reset, /logout
|   |   |   |   |-- CollaborationController.java # Collaboration proposals: propose, accept, decline, cancel
|   |   |   |   |-- DashboardController.java   # Student home: tasks summary, goals, notifications, actions
|   |   |   |   |-- FreelanceController.java   # Campus gig board: job listings and student freelance services
|   |   |   |   |-- GoalController.java        # Personal goal management and progress tracking
|   |   |   |   |-- HealthController.java      # /health liveness probe for deployment health checks
|   |   |   |   |-- MessageController.java     # Student direct messaging, conversation history, block/report
|   |   |   |   |-- NotificationController.java # In-app notification feed and read state management
|   |   |   |   |-- PasswordController.java    # Authenticated password change (requires current password)
|   |   |   |   |-- ProfileController.java     # Profile editor, custom links, showcase projects, public view
|   |   |   |   |-- ProjectSpaceController.java # Team workspaces, members, milestones, and project tasks
|   |   |   |   |-- ReportController.java      # Submit abuse/content reports against posts or users
|   |   |   |   |-- SavedItemController.java   # Bookmark/save profiles, skills, services, and jobs
|   |   |   |   |-- ScheduleController.java    # Weekly timetable and class schedule management
|   |   |   |   |-- SignInController.java      # Dedicated /auth/signin endpoint with rate limiting
|   |   |   |   |-- SkillController.java       # Peer skill directory: list, add, delete (Teach / Learn)
|   |   |   |   L-- TaskController.java        # Personal Kanban task board (TODO / IN_PROGRESS / DONE)
|   |   |   |
|   |   |   |-- service/                  # Business logic layer
|   |   |   |   |-- AuthService.java          # Login, registration, email OTP, password reset flows (BCrypt)
|   |   |   |   L-- EmailService.java         # Sends verification/reset codes via Brevo HTTPS REST API
|   |   |   |
|   |   |   |-- dao/                      # Data access layer — all SQL via prepared statements (JDBC)
|   |   |   |   |-- AdminDAO.java             # Platform stats, user search, admin role management
|   |   |   |   |-- AuthSessionDAO.java       # Persistent login session tokens (Remember Me)
|   |   |   |   |-- CollaborationRequestDAO.java # Collaboration request state and lookup queries
|   |   |   |   |-- EmailVerificationDAO.java # OTP hash storage, cooldown, and consumption for registration
|   |   |   |   |-- GoalDAO.java              # Goal CRUD and progress percentage updates
|   |   |   |   |-- JobDAO.java               # Freelance job posting CRUD and status queries
|   |   |   |   |-- MessageDAO.java           # Message threads, sender/receiver delete markers
|   |   |   |   |-- ModerationDAO.java        # Report records, resolution state, audit log entries
|   |   |   |   |-- NotificationDAO.java      # Notification records, mark-as-read, unread counts
|   |   |   |   |-- PasswordResetDAO.java     # Reset code hash storage, cooldown, and consumption
|   |   |   |   |-- ProfileDAO.java           # Profile data, custom links, and showcase projects
|   |   |   |   |-- ProjectSpaceDAO.java      # Workspace CRUD, members, milestones, and tasks
|   |   |   |   |-- SavedItemDAO.java         # Bookmarked item records (PROFILE / SKILL / SERVICE / JOB)
|   |   |   |   |-- SkillDAO.java             # Skill discovery records (type: TEACH or LEARN)
|   |   |   |   |-- TaskDAO.java              # Personal student task records and status updates
|   |   |   |   |-- UserBlockDAO.java         # Inter-user blocking relationships
|   |   |   |   L-- UserDAO.java              # User lookup by email/ID, creation, password hash updates
|   |   |   |
|   |   |   |-- model/                    # Domain entity POJOs (plain Java objects matching DB tables)
|   |   |   |   |-- CollaborationRequest.java # Peer collaboration proposal entity
|   |   |   |   |-- DashboardAction.java      # Quick action card shown on the student dashboard
|   |   |   |   |-- DashboardSummary.java     # Aggregated metrics object for dashboard rendering
|   |   |   |   |-- Goal.java                 # Student long-term goal entity
|   |   |   |   |-- Job.java                  # Freelance job posting entity
|   |   |   |   |-- Message.java              # Direct message entity
|   |   |   |   |-- Notification.java         # In-app notification entity
|   |   |   |   |-- Profile.java              # Student profile entity (name, bio, university, links)
|   |   |   |   |-- ProfileLink.java          # Custom social/portfolio link attached to a profile
|   |   |   |   |-- ProfileProject.java       # Showcase project card attached to a profile
|   |   |   |   |-- ProjectMember.java        # Project workspace team member (OWNER / CONTRIBUTOR)
|   |   |   |   |-- ProjectMilestone.java     # Milestone entry in a project workspace
|   |   |   |   |-- ProjectSpace.java         # Collaborative team workspace entity
|   |   |   |   |-- ProjectTask.java          # Task assigned within a project workspace
|   |   |   |   |-- SavedItem.java            # Bookmarked entity record
|   |   |   |   |-- Skill.java                # Peer skill entry (name, level, type, owner)
|   |   |   |   |-- Task.java                 # Personal student task entity
|   |   |   |   L-- User.java                 # Core user entity (id, email, role, status, authVersion)
|   |   |   |
|   |   |   L-- util/                     # Shared utilities, guards, and business rule policies
|   |   |       |-- AccessPolicy.java         # Post-login redirect paths by role (STUDENT / ADMIN)
|   |   |       |-- AppHealth.java            # Runtime health check data aggregation
|   |   |       |-- AuthAttemptLimiter.java   # In-memory sliding-window rate limiter (8 attempts / 15 min)
|   |   |       |-- BCryptUtil.java           # BCrypt hash and verify wrapper
|   |   |       |-- CollaborationPolicy.java  # Business rules for collaboration request eligibility
|   |   |       |-- CsrfUtil.java             # Synchronizer token generation and validation for POST forms
|   |   |       |-- DashboardCoach.java       # Generates profile completion tips and coaching prompts
|   |   |       |-- DBConnection.java         # HikariCP PostgreSQL connection pool (5 connections default)
|   |   |       |-- DiscoveryPolicy.java      # Visibility rules for skill/service discovery feeds
|   |   |       |-- HtmlUtil.java             # HTML escaping to prevent XSS in JSP output
|   |   |       |-- InitDB.java               # Idempotent DB schema runner (CREATE TABLE IF NOT EXISTS)
|   |   |       |-- InputValidator.java       # Email/password/budget validation rules
|   |   |       |-- ModerationPolicy.java     # Content moderation permission and status rules
|   |   |       |-- PersistentSessionManager.java # "Remember Me" cookie token management
|   |   |       |-- ProjectSpacePolicy.java   # Workspace access and permission rules
|   |   |       |-- SavedItemPolicy.java      # Saved item type validation and ownership rules
|   |   |       |-- SessionVersionUtil.java   # Detects stale sessions after password changes
|   |   |       L-- VerificationCodeUtil.java # Cryptographically random 6-digit OTP generator
|   |   |
|   |   L-- webapp/                       # === WEB FRONTEND ===
|   |       |
|   |       |-- WEB-INF/
|   |       |   L-- web.xml               # Servlet descriptor: session timeout, error page mappings
|   |       |
|   |       |-- assets/                   # Static brand and icon assets
|   |       |   |-- icons.svg             # SVG sprite sheet for all UI icons
|   |       |   |-- studentos-logo-transparent.png # Application logo
|   |       |   |-- theme-moon.svg        # Dark mode toggle icon
|   |       |   |-- theme-sun.svg         # Light mode toggle icon
|   |       |   L-- user-outline.svg      # Default user avatar placeholder icon
|   |       |
|   |       |-- css/
|   |       |   L-- main.css              # Global design system: tokens, typography, layout, components
|   |       |
|   |       |-- js/
|   |       |   |-- accessibility.js      # Keyboard navigation and ARIA enhancements
|   |       |   |-- admin-nav.js          # Admin sidebar navigation behavior
|   |       |   |-- mobile-nav.js         # Responsive hamburger menu behavior
|   |       |   L-- theme-toggle.js       # Light/dark mode persistence and switching
|   |       |
|   |       |-- favicon.png               # Browser tab favicon
|   |       |-- index.jsp                 # Root redirect (unauthenticated -> /auth/signin)
|   |       |
|   |       L-- views/                    # JSP/JSTL page templates (organized by feature)
|   |           |
|   |           |-- admin/                # Admin panel pages (role: ADMIN only)
|   |           |   |-- audit.jsp         # Moderation action audit log timeline
|   |           |   |-- content.jsp       # Flagged content moderation queue
|   |           |   |-- health.jsp        # Platform health metrics dashboard
|   |           |   |-- index.jsp         # Admin home: user stats, platform overview
|   |           |   |-- messages.jsp      # Admin message inspection and monitoring
|   |           |   |-- reports.jsp       # Abuse report queue and resolution panel
|   |           |   L-- users.jsp         # User list: search, status, role management
|   |           |
|   |           |-- auth/                 # Authentication and account recovery pages
|   |           |   |-- forgot.jsp        # Forgot password: email input form
|   |           |   |-- login.jsp         # Sign-in form (email + password)
|   |           |   |-- password.jsp      # Change password form (authenticated)
|   |           |   |-- register.jsp      # New account registration form
|   |           |   |-- reset.jsp         # Password reset: OTP code + new password form
|   |           |   L-- verify.jsp        # Email verification OTP entry form
|   |           |
|   |           |-- collaborations/
|   |           |   L-- index.jsp         # Collaboration proposals inbox and sent list
|   |           |
|   |           |-- dashboard/
|   |           |   L-- index.jsp         # Student hub: focus list, goals, notifications, actions
|   |           |
|   |           |-- error/                # Custom HTTP error pages
|   |           |   |-- 403.jsp           # 403 Forbidden
|   |           |   |-- 404.jsp           # 404 Not Found
|   |           |   L-- 500.jsp           # 500 Internal Server Error
|   |           |
|   |           |-- freelance/
|   |           |   |-- index.jsp         # Campus job board: freelance gig listings
|   |           |   L-- services.jsp      # Student service offerings (marketplace)
|   |           |
|   |           |-- life/
|   |           |   |-- goals.jsp         # Long-term student goals and progress tracker
|   |           |   L-- schedule.jsp      # Weekly timetable / class schedule view
|   |           |
|   |           |-- messages/
|   |           |   L-- index.jsp         # Student-to-student messaging with block/report actions
|   |           |
|   |           |-- notifications/
|   |           |   L-- index.jsp         # In-app notification feed
|   |           |
|   |           |-- profile/
|   |           |   |-- manage.jsp        # Profile editor: bio, university, skills, links, projects
|   |           |   L-- view.jsp          # Public student portfolio page (initial-letter avatar)
|   |           |
|   |           |-- projects/
|   |           |   |-- index.jsp         # Project workspaces index: list of joined/owned spaces
|   |           |   L-- workspace.jsp     # Individual workspace: members, milestones, and tasks
|   |           |
|   |           |-- saved/
|   |           |   L-- index.jsp         # Bookmarked items: profiles, skills, services, jobs
|   |           |
|   |           L-- skills/
|   |               |-- discover.jsp      # Skill discovery feed: browse all student skills and services
|   |               L-- index.jsp         # Personal skills manager: add/remove own skills
|   |
|   L-- test/                             # === TEST SUITE ===
|       L-- java/com/studentos/
|           |-- model/
|           |   |-- ProfileCollaborationTest.java   # Profile + collaboration eligibility unit tests
|           |   L-- SkillDiscoveryLabelTest.java    # Skill type label rendering tests
|           |-- service/
|           |   L-- EmailServiceIntegrationTest.java # Brevo API email delivery integration tests
|           L-- util/
|               |-- AccessPolicyTest.java           # Role-based redirect path tests
|               |-- AuthAttemptLimiterTest.java      # Rate limiter sliding window tests
|               |-- CollaborationPolicyTest.java     # Collaboration eligibility rule tests
|               |-- DashboardCoachTest.java          # Profile completion coaching prompt tests
|               |-- DiscoveryPolicyTest.java         # Content visibility rule tests
|               |-- HtmlUtilTest.java               # XSS escaping edge case tests
|               |-- InputValidatorTest.java          # Email and password validation tests
|               |-- ModerationPolicyTest.java        # Moderation permission rule tests
|               |-- PasswordPolicyRegressionTest.java # Password policy regression suite
|               |-- PersistentSessionManagerTest.java # Remember-Me token lifecycle tests
|               |-- ProjectSpacePolicyTest.java      # Workspace access rule tests
|               |-- SavedItemPolicyTest.java         # Saved item type validation tests
|               |-- SessionVersionUtilTest.java      # Auth-version staleness detection tests
|               L-- VerificationCodeUtilTest.java    # OTP generation randomness/format tests
|
|-- .dockerignore                         # Files excluded from Docker build context
|-- .env.example                          # Template for required environment variables
|-- .gitignore                            # Git-ignored files (target/, secrets, IDE files)
|-- CONTRIBUTING.md                       # Contribution guidelines and pull request process
|-- DATABASE.md                           # Database design decisions and schema reference
|-- Dockerfile                            # Multi-stage build: Maven compile -> Tomcat 10 runtime
|-- LICENSE                               # Project open source license
|-- LOCAL_DEVELOPMENT.md                  # Guide for running StudentOS locally
|-- PROJECT_STRUCTURE.md                  # High-level architectural reference (this file's companion)
|-- README.md                             # Project overview, features, and quick start
|-- SECURITY.md                           # Security policy and responsible disclosure guide
|-- SETUP.md                              # Environment variable and deployment setup guide
|-- deploy.ps1                            # Windows PowerShell deployment helper script
|-- docker-entrypoint.sh                  # Container startup script (sets file permissions)
|-- mvnw.cmd                              # Maven wrapper executable for Windows
|-- pom.xml                               # Maven: dependencies, Java 17, WAR packaging, compiler config
L-- render.yaml                           # Infrastructure-as-code: Render Web Service + PostgreSQL spec
```

---

## Layer Summary

| Layer | Package / Path | What It Does |
|:---|:---|:---|
| **Lifecycle** | `config/` | Boots DB schema tables on server start |
| **Middleware** | `filter/` | Auth guard, security headers, request tracing, logging |
| **Routing** | `controller/` | 19 Servlets handling HTTP GET/POST for each feature |
| **Business Logic** | `service/` | Auth flows, BCrypt hashing, email delivery |
| **Data Access** | `dao/` | 17 DAOs executing parameterized SQL against PostgreSQL |
| **Domain** | `model/` | 18 plain Java entity classes |
| **Utilities** | `util/` | CSRF, rate limiting, session management, policies, validation |
| **Presentation** | `webapp/views/` | 40 JSP/JSTL templates across 13 feature domains |
| **Static Assets** | `webapp/css/, js/, assets/` | Design system, scripts, icons, branding |
| **Tests** | `src/test/` | 17 unit test classes covering auth, policy, and utility logic |
