FROM tomcat:11.0-jdk17
COPY target/student-os.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
