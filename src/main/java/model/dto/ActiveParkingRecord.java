package model.dto;

import model.enums.VehicleType;

import java.time.LocalDateTime;

public class ActiveParkingRecord {
    private final String plate;
    private final VehicleType vehicleType;
    private final boolean hasHandicappedCard;
    private final boolean hasVipReservation;
    private final String spotId;
    private final LocalDateTime entryTime;
    private final String ticketId;

    public ActiveParkingRecord(String plate, VehicleType vehicleType, boolean hasHandicappedCard, boolean hasVipReservation, String spotId, LocalDateTime entryTime, String ticketId) {
        this.plate = plate;
        this.vehicleType = vehicleType;
        this.hasHandicappedCard = hasHandicappedCard;
        this.hasVipReservation = hasVipReservation;
        this.spotId = spotId;
        this.entryTime = entryTime;
        this.ticketId = ticketId;
    }

    public String getPlate() {
        return plate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public boolean isHasHandicappedCard() {
        return hasHandicappedCard;
    }

    public boolean isHasVipReservation() {
        return hasVipReservation;
    }

    public String getSpotId() {
        return spotId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public String getTicketId() {
        return ticketId;
    }
}