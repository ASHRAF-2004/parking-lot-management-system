package model.vehicle;

import model.enums.VehicleType;

import java.time.LocalDateTime;

public class Car extends Vehicle {
    public Car(String plate, boolean handicappedCardHolder, LocalDateTime entryTime) {
        super(plate, VehicleType.CAR, handicappedCardHolder, entryTime);
    }
}