package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class FormatUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private FormatUtil() {
    }

    public static String formatMoney(double value) {
        return String.format("RM %.2f", value);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}