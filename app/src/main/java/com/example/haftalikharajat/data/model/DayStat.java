package com.example.haftalikharajat.data.model;

import java.io.Serializable;

public class DayStat implements Serializable {
    private String dayName; // Dush, Sesh, Chor, Pay, Juma, Shan, Yak
    private String date;    // YYYY-MM-DD
    private double amount;
    private boolean isHighlighted;

    public DayStat(String dayName, String date, double amount, boolean isHighlighted) {
        this.dayName = dayName;
        this.date = date;
        this.amount = amount;
        this.isHighlighted = isHighlighted;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }
}
