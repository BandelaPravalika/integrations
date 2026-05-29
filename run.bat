@echo off
cd /d "%~dp0"
"D:\INTEGRATIONS\apache-maven\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
pause
