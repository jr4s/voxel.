@echo off
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
