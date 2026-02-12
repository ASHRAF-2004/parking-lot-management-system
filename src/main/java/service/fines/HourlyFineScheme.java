package service.fines;

public class HourlyFineScheme implements FineScheme {
    @Override
    public double compute(int billableHours) {
        // Malaysian standard: RM5 per hour overstay beyond 12 hours
        return billableHours > 12 ? 5.0 * (billableHours - 12) : 0.0;
    }
}