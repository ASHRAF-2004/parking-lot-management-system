package util;

import java.time.LocalDateTime;

public final class TimeUtil {
    private TimeUtil() {
    }

    public static String toIso(LocalDateTime dt) {
        return dt == null ? null : dt.toString();
    }

    public static LocalDateTime fromIso(String iso) {
        return iso == null ? null : LocalDateTime.parse(iso);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
}