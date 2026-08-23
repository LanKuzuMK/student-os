# StudentOS

![StudentOS](src/main/webapp/assets/studentos-logo-transparent.png)

> **StudentOS is a Java web platform for student planning, profiles, skills discovery, communication, collaboration, and role-aware community administration.**

[![Java 17](https://img.shields.io/badge/Java-17-1f6feb?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta_EE-Servlets_%2B_JSP-5d5ce2?style=flat-square)](https://jakarta.ee/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-336791?style=flat-square&logo=postgresql&logoColor=white)](https://neon.tech/)
[![Tomcat 11](https://img.shields.io/badge/Tomcat-11-f8dc75?style=flat-square&logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)
[![Maven](https://img.shields.io/badge/Maven-WAR-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Render](https://img.shields.io/badge/Hosted_on-Render-46E3B7?style=flat-square&logo=render&logoColor=white)](https://student-os-v2.onrender.com/)

[Live Application](https://student-os-v2.onrender.com/) · [Setup](SETUP.md) · [Local Development](LOCAL_DEVELOPMENT.md) · [Database Notes](DATABASE.md) · [Contributing](CONTRIBUTING.md) · [Security](SECURITY.md)

---

## Purpose

StudentOS brings common student activities into one structured workspace. Students can plan work, track goals, build a visible profile, discover classmates with relevant skills, communicate safely, save opportunities, and collaborate through project-oriented features. Staff users have separate protected tools for moderation, audit review, account oversight, and a non-sensitive health check.

The project is designed as a **strong Tier 3 pilot foundation**: it has real cloud persistence, role-aware access control, production-readiness checks, and an honest roadmap for further real-device and multi-user validation. It is not described as an enterprise platform.

## Core Capabilities

| Area | Current capability |
| --- | --- |
| Personal workspace | Dashboard, tasks, schedule, goals, progress tracking, and completed-task cleanup. |
| Profiles and discovery | Student profiles, skills, social links, saved items, and discovery of relevant classmates or opportunities. |
| Communication | Email-based recipient lookup, inbox, unread state, notifications, conversation controls, blocking, and reporting. |
| Collaboration | Project spaces and student-focused collaboration context. |
| Account safety | Registration, emailed verification and recovery flows, password changes, session revocation, and persistent sessions with an inactivity boundary. |
| Governance | Protected staff routes, reports, moderation controls, audit search, paginated administration, and a read-only health diagnostic. |
| Responsive experience | Mobile navigation drawer, saved light/dark appearance, keyboard-focus support, and clear empty states. |

## Technology and Architecture

| Layer | Technology | Responsibility |
| --- | --- | --- |
| Web interface | JSP, JSTL, CSS, JavaScript | Server-rendered pages, responsive layout, accessibility, and visual design. |
| Application layer | Java 17, Jakarta Servlets | Routes, validation, role checks, request handling, and page responses. |
| Data access | JDBC, DAO pattern, HikariCP | Parameterized PostgreSQL queries and connection management. |
| Database | PostgreSQL on Neon | Relational storage for users, workspaces, communication, reports, and audit data. |
| Security | BCrypt, CSRF validation, access filters | Password protection, request safety, ownership checks, role boundaries, and session control. |
| Delivery | Maven, WAR, Docker, Tomcat 11, Render | Repeatable build, containerized deployment, and managed hosting. |
| Email | Brevo HTTPS transactional delivery | Account verification and recovery messages. |

StudentOS follows a practical MVC-style structure:

```text
Browser / JSP page
        ↓
Servlet controller and request filters
        ↓
Validation, ownership, and role checks
        ↓
DAO layer and parameterized JDBC queries
        ↓
PostgreSQL on Neon
        ↓
Updated JSP response
```

## Security and Operational Boundaries

- Passwords are verified with BCrypt hashes; they are never stored as readable text.
- Protected actions validate the signed-in user, role, ownership, and CSRF token where applicable.
- Staff administration is separated from the student workspace by protected server-side routes.
- Persistent sessions are revocable on logout, password changes, account-status changes, and authentication-version changes. A valid session uses a rolling inactivity boundary.
- The staff health page performs a read-only availability check and does not expose credentials, connection strings, reset codes, or secrets.
- Profile-photo processing is intentionally constrained to keep database storage appropriate for the free Neon tier. Media-heavy features such as video uploads are not part of the current scope.

See [SECURITY.md](SECURITY.md) for responsible reporting guidance.

## Project Structure

```text
student-os/
├── src/main/java/com/studentos/
│   ├── controller/     # servlet routes and request handling
│   ├── dao/            # PostgreSQL queries and data access
│   ├── filter/         # authentication, request, and UI-support filters
│   ├── model/          # Java domain models
│   ├── service/        # business services such as email delivery
│   └── util/           # database, migrations, sessions, and shared utilities
├── src/main/webapp/
│   ├── WEB-INF/        # servlet, filter, and error configuration
│   ├── assets/         # StudentOS brand assets
│   ├── css/            # shared light/dark responsive design system
│   ├── js/             # navigation, accessibility, and theme behavior
│   └── views/          # JSP pages
├── database/           # reference schema
├── docs/               # public project documentation
├── Dockerfile          # Maven build and Tomcat runtime image
├── render.yaml         # Render service configuration
└── pom.xml             # Maven dependencies and WAR build configuration
```

## Local Development

### Requirements

Install Java 17, Apache Maven, Docker, and access to a PostgreSQL database. The deployed system uses Neon PostgreSQL, but a compatible local PostgreSQL database may also be used for development.

### Configure environment variables

Copy the example environment file and add **your own** database connection details. Never commit real environment files, passwords, reset codes, API keys, or production connection strings.

```bash
cp .env.example .env
```

### Build and test

```bash
mvn -q test
mvn -q package
```

The deployable WAR is created at `target/student-os.war`.

### Run with Docker

```bash
docker build -t student-os .
docker run --rm --env-file .env -p 8080:8080 student-os
```

Open [http://localhost:8080](http://localhost:8080) in a browser.

## Deployment

The repository includes `render.yaml` for the Docker web service. Configure production secrets through the hosting environment rather than storing them in the repository. The current hosted application is available at [student-os-v2.onrender.com](https://student-os-v2.onrender.com/).

Before a broader rollout, validate the system with real devices and at least two independent student accounts. The current project has completed owner-controlled browser, route-boundary, workflow-cleanup, source-build, and controlled-restart checks.

## Team and Planned Responsibilities

StudentOS is a university project maintained under the **MKV Team** name. The table below describes planned team responsibilities; it does **not** claim completed code authorship or testing that has not yet happened.

| Team member | Planned responsibility | Planned contribution outcome |
| --- | --- | --- |
| **[Project owner]** | Application development, architecture, and deployment coordination | Maintains the Java application, deployment workflow, and technical direction. |
| **Khon Sokkheng** | Quality assurance and user-acceptance testing | Reviews student workflows, records real findings, and helps prioritize usability fixes. |
| **Nhouv Vanne** | Mobile usability and documentation review | Reviews responsive behavior on real devices and helps improve clear project documentation. |

When a team member completes real work, document the specific contribution through their own commit or pull request and update this section accordingly.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md). Keep changes focused, test the affected behavior, use your own account for your contributions, and do not commit secrets or personal test data.

## License

StudentOS is available under the [MIT License](LICENSE). Copyright © 2026 **MKV Team**.
