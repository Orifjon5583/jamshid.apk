package com.example.haftalikharajat.data.model;

import java.io.Serializable;

public class CategoryStat implements Serializable {
    private String category;
    private double amount;
    private int percentage;

    public CategoryStat(String category, double amount, int percentage) {
        this.category = category;
        this.amount = amount;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
