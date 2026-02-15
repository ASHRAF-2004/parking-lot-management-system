# Parking Lot Management System

A Java Swing desktop application for managing a multi-floor parking lot with SQLite persistence, vehicle entry/exit processing, configurable fine schemes, payment capture, and operational reporting.

This repository is self-contained for offline usage:

- All required third-party JARs are vendored in `lib/`.
- No runtime download is performed.

## Prerequisites (All Operating Systems)

- **Java JDK 17 or higher** is required (not just JRE)
- Verify your installation:

```bash
java -version
javac -version
```

Both commands should return version 17 or higher.

---

## How to Run

### Windows

1. **Open Command Prompt or PowerShell**

2. **Navigate to the project directory:**
   ```cmd
   cd "path\to\parking-lot-management-system-main"
   ```

3. **Run the batch script:**
   ```cmd
   run.bat
   ```
   
   **Or in PowerShell:**
   ```powershell
   .\run.bat
   ```

**What the script does:**
- ✅ Checks if Java and javac are installed
- ✅ Verifies all required JAR files exist
- ✅ Compiles all Java source files to the `out` directory
- ✅ Handles paths with spaces correctly
- ✅ Runs the application with proper classpath

**Note:** The script works perfectly even if your project path contains spaces.

### Linux / macOS

1. **Open Terminal**

2. **Navigate to the project directory:**
   ```bash
   cd /path/to/parking-lot-management-system-main
   ```

3. **Option A - Using Make (Recommended):**
   ```bash
   make clean
   make run
   ```

4. **Option B - Manual compilation and run:**
   ```bash
   # Create output directory
   mkdir -p out
   
   # Compile all Java files
   find src/main/java -name "*.java" > sources.txt
   javac -encoding UTF-8 -d out \
     -cp "out:lib/sqlite-jdbc-3.46.0.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar" \
     @sources.txt
   
   # Run the application
   java -cp "out:lib/sqlite-jdbc-3.46.0.0.jar:lib/slf4j-api-2.0.13.jar:lib/slf4j-simple-2.0.13.jar" \
     app.Main
   
   # Clean up
   rm sources.txt
   ```

**Note:** Linux/macOS use colon (`:`) as the classpath separator, while Windows uses semicolon (`;`).

---

## Troubleshooting

---

## Troubleshooting

### Error: `'javac' is not recognized` (Windows)

**Solution:**
- Install **JDK 17+** (not just JRE) from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
- Add the JDK `bin` folder to your system PATH
- **Restart** your terminal/command prompt
- Verify: `java -version` and `javac -version`

### Error: `java: command not found` (Linux/macOS)

**Solution:**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# macOS (using Homebrew)
brew install openjdk@17

# Verify installation
java -version
javac -version
```

### Error: `ClassNotFoundException` or `NoClassDefFoundError`

**Possible causes:**
- Wrong classpath separator (`;` for Windows, `:` for Linux/macOS)
- Missing JAR files in the `lib/` directory
- Compiled classes not in the `out` directory

**Solution:**
- Re-run the script/make command
- Ensure all JAR files exist in `lib/`: `sqlite-jdbc-3.46.0.0.jar`, `slf4j-api-2.0.13.jar`, `slf4j-simple-2.0.13.jar`

### Warning: `Restricted method ... has been called`

This is a **normal warning** from Java 17+ regarding native library access by SQLite JDBC. The application works correctly despite this warning. You can safely ignore it, or suppress it by adding this to the java command:
```bash
--enable-native-access=ALL-UNNAMED
```

### Message: `Headless environment detected. UI launch skipped.`

This occurs when running in an environment without a graphical display (SSH, Docker, CI/CD). The UI requires a graphical desktop environment to launch.

### The GUI doesn't appear

**Check:**
- Are you running on a system with a graphical desktop?
- On Windows, try running from Command Prompt with admin privileges
- On Linux, ensure `DISPLAY` environment variable is set: `echo $DISPLAY`
- On macOS, ensure you're running from Terminal (not SSH)

---

## Project Structure

```
parking-lot-management-system-main/
├── src/
│   ├── main/java/           # Application source code
│   │   ├── app/            # Main entry point
│   │   ├── model/          # Data models (composite, core, dto, enums, vehicle)
│   │   ├── repository/     # Database access layer
│   │   ├── service/        # Business logic (entry, exit, fines, reports)
│   │   ├── ui/             # Swing GUI components
│   │   └── util/           # Utility classes
│   └── test/java/          # Test cases
├── lib/                     # Third-party dependencies (JARs)
│   ├── sqlite-jdbc-3.46.0.0.jar
│   ├── slf4j-api-2.0.13.jar
│   └── slf4j-simple-2.0.13.jar
├── out/                     # Compiled .class files (generated)
├── Makefile                 # Build script for Linux/macOS
├── run.bat                  # Launch script for Windows
├── pom.xml                  # Maven project descriptor
└── README.md               # This file
```

---

## Features

- **Multi-floor parking management**: 5 floors, 3 rows per floor, 10 spots per row (150 total spots)
- **Vehicle types**: Motorcycle, Car, SUV/Truck, Handicapped
- **Spot types**: Compact, Regular, Handicapped, Reserved (with different hourly rates)
- **Entry/Exit management**: Ticket generation, parking duration calculation
- **Payment processing**: Cash and card
- **Fine schemes**: Fixed, Hourly, Progressive
- **Reports**: Occupancy, revenue, active parking, fines
- **SQLite persistence**: All data stored locally in `parking.db`

---

## First Run

On first launch, the application will:
1. Create `parking.db` in the project root directory
2. Initialize database tables
3. Seed 150 parking spots (if database is empty)
4. Launch the GUI

**Expected console output:**
```
DB ready
Total spots seeded: 150
```

---

## System Requirements

- **OS**: Windows 10/11, Linux (Ubuntu 20.04+), macOS 10.14+
- **Java**: JDK 17 or higher
- **Memory**: 512 MB RAM minimum
- **Display**: Graphical desktop environment required for GUI
- **Disk**: ~50 MB for application and database

---

## Notes

- The `parking.db` SQLite database file is created automatically in the project root
- All dependencies are included in the `lib/` folder - no internet connection needed
- The application uses Java Swing for the GUI
- Spot seeding occurs only once when the database is empty
- The application has been tested on Windows 10/11, Ubuntu 22.04, and macOS

---

## Support

If you encounter issues not covered in troubleshooting:
1. Ensure you have JDK 17+ (not JRE)
2. Verify all JAR files are present in `lib/`
3. Check that you're running from the correct directory
4. On Windows, try running as Administrator
5. Ensure no antivirus is blocking the application

---

## License

This project is provided as-is for educational purposes.
