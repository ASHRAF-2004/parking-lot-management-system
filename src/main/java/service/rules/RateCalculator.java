package service.rules;

import model.enums.SpotType;
import model.enums.VehicleType;

public final class RateCalculator {
    private RateCalculator() {
    }

    // Malaysian daily cap: Maximum RM20 per day (24 hours)
    public static final double DAILY_CAP = 20.0;

    public static double computeHourlyRate(VehicleType vehicleType,
                                           boolean hasHandicappedCard,
                                           SpotType spotType,
                                           double defaultSpotRate) {
        // OKU (handicapped) cardholders park FREE or at reduced rate
        if (vehicleType == VehicleType.HANDICAPPED && hasHandicappedCard) {
            if (spotType == SpotType.HANDICAPPED) {
                return 0.0;  // FREE at designated OKU spots
            }
            return 1.0;      // RM1/hour at regular spots (50% discount)
        }
        return defaultSpotRate;
    }
    
    // Apply daily cap (Malaysian standard)
    public static double applyDailyCap(double totalFee, int hours) {
        if (hours >= 24) {
            int fullDays = hours / 24;
            int remainingHours = hours % 24;
            // Each full day capped at RM20
            return Math.min(totalFee, (fullDays * DAILY_CAP) + (remainingHours * 3.0));
        }
        return totalFee;
    }
}