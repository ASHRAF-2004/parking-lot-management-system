package model.vehicle;

import model.enums.VehicleType;

import java.time.LocalDateTime;

public class SuvTruck extends Vehicle {
    public SuvTruck(String plate, boolean handicappedCardHolder, LocalDateTime entryTime) {
        super(plate, VehicleType.SUV_TRUCK, handicappedCardHolder, entryTime);
    }
}