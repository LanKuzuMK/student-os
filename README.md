# Student OS

> Your Life. Your Skills. Your Opportunities.

Student OS is a premium, full-stack Java web application designed as the ultimate university and freelance platform. It combines a life management system, a skill-sharing hub, and a freelance job board into one cohesive, beautifully designed product.

## Features

- **Premium Design System**: Custom-built, responsive CSS (no Bootstrap) with an expensive, minimal aesthetic.
- **MVC Architecture**: Built with Jakarta Servlets and JSP.
- **Embedded Database**: Uses SQLite for zero-configuration, out-of-the-box data persistence.
- **Authentication**: Secure registration and login with jBCrypt password hashing and OTP email verification simulation.
- **Life OS**: Manage personal goals, task progress, and priorities.
- **SkillSwap**: Discover peers to teach or learn new skills.
- **Freelance Hub**: Post, manage, and bid on student-centric freelance jobs.
- **Admin Dashboard**: Secured portal for platform analytics and user management.

## Tech Stack

- **Backend**: Java 17, Jakarta EE (Servlets/JSP)
- **Database**: SQLite (JDBC)
- **Server**: Apache Tomcat 11
- **Build Tool**: Apache Maven

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 17+
- Apache Maven
- Apache Tomcat 11+

### Installation & Deployment

1. Clone the repository.
2. Build the project using Maven:
   `ash
   mvn clean package
   `
3. Deploy the resulting 	arget/student-os.war to your Tomcat webapps directory.
4. Start Tomcat. The SQLite database will automatically initialize upon the first startup.

## Authors

- Designed and Developed as a comprehensive University Project.
