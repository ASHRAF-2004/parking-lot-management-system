package service.rules;

import java.time.Duration;
import java.time.LocalDateTime;

public final class DurationCalculator {
    private DurationCalculator() {
    }

    public static int computeBillableHours(LocalDateTime entryTime, LocalDateTime exitTime) {
        long totalMinutes = Duration.between(entryTime, exitTime).toMinutes();
        if (totalMinutes <= 0) {
            return 0;
        }

        // Ceiling rounding to the nearest hour
        return (int) Math.ceil(totalMinutes / 60.0);
    }
}