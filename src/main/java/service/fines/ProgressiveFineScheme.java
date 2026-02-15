package service.fines;

public class ProgressiveFineScheme implements FineScheme {
    @Override
    public double compute(int billableHours) {
        if (billableHours <= 24) {
            return 0.0;
        }

        double fine = 50.0;
        if (billableHours > 24) {
            fine += 100.0;
        }
        if (billableHours > 48) {
            fine += 150.0;
        }
        if (billableHours > 72) {
            fine += 200.0;
        }
        return fine;
    }
}