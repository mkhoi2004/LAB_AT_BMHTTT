@echo off
chcp 65001 > nul
echo Dang chay Unit Tests kiem thu thuat toan va CSDL...
call mvnw.cmd test
pause
