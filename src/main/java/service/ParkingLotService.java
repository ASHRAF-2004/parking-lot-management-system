package service;

import model.dto.ParkingSpotRecord;
import model.enums.SpotStatus;
import model.enums.SpotType;
import model.enums.VehicleType;
import repository.Database;
import repository.ParkingSpotRepository;
import service.rules.SpotSuitability;

import java.util.List;

public class ParkingLotService {
    private final ParkingSpotRepository parkingSpotRepository;

    public ParkingLotService(Database database) {
        this.parkingSpotRepository = new ParkingSpotRepository(database);
    }

    public List<ParkingSpotRecord> getAllSpots() {
        return parkingSpotRepository.findAll();
    }

    public ParkingSpotRecord getSpot(String spotId) {
        return parkingSpotRepository.findById(spotId);
    }

    public List<ParkingSpotRecord> findAvailableSpotsFor(VehicleType type) {
        List<SpotType> suitableTypes = SpotSuitability.getSuitableTypes(type);
        return parkingSpotRepository.findAvailableByTypes(suitableTypes);
    }

    public void occupySpot(String spotId, String plate) {
        parkingSpotRepository.updateStatusAndPlate(spotId, SpotStatus.OCCUPIED, plate);
    }

    public void freeSpot(String spotId) {
        parkingSpotRepository.updateStatusAndPlate(spotId, SpotStatus.AVAILABLE, null);
    }
}