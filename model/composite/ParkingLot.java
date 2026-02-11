package model.composite;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot implements ParkingNode {
    private String name;
    private List<Floor> floors;

    public ParkingLot(String name, List<Floor> floors) {
        this.name = name;
        this.floors = floors != null ? floors : new ArrayList<>();
    }

    @Override
    public String getId() {
        return name;
    }

    @Override
    public List<ParkingNode> getChildren() {
        return new ArrayList<>(floors);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }
}