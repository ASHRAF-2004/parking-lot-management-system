package service.rules;

import java.time.Duration;
import java.time.LocalDateTime;

public final class DurationCalculator {
    private DurationCalculator() {
    }

    public static int computeBillableHours(LocalDateTime entryTime, LocalDateTime exitTime) {
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        int hours = (int) Math.ceil(minutes / 60.0);
        return Math.max(hours, 1);
    }
}