package com.example.haftalikharajat.data.model;

import java.io.Serializable;

public class ExpenseLimit implements Serializable {
    public static final String TYPE_DAILY = "DAILY";
    public static final String TYPE_WEEKLY = "WEEKLY";
    public static final String TYPE_MONTHLY = "MONTHLY";

    private long id;
    private String type; // DAILY, WEEKLY, MONTHLY
    private double amount;
    private boolean enabled;

    public ExpenseLimit() {
    }

    public ExpenseLimit(long id, String type, double amount, boolean enabled) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.enabled = enabled;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
