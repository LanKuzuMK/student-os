#!/usr/bin/env sh
set -eu

: "${PORT:=10000}"

# Render routes traffic to the port in $PORT. The official Tomcat image defaults to 8080.
sed -i "s/port=\"8080\" protocol=\"HTTP\/1.1\"/port=\"${PORT}\" protocol=\"HTTP\/1.1\"/" "$CATALINA_HOME/conf/server.xml"

exec catalina.sh run
