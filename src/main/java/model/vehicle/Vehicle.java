package model.vehicle;

import model.enums.SpotType;
import model.enums.VehicleType;

import java.time.LocalDateTime;

public abstract class Vehicle {
    private String plate;
    private VehicleType vehicleType;
    private boolean handicappedCardHolder;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    protected Vehicle(String plate, VehicleType vehicleType, boolean handicappedCardHolder, LocalDateTime entryTime) {
        this.plate = plate;
        this.vehicleType = vehicleType;
        this.handicappedCardHolder = handicappedCardHolder;
        this.entryTime = entryTime;
    }

    public boolean canParkIn(SpotType spotType) {
        if (spotType == SpotType.RESERVED) {
            return false;
        }
        if (spotType == SpotType.HANDICAPPED) {
            return handicappedCardHolder || vehicleType == VehicleType.HANDICAPPED;
        }
        return switch (vehicleType) {
            case MOTORCYCLE, CAR -> spotType == SpotType.COMPACT || spotType == SpotType.REGULAR;
            case SUV_TRUCK -> spotType == SpotType.REGULAR;
            case HANDICAPPED -> spotType == SpotType.COMPACT || spotType == SpotType.REGULAR || spotType == SpotType.HANDICAPPED;
        };
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isHandicappedCardHolder() {
        return handicappedCardHolder;
    }

    public void setHandicappedCardHolder(boolean handicappedCardHolder) {
        this.handicappedCardHolder = handicappedCardHolder;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
}