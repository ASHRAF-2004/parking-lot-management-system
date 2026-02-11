package model.dto;

public class RevenueRecord {
    private final double totalFees;
    private final double totalFines;
    private final double grandTotal;

    public RevenueRecord(double totalFees, double totalFines, double grandTotal) {
        this.totalFees = totalFees;
        this.totalFines = totalFines;
        this.grandTotal = grandTotal;
    }

    public double getTotalFees() {
        return totalFees;
    }

    public double getTotalFines() {
        return totalFines;
    }

    public double getGrandTotal() {
        return grandTotal;
    }
}