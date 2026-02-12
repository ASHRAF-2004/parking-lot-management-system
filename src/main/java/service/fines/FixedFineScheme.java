package service.fines;

public class FixedFineScheme implements FineScheme {
    @Override
    public double compute(int billableHours) {
        return billableHours > 24 ? 50.0 : 0.0;
    }
}