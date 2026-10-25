package com.example.haftalikharajat.data.model;

import java.io.Serializable;

public class Debt implements Serializable {
    public static final String TYPE_LENT = "LENT"; // Men bergan (Menga berishadi - Receivable)
    public static final String TYPE_BORROWED = "BORROWED"; // Men olgan (Men berishim kerak - Payable)

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_CLOSED = "CLOSED";

    private long id;
    private String personName;
    private double amount;
    private String reason;
    private String phone;
    private String givenDate; // YYYY-MM-DD
    private String dueDate;   // YYYY-MM-DD
    private String type;      // LENT or BORROWED
    private String status;    // ACTIVE or CLOSED
    private String closedDate; // YYYY-MM-DD or null

    public Debt() {
    }

    public Debt(long id, String personName, double amount, String reason, String phone,
                String givenDate, String dueDate, String type, String status, String closedDate) {
        this.id = id;
        this.personName = personName;
        this.amount = amount;
        this.reason = reason;
        this.phone = phone;
        this.givenDate = givenDate;
        this.dueDate = dueDate;
        this.type = type;
        this.status = status;
        this.closedDate = closedDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGivenDate() {
        return givenDate;
    }

    public void setGivenDate(String givenDate) {
        this.givenDate = givenDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(String closedDate) {
        this.closedDate = closedDate;
    }

    public boolean isLent() {
        return TYPE_LENT.equalsIgnoreCase(type);
    }

    public boolean isClosed() {
        return STATUS_CLOSED.equalsIgnoreCase(status);
    }
}
