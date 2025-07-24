package controllers;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private String title;  // এখানে 'title' রাখা হয়েছে (আগে ছিল description)
    private String category;
    private double amount;
    private String type;  // Expense or Income
    private LocalDate date;
    private String note;
    private int userId;

    public Transaction() {}

    public Transaction(int id, String title, String category, double amount, String type, LocalDate date, String note, int userId) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.type = type;
        this.date = date;
        this.note = note;
        this.userId = userId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
