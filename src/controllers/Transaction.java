package controllers;

public class Transaction {

    private String type;
    private String category;
    private double amount;
    private String date;
    private String note;

    public Transaction(String type, String category, double amount, String date, String note) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.note = note;
    }

    public String getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getNote() { return note; }
}
