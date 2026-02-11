@echo off
setlocal EnableExtensions

cd /d "%~dp0"

set "OUT_DIR=out"
set "SOURCES_FILE=%TEMP%\parking-lot-sources-%RANDOM%%RANDOM%.txt"
set "CP=%OUT_DIR%;lib\sqlite-jdbc-3.46.0.0.jar;lib\slf4j-api-2.0.13.jar;lib\slf4j-simple-2.0.13.jar"

where java >nul 2>nul
if errorlevel 1 (
  echo [ERROR] Java runtime not found in PATH.
  echo         Install JDK 17+ and re-open this terminal.
  exit /b 1
)

where javac >nul 2>nul
if errorlevel 1 (
  echo [ERROR] javac not found in PATH.
  echo         Install JDK 17+ ^(not only JRE^) and re-open this terminal.
  exit /b 1
)

if not exist "lib\sqlite-jdbc-3.46.0.0.jar" (
  echo [ERROR] Missing dependency: lib\sqlite-jdbc-3.46.0.0.jar
  exit /b 1
)
if not exist "lib\slf4j-api-2.0.13.jar" (
  echo [ERROR] Missing dependency: lib\slf4j-api-2.0.13.jar
  exit /b 1
)
if not exist "lib\slf4j-simple-2.0.13.jar" (
  echo [ERROR] Missing dependency: lib\slf4j-simple-2.0.13.jar
  exit /b 1
)

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

if exist "%SOURCES_FILE%" del /f /q "%SOURCES_FILE%" >nul 2>nul
for /f "delims=" %%f in ('dir /s /b "src\main\java\*.java"') do (
  echo %%f>>"%SOURCES_FILE%"
)

if not exist "%SOURCES_FILE%" (
  echo [ERROR] Could not create source file list.
  exit /b 1
)

for %%A in ("%SOURCES_FILE%") do if %%~zA==0 (
  echo [ERROR] No Java source files found under src\main\java.
  del /f /q "%SOURCES_FILE%" >nul 2>nul
  exit /b 1
)

echo [INFO] Compiling sources...
javac -encoding UTF-8 -d "%OUT_DIR%" -cp "%CP%" @"%SOURCES_FILE%"
if errorlevel 1 goto :fail

echo [INFO] Starting app.Main...
java -cp "%CP%" app.Main
set "EXIT_CODE=%ERRORLEVEL%"

del /f /q "%SOURCES_FILE%" >nul 2>nul
exit /b %EXIT_CODE%

:fail
echo [ERROR] Compilation failed.
del /f /q "%SOURCES_FILE%" >nul 2>nul
exit /b 1
