package model.composite;

import model.enums.SpotStatus;
import model.enums.SpotType;

import java.util.Collections;
import java.util.List;

public class ParkingSpot implements ParkingNode {
    private String spotId;
    private int floorNo;
    private int rowNo;
    private int spotNo;
    private SpotType type;
    private SpotStatus status;
    private double hourlyRate;
    private String currentPlate;

    public ParkingSpot(String spotId, int floorNo, int rowNo, int spotNo, SpotType type, SpotStatus status, double hourlyRate, String currentPlate) {
        this.spotId = spotId;
        this.floorNo = floorNo;
        this.rowNo = rowNo;
        this.spotNo = spotNo;
        this.type = type;
        this.status = status;
        this.hourlyRate = hourlyRate;
        this.currentPlate = currentPlate;
    }

    @Override
    public String getId() {
        return spotId;
    }

    @Override
    public List<ParkingNode> getChildren() {
        return Collections.emptyList();
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public int getRowNo() {
        return rowNo;
    }

    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    public int getSpotNo() {
        return spotNo;
    }

    public void setSpotNo(int spotNo) {
        this.spotNo = spotNo;
    }

    public SpotType getType() {
        return type;
    }

    public void setType(SpotType type) {
        this.type = type;
    }

    public SpotStatus getStatus() {
        return status;
    }

    public void setStatus(SpotStatus status) {
        this.status = status;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getCurrentPlate() {
        return currentPlate;
    }

    public void setCurrentPlate(String currentPlate) {
        this.currentPlate = currentPlate;
    }
}