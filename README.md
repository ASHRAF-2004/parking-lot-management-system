# Parking Lot Management System

A Java Swing desktop application for managing a multi-floor parking lot with SQLite persistence, vehicle entry/exit processing, configurable fine schemes, payment capture, and operational reporting.

## Quick Start (No Maven Required)

This project is configured to run with plain Java commands.

### 1) Prerequisites

- Java 17+ (JDK, not just JRE)
- Internet access once to download required JARs

### 2) Download required runtime dependencies

Create a `lib/` directory in the project root and download:

```bash
mkdir -p lib
curl -L -o lib/sqlite-jdbc-3.46.0.0.jar https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.46.0.0/sqlite-jdbc-3.46.0.0.jar
curl -L -o lib/slf4j-api-2.0.13.jar https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.13/slf4j-api-2.0.13.jar
curl -L -o lib/slf4j-simple-2.0.13.jar https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.13/slf4j-simple-2.0.13.jar
```

> Why SLF4J jars too? Recent versions of `sqlite-jdbc` use SLF4J for logging at runtime.

### 3) Compile

```bash
javac Main.java
```

### 4) Run

#### Linux/macOS

```bash
java -cp ".:lib/sqlite-jdbc-3.46.0.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar" Main
```

#### Windows (PowerShell / CMD)

```bat
java -cp ".;lib/sqlite-jdbc-3.46.0.0.jar;lib/slf4j-api-2.0.13.jar;lib/slf4j-simple-2.0.13.jar" Main
```

---

## Project Structure for Plain `javac`

To support `javac Main.java` directly, source packages are available at the repository root (not only under `src/main/java`).

```text
.
├── Main.java                # default-package launcher entry point
├── app/                     # app.Main and startup flow
├── model/                   # domain objects, enums, DTOs
├── repository/              # SQLite access and repositories
├── service/                 # business logic (entry/exit/fine/report)
├── ui/                      # Swing UI (Admin, Entry/Exit, Reports)
├── util/                    # formatting/time/id helpers
└── src/main/java/...        # original Maven-style source tree (kept for reference)
```

### Entry Point

- `Main.java` (root) is the command-line entry point used by:
  - `javac Main.java`
  - `java Main` (with required jars on classpath)
- `Main` delegates startup to `app.Main`.

---

## Features Overview

### 1) Entry workflow

- Register arriving vehicle with:
  - plate number
  - vehicle type (car, motorcycle, SUV/truck, handicapped)
  - handicapped card flag
  - VIP reservation flag
- System finds suitable available spot and assigns ticket.

### 2) Exit workflow

- Find active parking by plate.
- Compute duration and parking fee.
- Apply unpaid fine amounts when applicable.
- Accept payment method and produce receipt data.
- Release spot back to available.

### 3) Fine scheme selection (Admin)

Admin tab allows selecting and saving current fine calculation mode:

- `FIXED`
- `HOURLY`
- `PROGRESSIVE`

### 4) Reporting module

Reports tab provides operational summaries such as:

- occupancy by floor / spot type
- active parking list
- fines summary
- revenue views (historical payments)

### 5) UI overview

Main window uses tabbed Swing interface:

- **Admin**: fine scheme + live operational tables
- **Entry/Exit**: vehicle processing and payment
- **Reports**: analytical views for operations

---

## Example Run Output (Console)

When starting successfully, console output includes:

```text
DB ready
Total spots seeded: 150
```

Then the Swing window opens.

---

## Dependencies (No Maven)

This project uses standard JDK libraries plus these external JARs:

1. **SQLite JDBC driver**
   - `org.xerial:sqlite-jdbc:3.46.0.0`
2. **SLF4J API**
   - `org.slf4j:slf4j-api:2.0.13`
3. **SLF4J Simple binding**
   - `org.slf4j:slf4j-simple:2.0.13`

All are satisfied by manually downloading JARs into `lib/` and supplying `-cp` during `java` execution.

---

## Troubleshooting

### Error: `package ... does not exist` during compile

**Cause:** You are not compiling from project root.

**Fix:**
- `cd` into the repository root (where `Main.java` is located)
- run:
  ```bash
  javac Main.java
  ```

### Error: `SQLite JDBC driver not found. Add sqlite-jdbc JAR to classpath.`

**Cause:** Missing SQLite JDBC JAR at runtime.

**Fix:**
- Ensure `lib/sqlite-jdbc-3.46.0.0.jar` exists
- Add it in `java -cp ...`

### Error: `NoClassDefFoundError: org/slf4j/LoggerFactory`

**Cause:** Missing SLF4J jars required by sqlite-jdbc.

**Fix:**
- Add both:
  - `lib/slf4j-api-2.0.13.jar`
  - `lib/slf4j-simple-2.0.13.jar`
- Include both in runtime classpath.

### Error: `No suitable driver found for jdbc:sqlite:parking.db`

**Cause:** JDBC driver not loaded or not on runtime classpath.

**Fix:**
- Use run command exactly as documented (with all jars).

### Error: `HeadlessException` on Linux server/CI

**Cause:** Swing UI requires desktop environment (X11/GUI).

**Fix options:**
- Run on desktop OS/session with display
- Or use virtual display (e.g., Xvfb) when testing in CI

---

## Notes

- Database file `parking.db` is created automatically in the project root at first run.
- Spot seeding occurs once when the parking spots table is empty.
- You can still keep/use `pom.xml`, but it is not required for compile/run using the commands above.
