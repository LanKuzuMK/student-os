# StudentOS — Project Structure & Architectural Map

StudentOS is a multi-tier, monolithic web application built on the classic **4-Tier MVC Architecture** (Model-View-Controller) using pure Jakarta EE Servlets, JSP/JSTL, and raw JDBC with connection pooling. It runs on Java 17, containerized via Docker on Apache Tomcat, backed by PostgreSQL.

---

## 1. High-Level Architectural Flow

```
[ Browser / Client ]
        │ HTTP / HTTPS (GET, POST)
        ▼
[ Security & Operational Filters ] (AuthFilter, SecurityHeadersFilter, RequestTraceFilter, LoggingFilter)
        │ Filter Chain Forward
        ▼
[ Servlet Controllers ] (src/main/java/com/studentos/controller/)
        │ Calls Business Logic
        ▼
[ Service & Policy Layer ] (src/main/java/com/studentos/service/ & util/)
        │ Executes Data Operations
        ▼
[ Data Access Objects (DAO) ] (src/main/java/com/studentos/dao/)
        │ SQL via HikariCP Connection Pool (DBConnection.java)
        ▼
[ PostgreSQL Database ] (users, profiles, collaborations, skills, tasks, messages, etc.)
        │ Returns Entity Data / Models (src/main/java/com/studentos/model/)
        ▼
[ JSP / JSTL Presentation Layer ] (src/main/webapp/views/)
        │ Rendered HTML / CSS
        ▼
[ Browser Response ]
```

---

## 2. Directory Tree Overview

```
student-os/
├── .github/                      # CI/CD workflows (GitHub Actions)
├── database/                     # PostgreSQL schema scripts and seed data
│   ├── schema.sql                # Complete table definitions, foreign keys, and indexes
│   └── seed.sql                  # Seed fixtures for local testing
├── docs/                         # Security hardening reports, readiness logs, and release notes
│   ├── releases/                 # Versioned release notes (v1.0.0, v1.0.1)
│   └── ...                       # Security hardening & credential management documentation
├── src/
│   ├── main/
│   │   ├── java/com/studentos/   # Backend Java source code (Controllers, DAOs, Models, Services)
│   │   └── webapp/               # Web frontend (JSP templates, CSS, JS, assets, WEB-INF)
│   └── test/                     # Unit and integration test suites
├── Dockerfile                    # Multi-stage production container build (Maven compile -> Tomcat 10)
├── docker-entrypoint.sh          # Container runtime startup hook
├── pom.xml                       # Maven project dependencies and build configuration
├── render.yaml                   # Infrastructure-as-code specification for Render cloud hosting
└── README.md                     # Project overview and runbook
```

---

## 3. Backend Source Breakdown (`src/main/java/com/studentos/`)

The backend codebase is strictly modularized into functional packages:

### `config/` — Lifecycle & Initialization
* `AppContextListener.java`: ServletContextListener that initializes database tables (`InitDB.init()`) on application startup.

### `filter/` — Middleware & Security Pipeline
* `AuthFilter.java`: Session validator protecting authenticated routes (`/dashboard`, `/profile`, `/tasks`, `/messages`, etc.) and enforcing active user status and auth-version consistency.
* `SecurityHeadersFilter.java`: Injects defensive HTTP headers (CSP, HSTS, X-Content-Type-Options, X-Frame-Options, Referrer-Policy).
* `RequestTraceFilter.java`: Generates unique correlation trace IDs per request for diagnostic tracking.
* `LoggingFilter.java`: Structured request and response logging with execution duration metrics.
* `UnreadMessageFilter.java`: Dynamically computes unread message counts across authenticated requests.

### `controller/` — Presentation Controllers (Servlets)
* `AuthController.java`: Core authentication routes (`/auth/login`, `/auth/register`, `/auth/verify`, `/auth/forgot`, `/auth/reset`, `/auth/logout`).
* `SignInController.java`: Dedicated endpoint for handling sign-in form submissions and rate-limiting.
* `PasswordController.java`: Authenticated in-app password changes.
* `DashboardController.java`: Aggregates active goals, tasks, unread notifications, and quick actions into the student hub.
* `ProfileController.java`: User profile management, bio editing, links, and public profile views.
* `CollaborationController.java`: Peer collaboration requests (proposing, accepting, declining, cancelling).
* `ProjectSpaceController.java`: Team workspaces, contributor management, milestones, and project-specific tasks.
* `TaskController.java`: Personal student task board (Kanban / status-based task tracking).
* `GoalController.java`: Long-term student goals and progress percentages.
* `SkillController.java`: Peer skill-sharing directory and service listings (Teach vs. Learn).
* `FreelanceController.java`: Campus freelance gig board and student services.
* `MessageController.java`: Student direct messaging, conversation history, user blocking, and reporting.
* `NotificationController.java`: In-app notification center.
* `SavedItemController.java`: Bookmarking system for saving profiles, skills, services, and jobs.
* `ScheduleController.java`: Weekly timetable and class schedule viewer.
* `AdminController.java`: Administrative moderation dashboard, user audits, report resolutions, and platform health.
* `ReportController.java`: Reporting objectionable content or student abuse.
* `HealthController.java`: Liveness and readiness health-check probe.

### `service/` — Business Logic
* `AuthService.java`: User credential verification, BCrypt password hashing, registration transaction handling, and password reset flows.
* `EmailService.java`: Dispatches transactional verification codes and password reset emails via the Brevo REST API.

### `dao/` — Data Access Objects (JDBC)
All database interactions use parameterized SQL statements to prevent SQL injection:
* `UserDAO.java`: User lookup, creation, and password hash updates.
* `ProfileDAO.java`: Profile data, custom links, and showcase projects.
* `AuthSessionDAO.java`: Persistent login session tokens and expiry tracking.
* `EmailVerificationDAO.java`: Registration OTP code generation, BCrypt code hashing, and consumption.
* `PasswordResetDAO.java`: Password reset code generation and consumption.
* `CollaborationRequestDAO.java`: Peer collaboration proposal states.
* `ProjectSpaceDAO.java`: Project workspaces, member permissions, milestones, and tasks.
* `TaskDAO.java`: Personal student task records.
* `GoalDAO.java`: Goal tracking records.
* `SkillDAO.java`: Skills exchange records.
* `JobDAO.java`: Freelance job postings and offers.
* `MessageDAO.java`: Peer messaging threads and deletion markers.
* `NotificationDAO.java`: User notification records.
* `SavedItemDAO.java`: Bookmarked entity records.
* `UserBlockDAO.java`: Inter-user blocking relationships.
* `ModerationDAO.java`: Reports, audit trails, and moderation state.
* `AdminDAO.java`: Platform analytics and administrative user management.

### `model/` — Domain POJOs
Plain Java objects matching database entities:
`User`, `Profile`, `ProfileLink`, `ProfileProject`, `Task`, `Goal`, `Skill`, `Job`, `Message`, `Notification`, `CollaborationRequest`, `ProjectSpace`, `ProjectMember`, `ProjectMilestone`, `ProjectTask`, `SavedItem`, `DashboardSummary`, `DashboardAction`.

### `util/` — Utilities, Guards & Policies
* `DBConnection.java`: PostgreSQL connection management using HikariCP connection pooling.
* `InitDB.java`: Idempotent table and index creation on application boot.
* `CsrfUtil.java`: Synchronizer token pattern generation and validation for POST requests.
* `AuthAttemptLimiter.java`: In-memory sliding-window rate limiter against brute-force attacks.
* `BCryptUtil.java`: Password hashing wrapper.
* `InputValidator.java`: Input validation rules for emails, passwords, and numerical bounds.
* `AccessPolicy.java`, `CollaborationPolicy.java`, `DiscoveryPolicy.java`, `ModerationPolicy.java`, `ProjectSpacePolicy.java`, `SavedItemPolicy.java`: Centralized business rules and permission checks.
* `SessionVersionUtil.java`: Session invalidation on password change via version bumping.
* `PersistentSessionManager.java`: "Remember Me" persistent cookie authentication tokens.
* `VerificationCodeUtil.java`: Secure random 6-digit OTP code generator.
* `HtmlUtil.java`: HTML escaping utility preventing Cross-Site Scripting (XSS).

---

## 4. Web Application Frontend (`src/main/webapp/`)

### `WEB-INF/`
* `web.xml`: Core servlet descriptor configuring context attributes, session timeout, and error page mappings (`403`, `404`, `500`).

### `views/` — JSP Templates
View components organized by domain feature:
* `admin/`: Moderation queues, system audits, platform health, user lists (`index.jsp`, `audit.jsp`, `content.jsp`, `health.jsp`, `messages.jsp`, `reports.jsp`, `users.jsp`).
* `auth/`: Authentication views (`login.jsp`, `register.jsp`, `verify.jsp`, `forgot.jsp`, `reset.jsp`, `password.jsp`).
* `collaborations/`: Collaboration proposal inbox and sent views (`index.jsp`).
* `dashboard/`: Student dashboard home (`index.jsp`).
* `error/`: Custom HTTP error pages (`403.jsp`, `404.jsp`, `500.jsp`).
* `freelance/`: Job postings and campus gig listings (`index.jsp`, `services.jsp`).
* `life/`: Lifestyle productivity views (`goals.jsp`, `schedule.jsp`).
* `messages/`: Direct messaging chat interface (`index.jsp`).
* `notifications/`: Notification feed (`index.jsp`).
* `profile/`: Student profile editor and public portfolio viewer (`manage.jsp`, `view.jsp`).
* `projects/`: Collaborative workspace project hub (`index.jsp`, `workspace.jsp`).
* `saved/`: Saved / bookmarked items center (`index.jsp`).
* `skills/`: Peer skill directory and teacher/learner matching (`index.jsp`, `discover.jsp`).

### `css/`, `js/`, `assets/` — Static Assets
* `css/main.css`: Global styles, layout grid, typography, and dark/light color tokens.
* `js/accessibility.js`, `js/mobile-nav.js`, `js/theme-toggle.js`, `js/admin-nav.js`: Client-side UI enhancements.
* `assets/`: Vector icons (`icons.svg`), theme icons (`theme-sun.svg`, `theme-moon.svg`), and branding logos.

---

## 5. Deployment & Runtime Architecture

* **Containerization**: A multi-stage `Dockerfile` uses `maven:3.9-eclipse-temurin-17` to compile the `.war` package, then deploys to `tomcat:10.1-jdk17-temurin` running as an unprivileged user (`studentos:10001`).
* **Cloud Orchestration**: Configured via `render.yaml` as a Web Service running on Linux containers attached to a managed PostgreSQL database.
