@echo off
setlocal EnableExtensions EnableDelayedExpansion

cd /d "%~dp0"

if not exist out mkdir out

set "CP=out;lib\sqlite-jdbc-3.46.0.0.jar;lib\slf4j-api-2.0.13.jar;lib\slf4j-simple-2.0.13.jar"

> sources.txt (
  for /r "src\main\java" %%f in (*.java) do (
    set "FILE=%%f"
    set "FILE=!FILE:\=/!"
    echo !FILE!
  )
)

javac -d out -cp "%CP%" @sources.txt
if errorlevel 1 goto :fail

java -cp "%CP%" app.Main
set "EXIT_CODE=%ERRORLEVEL%"

del sources.txt 2>nul
exit /b %EXIT_CODE%

:fail
echo Compilation failed.
del sources.txt 2>nul
exit /b 1
