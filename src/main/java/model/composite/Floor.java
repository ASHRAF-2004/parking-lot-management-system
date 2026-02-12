package model.composite;

import java.util.ArrayList;
import java.util.List;

public class Floor implements ParkingNode {
    private int floorNo;
    private List<Row> rows;

    public Floor(int floorNo, List<Row> rows) {
        this.floorNo = floorNo;
        this.rows = rows != null ? rows : new ArrayList<>();
    }

    @Override
    public String getId() {
        return "FLOOR-" + floorNo;
    }

    @Override
    public List<ParkingNode> getChildren() {
        return new ArrayList<>(rows);
    }

    public int getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(int floorNo) {
        this.floorNo = floorNo;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
}