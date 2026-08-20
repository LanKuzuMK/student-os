# StudentOS

![StudentOS](src/main/webapp/assets/studentos-logo-transparent.png)

> A Java web platform made for students to manage their work, grow their skills, and connect with other students.

[![Java 17](https://img.shields.io/badge/Java-17-1f6feb?style=flat-square&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Jakarta EE](https://img.shields.io/badge/Jakarta_EE-Servlets_%2B_JSP-5d5ce2?style=flat-square)](https://jakarta.ee/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-336791?style=flat-square&logo=postgresql&logoColor=white)](https://neon.tech/)
[![Tomcat 11](https://img.shields.io/badge/Tomcat-11-f8dc75?style=flat-square&logo=apachetomcat&logoColor=black)](https://tomcat.apache.org/)
[![Maven](https://img.shields.io/badge/Maven-WAR-C71A36?style=flat-square&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Render](https://img.shields.io/badge/Deployed_on-Render-46E3B7?style=flat-square&logo=render&logoColor=white)](https://student-os-v2.onrender.com/)

[Live Demo](https://student-os-v2.onrender.com/) · [Setup Guide](SETUP.md) · [Database Notes](DATABASE.md) · [Contributing](CONTRIBUTING.md)

---

## About the Project

StudentOS is a university web project built to put common student activities in one simple platform. Instead of keeping study tasks, goals, skill notes, and contacts in different places, students can use one dashboard to plan their work and meet classmates.

I built this project with Java, Jakarta Servlets, JSP, PostgreSQL, and Apache Tomcat. The project follows a simple MVC structure, so the controller, data access, models, and pages are separated. This makes the project easier to understand, improve, and present as a second-year student project.

## What Students Can Do

| Area | Main functions |
|---|---|
| Dashboard | View current tasks, progress, and quick student activity in one workspace. |
| Tasks and goals | Create tasks, finish tasks, manage goals, update goal progress, and remove completed tasks from the schedule. |
| Skills | Add personal skills, discover other student skills, and find classmates for collaboration. |
| Messages | Contact another student by email, see unread-message badges, and delete private conversation history. |
| Freelance area | Browse student freelance jobs and services in the same platform. |
| Accounts | Register, verify an account with the demo OTP flow, sign in, and sign out safely. |

## Main Technology

| Layer | Tools used |
|---|---|
| Backend | Java 17, Jakarta Servlets, JSP |
| Build | Apache Maven with WAR packaging |
| Server | Apache Tomcat 11 |
| Database | PostgreSQL on Neon |
| Database pool | HikariCP |
| Security | jBCrypt password hashing |
| Frontend | JSP, JSTL, CSS, SVG icons |
| Hosting | Docker and Render |

## Project Structure

```text
student-os/
├── src/main/java/com/studentos/
│   ├── config/       # application startup configuration
│   ├── controller/   # servlet controllers and routes
│   ├── dao/          # PostgreSQL queries and data access
│   ├── filter/       # authentication and unread-message helpers
│   ├── model/        # Java data models
│   ├── service/      # business logic
│   └── util/         # database connection and migrations
├── src/main/webapp/
│   ├── WEB-INF/      # servlet and filter mappings
│   ├── assets/       # logo and SVG icon assets
│   ├── css/          # StudentOS design system
│   └── views/        # JSP pages
├── database/          # reference database schema
├── Dockerfile         # Maven build and Tomcat runtime image
├── render.yaml        # Render service configuration
└── pom.xml            # Maven dependencies and WAR build settings
```

## Run It Locally

### 1. Requirements

Install Java 17, Apache Maven, Docker, and a PostgreSQL database. Neon PostgreSQL is recommended because it matches the deployed project.

### 2. Configure the database

Copy the example environment file and add your own Neon connection string. Do not commit the real `.env` file.

```bash
cp .env.example .env
```

Set `DATABASE_URL` in `.env` with the full Neon pooled connection string. Keep the `sslmode=require` parameters supplied by Neon.

### 3. Build the application

```bash
mvn clean package
```

Maven creates the deployable file at `target/student-os.war`.

### 4. Run with Docker

```bash
docker build -t student-os .
docker run --rm --env-file .env -p 8080:8080 student-os
```

Open [http://localhost:8080](http://localhost:8080) in your browser.

## Deploy on Render

This repository includes `render.yaml` for the Docker web service. In Render, add `DATABASE_URL` as a secret environment variable and use the full Neon connection string. The container reads the port assigned by Render and starts Tomcat on that port.

The current live project is available at [student-os-v2.onrender.com](https://student-os-v2.onrender.com/).

## Demo Account

For a quick project review, use the following demo account:

```text
Email: mong@example.com
Password: password
```

## Notes for the Project

This project was designed and developed by **MKV Team** as a university project. The goal is not only to make a nice interface, but also to practice real Java web development topics such as MVC structure, authentication, JSP rendering, database connections, CRUD operations, and deployment.

## License

StudentOS is available under the [MIT License](LICENSE). Copyright © 2026 MKV Team.
