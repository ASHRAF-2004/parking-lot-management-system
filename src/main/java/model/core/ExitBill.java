package model.core;

import java.time.LocalDateTime;

public class ExitBill {
    private String plate;
    private String spotId;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private int hours;
    private double hourlyRate;
    private double parkingFee;
    private double unpaidFinesBefore;
    private double newFineThisExit;
    private double totalDue;

    public ExitBill(String plate, String spotId, LocalDateTime entryTime, LocalDateTime exitTime, int hours, double hourlyRate, double parkingFee, double unpaidFinesBefore, double newFineThisExit, double totalDue) {
        this.plate = plate;
        this.spotId = spotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.hours = hours;
        this.hourlyRate = hourlyRate;
        this.parkingFee = parkingFee;
        this.unpaidFinesBefore = unpaidFinesBefore;
        this.newFineThisExit = newFineThisExit;
        this.totalDue = totalDue;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
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

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getParkingFee() {
        return parkingFee;
    }

    public void setParkingFee(double parkingFee) {
        this.parkingFee = parkingFee;
    }

    public double getUnpaidFinesBefore() {
        return unpaidFinesBefore;
    }

    public void setUnpaidFinesBefore(double unpaidFinesBefore) {
        this.unpaidFinesBefore = unpaidFinesBefore;
    }

    public double getNewFineThisExit() {
        return newFineThisExit;
    }

    public void setNewFineThisExit(double newFineThisExit) {
        this.newFineThisExit = newFineThisExit;
    }

    public double getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(double totalDue) {
        this.totalDue = totalDue;
    }
}