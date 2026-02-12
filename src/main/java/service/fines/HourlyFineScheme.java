package service.fines;

public class HourlyFineScheme implements FineScheme {
    @Override
    public double compute(int billableHours) {
        return billableHours > 24 ? 20.0 * (billableHours - 24) : 0.0;
    }
}