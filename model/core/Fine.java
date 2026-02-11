package model.core;

import model.enums.FineReason;
import model.enums.FineSchemeType;
import model.enums.FineStatus;

import java.time.LocalDateTime;

public class Fine {
    private int id;
    private String plate;
    private FineReason reason;
    private FineSchemeType schemeAtTime;
    private double amount;
    private FineStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public Fine(int id, String plate, FineReason reason, FineSchemeType schemeAtTime, double amount, FineStatus status, LocalDateTime createdAt, LocalDateTime paidAt) {
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

    public void setId(int id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public FineReason getReason() {
        return reason;
    }

    public void setReason(FineReason reason) {
        this.reason = reason;
    }

    public FineSchemeType getSchemeAtTime() {
        return schemeAtTime;
    }

    public void setSchemeAtTime(FineSchemeType schemeAtTime) {
        this.schemeAtTime = schemeAtTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public FineStatus getStatus() {
        return status;
    }

    public void setStatus(FineStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }
}