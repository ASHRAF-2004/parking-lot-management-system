package service.rules;

import model.enums.SpotType;
import model.enums.VehicleType;

public final class RateCalculator {
    private RateCalculator() {
    }

    public static double computeHourlyRate(VehicleType vehicleType,
                                           boolean hasHandicappedCard,
                                           SpotType spotType,
                                           double defaultSpotRate) {
        if (vehicleType == VehicleType.HANDICAPPED && hasHandicappedCard) {
            if (spotType == SpotType.HANDICAPPED) {
                return 0.0;
            }
            return 2.0;
        }
        return defaultSpotRate;
    }
}