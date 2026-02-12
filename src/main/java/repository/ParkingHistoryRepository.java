package repository;

import util.TimeUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParkingHistoryRepository {
    private final Database database;

    public ParkingHistoryRepository(Database database) {
        this.database = database;
    }

    public void insertHistoryRow(String plate,
                                 String vehicleType,
                                 boolean hasHandicappedCard,
                                 boolean hasVipReservation,
                                 String spotId,
                                 LocalDateTime entryTime,
                                 LocalDateTime exitTime,
                                 int hours,
                                 double hourlyRate,
                                 double parkingFee,
                                 double fineApplied,
                                 double totalPaid,
                                 String paymentMethod,
                                 String ticketId) {
        String sql = """
                INSERT INTO parking_history(
                    plate, vehicle_type, has_handicapped_card, has_vip_reservation, spot_id,
                    entry_time, exit_time, hours, hourly_rate, parking_fee, fine_applied,
                    total_paid, payment_method, ticket_id
                ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, plate);
            statement.setString(2, vehicleType);
            statement.setInt(3, hasHandicappedCard ? 1 : 0);
            statement.setInt(4, hasVipReservation ? 1 : 0);
            statement.setString(5, spotId);
            statement.setString(6, TimeUtil.toIso(entryTime));
            statement.setString(7, TimeUtil.toIso(exitTime));
            statement.setInt(8, hours);
            statement.setDouble(9, hourlyRate);
            statement.setDouble(10, parkingFee);
            statement.setDouble(11, fineApplied);
            statement.setDouble(12, totalPaid);
            statement.setString(13, paymentMethod);
            statement.setString(14, ticketId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert parking history row.", e);
        }
    }

    public double sumTotalPaid() {
        String sql = "SELECT COALESCE(SUM(total_paid), 0) FROM parking_history";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.getDouble(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to calculate total paid from parking history.", e);
        }
    }

    public List<Map<String, Object>> rawHistoryList() {
        String sql = "SELECT * FROM parking_history ORDER BY id DESC";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                rows.add(row);
            }
            return rows;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch parking history list.", e);
        }
    }
}