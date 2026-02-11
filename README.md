# Parking Lot Management System

A Java Swing desktop application for managing a multi-floor parking lot with SQLite persistence, vehicle entry/exit processing, configurable fine schemes, payment capture, and operational reporting.

This repository is self-contained for offline usage:

- All required third-party JARs are vendored in `lib/`.
- No runtime download is performed.

## 1) Prerequisites (All OS)

- Java **JDK 17+** is required.
- Verify your installation:

```bash
java -version
javac -version
```

## 2) Windows (NO make)

Use the included launcher scripts from the project root:

### A) Command Prompt (CMD)

```bat
run.cmd
```

### B) PowerShell

```powershell
./run.ps1
```

Both scripts compile and run `app.Main`, and they normalize source paths to avoid the `javac @sources.txt` backslash escaping issue on Windows absolute paths.

## 3) Linux/macOS (make allowed)

This repository already includes a `Makefile`. Use:

```bash
make clean
make run
```

`make run` compiles sources and starts `app.Main`.

## 4) Troubleshooting

### `'javac' is not recognized` (Windows)

- Install a **JDK** (not JRE) 17+.
- Add the JDK `bin` folder to your `PATH`.
- Re-open terminal and re-check:
  - `java -version`
  - `javac -version`

### `ClassNotFoundException`

- Ensure you used the correct classpath (`-cp`) separator for your OS:
  - Windows: `;`
  - Linux/macOS: `:`
- Ensure main class is exactly `app.Main`.
- Ensure compile output directory (`out` or `bin`) exists and contains compiled classes.

### Compilation errors due to package/path mismatch

- Keep source files under `src/main/java` matching package declarations (for example, `package app;` in `src/main/java/app/Main.java`).
- Compile all source files together (`@sources.txt`) so interdependent packages resolve correctly.

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
