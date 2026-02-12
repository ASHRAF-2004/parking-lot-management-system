package model.dto;

import model.enums.SpotType;

public class OccupancyByTypeRecord {
    private final SpotType type;
    private final int total;
    private final int occupied;

    public OccupancyByTypeRecord(SpotType type, int total, int occupied) {
        this.type = type;
        this.total = total;
        this.occupied = occupied;
    }

    public SpotType getType() {
        return type;
    }

    public int getTotal() {
        return total;
    }

    public int getOccupied() {
        return occupied;
    }
}