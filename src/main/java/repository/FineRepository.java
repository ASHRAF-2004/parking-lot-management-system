package repository;

import model.dto.FineRecord;
import model.enums.FineReason;
import model.enums.FineSchemeType;
import model.enums.FineStatus;
import util.TimeUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FineRepository {
    private final Database database;

    public FineRepository(Database database) {
        this.database = database;
    }

    public void insertFine(FineRecord fine) {
        String sql = """
                INSERT INTO fines(plate, reason, scheme_at_time, amount, status, created_at, paid_at)
                VALUES(?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fine.getPlate());
            statement.setString(2, fine.getReason().name());
            statement.setString(3, fine.getSchemeAtTime().name());
            statement.setDouble(4, fine.getAmount());
            statement.setString(5, fine.getStatus().name());
            statement.setString(6, TimeUtil.toIso(fine.getCreatedAt()));
            statement.setString(7, TimeUtil.toIso(fine.getPaidAt()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert fine.", e);
        }
    }

    public List<FineRecord> findUnpaidByPlate(String plate) {
        String sql = "SELECT * FROM fines WHERE plate = ? AND status = ? ORDER BY created_at";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, plate);
            statement.setString(2, FineStatus.UNPAID.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<FineRecord> fines = new ArrayList<>();
                while (resultSet.next()) {
                    fines.add(map(resultSet));
                }
                return fines;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch unpaid fines for plate.", e);
        }
    }

    public double sumUnpaidAmountByPlate(String plate) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM fines WHERE plate = ? AND status = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, plate);
            statement.setString(2, FineStatus.UNPAID.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to sum unpaid fine amount by plate.", e);
        }
    }

    

    public List<FineRecord> findAllUnpaid() {
        String sql = "SELECT * FROM fines WHERE status = ? ORDER BY created_at";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, FineStatus.UNPAID.name());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<FineRecord> fines = new ArrayList<>();
                while (resultSet.next()) {
                    fines.add(map(resultSet));
                }
                return fines;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch all unpaid fines.", e);
        }
    }

    public void markAllPaidForPlate(String plate, String paidAtIso) {
        String sql = "UPDATE fines SET status = ?, paid_at = ? WHERE plate = ? AND status = ?";
        try (Connection connection = database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, FineStatus.PAID.name());
            statement.setString(2, paidAtIso);
            statement.setString(3, plate);
            statement.setString(4, FineStatus.UNPAID.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to mark fines as paid for plate.", e);
        }
    }

    private FineRecord map(ResultSet resultSet) throws SQLException {
        String paidAt = resultSet.getString("paid_at");
        return new FineRecord(
                resultSet.getInt("id"),
                resultSet.getString("plate"),
                FineReason.valueOf(resultSet.getString("reason")),
                FineSchemeType.valueOf(resultSet.getString("scheme_at_time")),
                resultSet.getDouble("amount"),
                FineStatus.valueOf(resultSet.getString("status")),
                TimeUtil.fromIso(resultSet.getString("created_at")),
                paidAt == null ? null : TimeUtil.fromIso(paidAt)
        );
    }
}
