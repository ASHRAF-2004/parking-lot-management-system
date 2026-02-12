package model.dto;

import model.enums.FineReason;
import model.enums.FineSchemeType;
import model.enums.FineStatus;

import java.time.LocalDateTime;

public class FineRecord {
    private final int id;
    private final String plate;
    private final FineReason reason;
    private final FineSchemeType schemeAtTime;
    private final double amount;
    private final FineStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime paidAt;

    public FineRecord(int id, String plate, FineReason reason, FineSchemeType schemeAtTime, double amount, FineStatus status, LocalDateTime createdAt, LocalDateTime paidAt) {
        this.id = id;
        this.plate = plate;
        this.reason = reason;
        this.schemeAtTime = schemeAtTime;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
        this.paidAt = paidAt;
    }

    public int getId() {
        return id;
    }

    public String getPlate() {
        return plate;
    }

    public FineReason getReason() {
        return reason;
    }

    public FineSchemeType getSchemeAtTime() {
        return schemeAtTime;
    }

    public double getAmount() {
        return amount;
    }

    public FineStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }
}