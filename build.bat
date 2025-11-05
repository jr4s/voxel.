@echo off
echo [1] Build
echo [2] Build and Run
echo.

set /p choice=: 

if "%choice%"=="1" goto build
if "%choice%"=="2" goto run
if "%choice%"=="3" exit
goto end

:build
echo Building Java project...
javac -d bin src\*.java
if %ERRORLEVEL% neq 0 (
    echo Build failed.
    pause
    exit /b
)
echo Build successful.
pause
goto end

:run
echo Compiling Java files...
javac -d bin src\*.java
if %ERRORLEVEL% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)
echo Running simulation...
java -cp bin Main
pause
goto end

:end
