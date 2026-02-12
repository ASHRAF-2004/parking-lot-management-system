package service.fines;

public class ProgressiveFineScheme implements FineScheme {
    @Override
    public double compute(int billableHours) {
        // Malaysian progressive fines (more realistic)
        if (billableHours <= 12) {
            return 0.0;           // Within normal parking time
        }
        if (billableHours <= 24) {
            return 30.0;          // First day overstay
        }
        if (billableHours <= 48) {
            return 80.0;          // Second day (clamping equivalent)
        }
        if (billableHours <= 72) {
            return 150.0;         // Third day
        }
        return 250.0;             // Extended overstay (towing equivalent)
    }
}