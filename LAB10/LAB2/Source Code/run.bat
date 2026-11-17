@echo off
chcp 65001 > nul
echo ========================================================
echo   LAB 2 - MÃ HÓA ĐỐI XỨNG HIỆN ĐẠI (JAVA SWING)
echo ========================================================
echo Dang khoi chay giao dien chinh (frm_Menu)...
if exist "%USERPROFILE%\.maven\apache-maven-3.9.6\bin\mvn.cmd" (
    call "%USERPROFILE%\.maven\apache-maven-3.9.6\bin\mvn.cmd" compile exec:java
) else (
    call mvnw.cmd compile exec:java
)
pause
