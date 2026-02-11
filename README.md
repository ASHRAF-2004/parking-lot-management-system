# Parking Lot Management System

A Java Swing desktop application for managing a multi-floor parking lot with SQLite persistence, vehicle entry/exit processing, configurable fine schemes, payment capture, and operational reporting.

## Offline-First Setup (No Maven Required)

This repository is self-contained for offline usage:

- All required third-party JARs are vendored in `lib/`.
- No runtime download is performed.
- `make` targets run fully offline.

## Prerequisites

- Java 17+ (JDK, not just JRE)
- `make` and a POSIX shell (Linux/macOS native, Git Bash/MSYS2 on Windows)

## Included Local Dependencies

- `lib/sqlite-jdbc-3.46.0.0.jar`
- `lib/slf4j-api-2.0.13.jar`
- `lib/slf4j-simple-2.0.13.jar`

## Build and Run (Recommended)

### macOS/Linux

```bash
make clean
make run
```

### Windows (Git Bash / MSYS2)

```bash
make clean
make run
```

## Smoke Checks

Runs minimal end-to-end checks for:

- DB init
- Main startup flow / frame construction safety
- One entry/exit payment cycle

### macOS/Linux

```bash
make smoke
```

### Windows (Git Bash / MSYS2)

```bash
make smoke
```

## Direct `javac` / `java` Commands (Without `make`)

If you cannot use `make`, compile sources under `src/` and run `app.Main`.

### macOS/Linux

```bash
mkdir -p bin
javac -d bin -cp "bin:lib/sqlite-jdbc-3.46.0.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar" $(find src -type f -name '*.java')
java -cp "bin:lib/sqlite-jdbc-3.46.0.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar" app.Main
```

### Windows (PowerShell)

```powershell
New-Item -ItemType Directory -Force -Path bin | Out-Null
$files = Get-ChildItem -Recurse -Path src -Filter *.java | ForEach-Object { $_.FullName }
javac -d bin -cp "bin;lib/sqlite-jdbc-3.46.0.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar" $files
java -cp "bin;lib/sqlite-jdbc-3.46.0.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar" app.Main
```

## Project Structure

```text
.
├── src/
│   ├── main/java/           # Application source (app, model, repository, service, ui, util)
│   └── test/java/           # Smoke checks
├── lib/                     # Vendored third-party JAR dependencies
├── Makefile
├── pom.xml
└── README.md
```

## Notes

- Database file `parking.db` is created automatically in the project root at first run.
- Spot seeding occurs once when `parking_spots` is empty.
- In headless environments, UI launch is skipped by `app.Main`.
