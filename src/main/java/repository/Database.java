package repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String JDBC_URL = "jdbc:sqlite:parking.db";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(JDBC_URL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to SQLite database.", e);
        }
    }

    public void init() {
        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE IF NOT EXISTS parking_spots (
                        spot_id TEXT PRIMARY KEY,
                        floor_no INTEGER,
                        row_no INTEGER,
                        spot_no INTEGER,
                        spot_type TEXT,
                        hourly_rate REAL,
                        status TEXT,
                        current_plate TEXT NULL
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS active_parkings (
                        plate TEXT PRIMARY KEY,
                        vehicle_type TEXT,
                        has_handicapped_card INTEGER,
                        has_vip_reservation INTEGER,
                        spot_id TEXT,
                        entry_time TEXT,
                        ticket_id TEXT
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS parking_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        plate TEXT,
                        vehicle_type TEXT,
                        has_handicapped_card INTEGER,
                        has_vip_reservation INTEGER,
                        spot_id TEXT,
                        entry_time TEXT,
                        exit_time TEXT,
                        hours INTEGER,
                        hourly_rate REAL,
                        parking_fee REAL,
                        fine_applied REAL,
                        total_paid REAL,
                        payment_method TEXT,
                        ticket_id TEXT
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS fines (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        plate TEXT,
                        reason TEXT,
                        scheme_at_time TEXT,
                        amount REAL,
                        status TEXT,
                        created_at TEXT,
                        paid_at TEXT NULL
                    )
                    """);

            statement.execute("""
                    CREATE TABLE IF NOT EXISTS admin_settings (
                        key TEXT PRIMARY KEY,
                        value TEXT
                    )
                    """);

            statement.execute("""
                    INSERT INTO admin_settings(key, value)
                    VALUES ('fine_scheme_current', 'FIXED')
                    ON CONFLICT(key) DO NOTHING
                    """);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema.", e);
        }
    }
}