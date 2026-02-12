package util;

public final class MoneyUtil {
    private MoneyUtil() {
    }

    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}