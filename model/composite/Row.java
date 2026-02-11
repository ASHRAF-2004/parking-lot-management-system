package model.composite;

import java.util.ArrayList;
import java.util.List;

public class Row implements ParkingNode {
    private int rowNo;
    private List<ParkingSpot> spots;

    public Row(int rowNo, List<ParkingSpot> spots) {
        this.rowNo = rowNo;
        this.spots = spots != null ? spots : new ArrayList<>();
    }

    @Override
    public String getId() {
        return "ROW-" + rowNo;
    }

    @Override
    public List<ParkingNode> getChildren() {
        return new ArrayList<>(spots);
    }

    public int getRowNo() {
        return rowNo;
    }

    public void setRowNo(int rowNo) {
        this.rowNo = rowNo;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }

    public void setSpots(List<ParkingSpot> spots) {
        this.spots = spots;
    }
}