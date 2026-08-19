#!/usr/bin/env sh
set -eu

: "${PORT:=8080}"
case "$PORT" in
  *[!0-9]* | "")
    echo "PORT must be a numeric TCP port" >&2
    exit 1
    ;;
esac

SERVER_XML="$CATALINA_HOME/conf/server.xml"

# Disable Tomcat's legacy shutdown listener. It is not needed in a container and
# prevents platform port checks from mistaking it for the web service.
sed -i 's/port="8005" shutdown="SHUTDOWN"/port="-1" shutdown="SHUTDOWN"/' "$SERVER_XML"

# Render routes HTTP traffic to $PORT. Tomcat's base image defaults to 8080.
sed -i "s/port=\"8080\" protocol=\"HTTP\/1.1\"/port=\"${PORT}\" protocol=\"HTTP\/1.1\"/" "$SERVER_XML"

exec catalina.sh run
