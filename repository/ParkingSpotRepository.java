package repository;

import model.dto.ParkingSpotRecord;
import model.enums.SpotStatus;
import model.enums.SpotType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class ParkingSpotRepository {
    private final Database database;

    public ParkingSpotRepository(Database database) {
        this.database = database;
    }

    public boolean isEmpty() {
        String sql = "SELECT COUNT(*) FROM parking_spots";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return resultSet.getInt(1) == 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check if parking_spots is empty.", e);
        }
    }

    public void insertMany(List<ParkingSpotRecord> spots) {
        String sql = """
                INSERT INTO parking_spots(spot_id, floor_no, row_no, spot_no, spot_type, hourly_rate, status, current_plate)
                VALUES(?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (ParkingSpotRecord spot : spots) {
                statement.setString(1, spot.getSpotId());
                statement.setInt(2, spot.getFloorNo());
                statement.setInt(3, spot.getRowNo());
                statement.setInt(4, spot.getSpotNo());
                statement.setString(5, spot.getType().name());
                statement.setDouble(6, spot.getRate());
                statement.setString(7, spot.getStatus().name());
                statement.setString(8, spot.getCurrentPlate());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert parking spots.", e);
        }
    }

    public List<ParkingSpotRecord> findAll() {
        String sql = "SELECT * FROM parking_spots ORDER BY floor_no, row_no, spot_no";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            List<ParkingSpotRecord> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch parking spots.", e);
        }
    }

    public List<ParkingSpotRecord> findAvailableByTypes(List<SpotType> types) {
        if (types == null || types.isEmpty()) {
            return List.of();
        }

        StringJoiner placeholders = new StringJoiner(",");
        for (int i = 0; i < types.size(); i++) {
            placeholders.add("?");
        }

        String sql = "SELECT * FROM parking_spots WHERE status = ? AND spot_type IN (" + placeholders + ") ORDER BY floor_no, row_no, spot_no";

        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int paramIndex = 1;
            statement.setString(paramIndex++, SpotStatus.AVAILABLE.name());
            for (SpotType type : types) {
                statement.setString(paramIndex++, type.name());
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                List<ParkingSpotRecord> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(map(resultSet));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find available parking spots by type.", e);
        }
    }

    public ParkingSpotRecord findById(String spotId) {
        String sql = "SELECT * FROM parking_spots WHERE spot_id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, spotId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return map(resultSet);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find parking spot by ID.", e);
        }
    }

    public void updateStatusAndPlate(String spotId, SpotStatus status, String currentPlateOrNull) {
        String sql = "UPDATE parking_spots SET status = ?, current_plate = ? WHERE spot_id = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setString(2, currentPlateOrNull);
            statement.setString(3, spotId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update parking spot status and plate.", e);
        }
    }

    private ParkingSpotRecord map(ResultSet resultSet) throws SQLException {
        return new ParkingSpotRecord(
                resultSet.getString("spot_id"),
                resultSet.getInt("floor_no"),
                resultSet.getInt("row_no"),
                resultSet.getInt("spot_no"),
                SpotType.valueOf(resultSet.getString("spot_type")),
                resultSet.getDouble("hourly_rate"),
                SpotStatus.valueOf(resultSet.getString("status")),
                resultSet.getString("current_plate")
        );
    }
}