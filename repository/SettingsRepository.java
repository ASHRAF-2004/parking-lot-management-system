package repository;

import model.enums.FineSchemeType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SettingsRepository {
    private final Database database;

    public SettingsRepository(Database database) {
        this.database = database;
    }

    public FineSchemeType getFineScheme() {
        String sql = "SELECT value FROM admin_settings WHERE key = 'fine_scheme_current'";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                return FineSchemeType.valueOf(resultSet.getString("value"));
            }
            return FineSchemeType.FIXED;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read current fine scheme setting.", e);
        }
    }

    public void setFineScheme(FineSchemeType type) {
        String sql = """
                INSERT INTO admin_settings(key, value)
                VALUES('fine_scheme_current', ?)
                ON CONFLICT(key) DO UPDATE SET value = excluded.value
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update fine scheme setting.", e);
        }
    }
}