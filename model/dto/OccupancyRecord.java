package model.dto;

public class OccupancyRecord {
    private final int totalSpots;
    private final int occupiedSpots;
    private final double occupancyRate;

    public OccupancyRecord(int totalSpots, int occupiedSpots, double occupancyRate) {
        this.totalSpots = totalSpots;
        this.occupiedSpots = occupiedSpots;
        this.occupancyRate = occupancyRate;
    }

    public int getTotalSpots() {
        return totalSpots;
    }

    public int getOccupiedSpots() {
        return occupiedSpots;
    }

    public double getOccupancyRate() {
        return occupancyRate;
    }
}