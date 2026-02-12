package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class IdUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private IdUtil() {
    }

    public static String generateTicketId(String plate, LocalDateTime entryTime) {
        return "T-" + plate + "-" + entryTime.format(FORMATTER);
    }
}