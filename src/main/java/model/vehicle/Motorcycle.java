package model.vehicle;

import model.enums.VehicleType;

import java.time.LocalDateTime;

public class Motorcycle extends Vehicle {
    public Motorcycle(String plate, boolean handicappedCardHolder, LocalDateTime entryTime) {
        super(plate, VehicleType.MOTORCYCLE, handicappedCardHolder, entryTime);
    }
}