# Build the JSP application as a WAR.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src
RUN mvn --batch-mode clean package -DskipTests

# Run the WAR with a Jakarta-compatible Tomcat release.
FROM tomcat:11.0-jdk17-temurin
COPY --from=build /app/target/student-os.war /usr/local/tomcat/webapps/ROOT.war
COPY docker-entrypoint.sh /usr/local/bin/student-os-entrypoint
RUN chmod +x /usr/local/bin/student-os-entrypoint

ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/student-os-entrypoint"]
