package service;

import model.core.Ticket;
import model.dto.ActiveParkingRecord;
import model.dto.ParkingSpotRecord;
import model.enums.SpotStatus;
import model.enums.VehicleType;
import repository.ActiveParkingRepository;
import repository.Database;
import repository.ParkingSpotRepository;
import service.rules.SpotSuitability;
import util.IdUtil;
import util.TimeUtil;

import java.time.LocalDateTime;

public class EntryService {
    private final ActiveParkingRepository activeParkingRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    public EntryService(Database database) {
        this.activeParkingRepository = new ActiveParkingRepository(database);
        this.parkingSpotRepository = new ParkingSpotRepository(database);
    }

    public Ticket registerEntry(String plate,
                                VehicleType type,
                                boolean hasHandicappedCard,
                                boolean hasVipReservation,
                                String selectedSpotId) {
        String normalizedPlate = plate == null ? "" : plate.trim().toUpperCase();
        if (normalizedPlate.isEmpty()) {
            throw new IllegalArgumentException("Plate is required.");
        }
        if (activeParkingRepository.findByPlate(normalizedPlate) != null) {
            throw new IllegalArgumentException("Vehicle is already parked.");
        }

        ParkingSpotRecord selectedSpot = parkingSpotRepository.findById(selectedSpotId);
        if (selectedSpot == null) {
            throw new IllegalArgumentException("Selected spot does not exist.");
        }
        if (selectedSpot.getStatus() != SpotStatus.AVAILABLE) {
            throw new IllegalArgumentException("Selected spot is not available.");
        }

        if (!SpotSuitability.getSuitableTypes(type).contains(selectedSpot.getType())) {
            throw new IllegalArgumentException("Selected spot type is not suitable for this vehicle.");
        }

        LocalDateTime entryTime = TimeUtil.now();
        String ticketId = IdUtil.generateTicketId(normalizedPlate, entryTime);

        activeParkingRepository.insert(new ActiveParkingRecord(
                normalizedPlate,
                type,
                hasHandicappedCard,
                hasVipReservation,
                selectedSpotId,
                entryTime,
                ticketId
        ));
        parkingSpotRepository.updateStatusAndPlate(selectedSpotId, SpotStatus.OCCUPIED, normalizedPlate);

        return new Ticket(ticketId, normalizedPlate, selectedSpotId, entryTime);
    }
}