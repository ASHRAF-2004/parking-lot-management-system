package service.fines;

public interface FineScheme {
    double compute(int billableHours);
}