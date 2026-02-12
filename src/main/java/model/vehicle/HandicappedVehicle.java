package model.vehicle;

import model.enums.VehicleType;

import java.time.LocalDateTime;

public class HandicappedVehicle extends Vehicle {
    public HandicappedVehicle(String plate, LocalDateTime entryTime) {
        super(plate, VehicleType.HANDICAPPED, true, entryTime);
    }
}