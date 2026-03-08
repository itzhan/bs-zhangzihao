@echo off
:: ============================================================
:: Farmstay Reservation System - Quick Start (Windows)
:: ============================================================

title Farmstay System Launcher

:: Start backend
start "Backend" cmd /k "cd /d %~dp0backend && mvn spring-boot:run"

:: Wait a moment before starting frontends
timeout /t 3 /nobreak >nul

:: Start admin panel
start "Admin" cmd /k "cd /d %~dp0admin && pnpm dev"

:: Start user frontend
start "Frontend" cmd /k "cd /d %~dp0frontend && pnpm dev"

echo.
echo ========================================
echo   Backend:  http://localhost:8090
echo   Admin:    http://localhost:8848
echo   Frontend: http://localhost:5173
echo ========================================
echo.
echo Press any key to exit this launcher...
pause >nul
