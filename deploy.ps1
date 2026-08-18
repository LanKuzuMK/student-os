Remove-Item -Path "C:\Program Files\Apache Software Foundation\Tomcat 11.0\webapps\ROOT" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "C:\Program Files\Apache Software Foundation\Tomcat 11.0\webapps\ROOT.war" -Force -ErrorAction SilentlyContinue

Copy-Item -Path "target\student-os.war" -Destination "C:\Program Files\Apache Software Foundation\Tomcat 11.0\webapps\ROOT.war" -Force

try {
    Start-Service Tomcat11 -ErrorAction Stop
} catch {
    Write-Output "Could not start service natively. Trying startup.bat..."
    Start-Process "C:\Program Files\Apache Software Foundation\Tomcat 11.0\bin\startup.bat"
}

Write-Output "Tomcat deployed successfully."
