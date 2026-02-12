package service.rules;

import java.time.Duration;
import java.time.LocalDateTime;

public final class DurationCalculator {
    private DurationCalculator() {
    }

    // Grace period: First 15 minutes FREE (Malaysian standard)
    private static final int GRACE_PERIOD_MINUTES = 15;

    public static int computeBillableHours(LocalDateTime entryTime, LocalDateTime exitTime) {
        long totalMinutes = Duration.between(entryTime, exitTime).toMinutes();
        
        // Apply grace period
        long billableMinutes = Math.max(0, totalMinutes - GRACE_PERIOD_MINUTES);
        
        // If within grace period, no charge
        if (billableMinutes <= 0) {
            return 0;
        }
        
        // Round up to nearest hour after grace period
        int hours = (int) Math.ceil(billableMinutes / 60.0);
        return Math.max(hours, 1);
    }
}