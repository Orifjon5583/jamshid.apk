package com.example.haftalikharajat.data.model;

import java.io.Serializable;

public class Transaction implements Serializable {
    public static final String TYPE_EXPENSE = "EXPENSE";
    public static final String TYPE_INCOME = "INCOME";

    private long id;
    private String type; // EXPENSE or INCOME
    private String title;
    private double amount;
    private String category;
    private String date; // YYYY-MM-DD
    private String time; // HH:mm
    private String accountType; // Karta, Naqd, etc.
    private String note;
    private long timestamp;

    public Transaction() {
    }

    public Transaction(long id, String type, String title, double amount, String category,
                       String date, String time, String accountType, String note, long timestamp) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.time = time;
        this.accountType = accountType;
        this.note = note;
        this.timestamp = timestamp;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isExpense() {
        return TYPE_EXPENSE.equalsIgnoreCase(type);
    }
}
