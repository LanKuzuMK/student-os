@echo off
cloudflared.exe tunnel --url http://localhost:8080 > tunnel.log 2>&1
