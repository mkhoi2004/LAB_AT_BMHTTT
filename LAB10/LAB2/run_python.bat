@echo off
chcp 65001 > nul
echo ========================================================
echo   LAB 2 - BÀI NÂNG CAO 02: AES-256-CBC (PYTHON TKINTER)
echo ========================================================
echo [1] Kiem thu tu dong (self-test):
python aes_cbc_gui.py --self-test
echo.
echo [2] Khoi chay giao dien Tkinter...
python aes_cbc_gui.py
pause
