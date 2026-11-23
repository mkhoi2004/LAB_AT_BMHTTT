@echo off
chcp 65001 >nul
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.4.101-hotspot"
set "MAVEN_HOME=C:\Users\ADMIN\.maven\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

echo ========================================================
echo  KHOI DONG HE THONG LAB 3 - MA HOA KHOA CONG KHAI
echo ========================================================
mvn compile exec:java
pause
