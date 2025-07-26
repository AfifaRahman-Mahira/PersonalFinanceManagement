package controllers;

public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String gender;

    // Budget fields
    private double foodBudget;
    private double transportBudget;
    private double shoppingBudget;
    private double otherBudget;
    private double totalBudget;

    // Getters and Setters for ID
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    // Getters and Setters for Username
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    // Getters and Setters for Password
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    // Getters and Setters for Full Name
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Getters and Setters for Email
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    // Getters and Setters for Gender
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }

    // Getters and Setters for Food Budget
    public double getFoodBudget() {
        return foodBudget;
    }
    public void setFoodBudget(double foodBudget) {
        this.foodBudget = foodBudget;
    }

    // Getters and Setters for Transport Budget
    public double getTransportBudget() {
        return transportBudget;
    }
    public void setTransportBudget(double transportBudget) {
        this.transportBudget = transportBudget;
    }

    // Getters and Setters for Shopping Budget
    public double getShoppingBudget() {
        return shoppingBudget;
    }
    public void setShoppingBudget(double shoppingBudget) {
        this.shoppingBudget = shoppingBudget;
    }

    // Getters and Setters for Other Budget
    public double getOtherBudget() {
        return otherBudget;
    }
    public void setOtherBudget(double otherBudget) {
        this.otherBudget = otherBudget;
    }

    // Getters and Setters for Total Budget
    public double getTotalBudget() {
        return totalBudget;
    }
    public void setTotalBudget(double totalBudget) {
        this.totalBudget = totalBudget;
    }
}
