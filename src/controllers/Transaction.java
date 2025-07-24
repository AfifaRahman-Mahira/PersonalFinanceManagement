package controllers;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private int userId;
    private String title;
    private String category;
    private double amount;
    private String type;  // Income or Expense
    private LocalDate date;
    private String note;

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

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

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
}
