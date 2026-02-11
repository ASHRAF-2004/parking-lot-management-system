package repository;

import model.dto.ActiveParkingRecord;
import model.enums.VehicleType;
import util.TimeUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActiveParkingRepository {
    private final Database database;

    public ActiveParkingRepository(Database database) {
        this.database = database;
    }

    public ActiveParkingRecord findByPlate(String plate) {
        String sql = "SELECT * FROM active_parkings WHERE plate = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, plate);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return map(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find active parking by plate.", e);
        }
    }

    public void insert(ActiveParkingRecord record) {
        String sql = """
                INSERT INTO active_parkings(plate, vehicle_type, has_handicapped_card, has_vip_reservation, spot_id, entry_time, ticket_id)
                VALUES(?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, record.getPlate());
            statement.setString(2, record.getVehicleType().name());
            statement.setInt(3, record.isHasHandicappedCard() ? 1 : 0);
            statement.setInt(4, record.isHasVipReservation() ? 1 : 0);
            statement.setString(5, record.getSpotId());
            statement.setString(6, TimeUtil.toIso(record.getEntryTime()));
            statement.setString(7, record.getTicketId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert active parking record.", e);
        }
    }

    public void deleteByPlate(String plate) {
        String sql = "DELETE FROM active_parkings WHERE plate = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, plate);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete active parking record.", e);
        }
    }

    public List<ActiveParkingRecord> findAll() {
        String sql = "SELECT * FROM active_parkings ORDER BY entry_time";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<ActiveParkingRecord> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch active parking records.", e);
        }
    }

    private ActiveParkingRecord map(ResultSet resultSet) throws SQLException {
        return new ActiveParkingRecord(
                resultSet.getString("plate"),
                VehicleType.valueOf(resultSet.getString("vehicle_type")),
                resultSet.getInt("has_handicapped_card") == 1,
                resultSet.getInt("has_vip_reservation") == 1,
                resultSet.getString("spot_id"),
                TimeUtil.fromIso(resultSet.getString("entry_time")),
                resultSet.getString("ticket_id")
        );
    }
}