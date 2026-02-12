package service.fines;

public class FixedFineScheme implements FineScheme {
    @Override
    public double compute(int billableHours) {
        // Malaysian standard: Overstay beyond 12 hours = RM30 fixed fine
        return billableHours > 12 ? 30.0 : 0.0;
    }
}