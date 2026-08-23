# Build the JSP application as a WAR.
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src
RUN mvn --batch-mode clean package -DskipTests

# Run the WAR with a Jakarta-compatible Tomcat release.
FROM tomcat:11.0-jdk17-temurin

# Keep the application process separate from root while granting it only the
# Tomcat paths it needs to configure and run at container startup.
RUN groupadd --system --gid 10001 studentos \
    && useradd --system --uid 10001 --gid studentos --create-home --shell /usr/sbin/nologin studentos \
    && chown -R studentos:studentos /usr/local/tomcat/conf /usr/local/tomcat/logs /usr/local/tomcat/temp /usr/local/tomcat/webapps /usr/local/tomcat/work

COPY --chown=studentos:studentos --from=build /app/target/student-os.war /usr/local/tomcat/webapps/ROOT.war
COPY --chown=studentos:studentos docker-entrypoint.sh /usr/local/bin/student-os-entrypoint
RUN chmod +x /usr/local/bin/student-os-entrypoint

ENV PORT=8080
EXPOSE 8080
USER studentos
ENTRYPOINT ["/usr/local/bin/student-os-entrypoint"]
