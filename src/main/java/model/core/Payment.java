package model.core;

import model.enums.PaymentMethod;

public class Payment {
    private PaymentMethod method;
    private double amountDue;
    private double cashGiven;
    private String maskedCardNo;
    private double change;

    public Payment(PaymentMethod method, double amountDue, double cashGiven, String maskedCardNo, double change) {
        this.method = method;
        this.amountDue = amountDue;
        this.cashGiven = cashGiven;
        this.maskedCardNo = maskedCardNo;
        this.change = change;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public double getCashGiven() {
        return cashGiven;
    }

    public void setCashGiven(double cashGiven) {
        this.cashGiven = cashGiven;
    }

    public String getMaskedCardNo() {
        return maskedCardNo;
    }

    public void setMaskedCardNo(String maskedCardNo) {
        this.maskedCardNo = maskedCardNo;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }
}