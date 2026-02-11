package model.composite;

import model.enums.SpotStatus;

import java.util.List;

public interface ParkingNode {
    String getId();

    List<ParkingNode> getChildren();

    default int countTotalSpots() {
        List<ParkingNode> children = getChildren();
        if (children.isEmpty()) {
            return this instanceof ParkingSpot ? 1 : 0;
        }

        int total = 0;
        for (ParkingNode child : children) {
            total += child.countTotalSpots();
        }
        return total;
    }

    default int countOccupiedSpots() {
        List<ParkingNode> children = getChildren();
        if (children.isEmpty()) {
            if (this instanceof ParkingSpot spot && spot.getStatus() == SpotStatus.OCCUPIED) {
                return 1;
            }
            return 0;
        }

        int occupied = 0;
        for (ParkingNode child : children) {
            occupied += child.countOccupiedSpots();
        }
        return occupied;
    }

    default double occupancyRate() {
        int total = countTotalSpots();
        if (total == 0) {
            return 0.0;
        }
        return (double) countOccupiedSpots() / total;
    }
}