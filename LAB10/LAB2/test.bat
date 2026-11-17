@echo off
chcp 65001 > nul
echo ========================================================
echo   LAB 2 - KIỂM THỬ TỰ ĐỘNG (UNIT TESTS)
echo ========================================================
set "LAB2_SQL_PASSWORD=LabUser@2026"
set "LAB2_SQL_PORT=1433"
if exist "%USERPROFILE%\.maven\apache-maven-3.9.6\bin\mvn.cmd" (
    call "%USERPROFILE%\.maven\apache-maven-3.9.6\bin\mvn.cmd" test
) else (
    call mvnw.cmd test
)
pause
