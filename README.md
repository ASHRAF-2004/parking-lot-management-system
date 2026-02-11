# Parking Lot Management System

A Java Swing desktop application for managing a multi-floor parking lot with SQLite persistence, vehicle entry/exit processing, configurable fine schemes, payment capture, and operational reporting.

## Offline-First Setup (No Maven Required)

This repository is now self-contained for offline usage:

- All required third-party JARs are vendored in `lib/`.
- No runtime download is performed.
- No Makefile step downloads anything.

## Prerequisites

- Java 17+ (JDK, not just JRE)

## Included Local Dependencies

The following files are committed in `lib/`:

- `lib/sqlite-jdbc-3.46.0.0.jar`
- `lib/slf4j-api-2.0.13.jar`
- `lib/slf4j-simple-2.0.13.jar`

## Build and Run (Fully Offline)

### Compile
```bash
make compile
```

### Run

```bash
make run
```

`make run` validates local JARs, compiles sources, and launches the app.

## Manual Run Commands (Offline)

### Compile

```bash
javac Main.java
```

### Run (Linux/macOS)

```bash
java -cp ".:lib/sqlite-jdbc-3.46.0.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar" Main
```

### Run (Windows)

```bat
java -cp ".;lib/sqlite-jdbc-3.46.0.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar" Main
```

---

## Project Structure for Plain `javac`

```text
.
├── Main.java                # default-package launcher entry point
├── app/                     # app.Main and startup flow
├── model/                   # domain objects, enums, DTOs
├── repository/              # SQLite access and repositories
├── service/                 # business logic (entry/exit/fine/report)
├── ui/                      # Swing UI (Admin, Entry/Exit, Reports)
├── util/                    # formatting/time/id helpers
├── lib/                     # vendored third-party JAR dependencies
└── src/main/java/...        # original Maven-style source tree (kept for reference)
```

## Notes

- Database file `parking.db` is created automatically in the project root at first run.
- Spot seeding occurs once when the parking spots table is empty.
- Maven file is kept for compatibility, but the recommended offline workflow is `make`/`javac` from this repository.
