package service.rules;

import model.enums.SpotType;
import model.enums.VehicleType;

import java.util.List;

public final class SpotSuitability {
    private SpotSuitability() {
    }

    public static List<SpotType> getSuitableTypes(VehicleType vehicleType) {
        return switch (vehicleType) {
            case MOTORCYCLE -> List.of(SpotType.COMPACT);
            case CAR -> List.of(SpotType.COMPACT, SpotType.REGULAR);
            case SUV_TRUCK -> List.of(SpotType.REGULAR);
            case HANDICAPPED -> List.of(SpotType.COMPACT, SpotType.REGULAR, SpotType.HANDICAPPED, SpotType.RESERVED);
        };
    }
}