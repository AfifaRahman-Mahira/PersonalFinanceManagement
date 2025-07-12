package controllers;

import javafx.beans.property.*;

public class Transaction {
    private final StringProperty description;
    private final StringProperty category;
    private final DoubleProperty amount;

    public Transaction(String description, String category, double amount) {
        this.description = new SimpleStringProperty(description);
        this.category = new SimpleStringProperty(category);
        this.amount = new SimpleDoubleProperty(amount);
    }

    public String getDescription() { return description.get(); }
    public void setDescription(String desc) { description.set(desc); }
    public StringProperty descriptionProperty() { return description; }

    public String getCategory() { return category.get(); }
    public void setCategory(String cat) { category.set(cat); }
    public StringProperty categoryProperty() { return category; }

    public double getAmount() { return amount.get(); }
    public void setAmount(double amt) { amount.set(amt); }
    public DoubleProperty amountProperty() { return amount; }
}
