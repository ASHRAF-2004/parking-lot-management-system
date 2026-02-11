package service.fines;

public class ProgressiveFineScheme implements FineScheme {
    @Override
    public double compute(int billableHours) {
        if (billableHours <= 24) {
            return 0.0;
        }
        if (billableHours <= 48) {
            return 50.0;
        }
        if (billableHours <= 72) {
            return 150.0;
        }
        if (billableHours <= 96) {
            return 300.0;
        }
        return 500.0;
    }
}