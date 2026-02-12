package model.dto;

import model.enums.SpotStatus;
import model.enums.SpotType;

public class ParkingSpotRecord {
    private final String spotId;
    private final int floorNo;
    private final int rowNo;
    private final int spotNo;
    private final SpotType type;
    private final double rate;
    private final SpotStatus status;
    private final String currentPlate;

    public ParkingSpotRecord(String spotId, int floorNo, int rowNo, int spotNo, SpotType type, double rate, SpotStatus status, String currentPlate) {
        this.spotId = spotId;
        this.floorNo = floorNo;
        this.rowNo = rowNo;
        this.spotNo = spotNo;
        this.type = type;
        this.rate = rate;
        this.status = status;
        this.currentPlate = currentPlate;
    }

    public String getSpotId() {
        return spotId;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public int getRowNo() {
        return rowNo;
    }

    public int getSpotNo() {
        return spotNo;
    }

    public SpotType getType() {
        return type;
    }

    public double getRate() {
        return rate;
    }

    public SpotStatus getStatus() {
        return status;
    }

    public String getCurrentPlate() {
        return currentPlate;
    }
}