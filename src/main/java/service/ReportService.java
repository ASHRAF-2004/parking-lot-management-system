package service;

import model.dto.ActiveParkingRecord;
import model.dto.FineRecord;
import model.dto.OccupancyRecord;
import model.dto.ParkingSpotRecord;
import model.enums.SpotStatus;
import repository.ActiveParkingRepository;
import repository.Database;
import repository.FineRepository;
import repository.ParkingHistoryRepository;
import repository.ParkingSpotRepository;

import java.util.List;

public class ReportService {
    private final ActiveParkingRepository activeParkingRepository;
    private final ParkingHistoryRepository parkingHistoryRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final FineRepository fineRepository;

    public ReportService(Database database) {
        this.activeParkingRepository = new ActiveParkingRepository(database);
        this.parkingHistoryRepository = new ParkingHistoryRepository(database);
        this.parkingSpotRepository = new ParkingSpotRepository(database);
        this.fineRepository = new FineRepository(database);
    }

    public List<ActiveParkingRecord> getCurrentVehicles() {
        return activeParkingRepository.findAll();
    }

    public double getTotalRevenue() {
        return parkingHistoryRepository.sumTotalPaid();
    }

    public OccupancyRecord getOccupancy() {
        List<ParkingSpotRecord> spots = parkingSpotRepository.findAll();
        int total = spots.size();
        int occupied = (int) spots.stream().filter(s -> s.getStatus() == SpotStatus.OCCUPIED).count();
        double rate = total == 0 ? 0.0 : (occupied * 100.0 / total);
        return new OccupancyRecord(total, occupied, rate);
    }

    public List<FineRecord> getOutstandingFines() {
        return fineRepository.findAllUnpaid();
    }
}