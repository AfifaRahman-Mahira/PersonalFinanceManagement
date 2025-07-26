package controllers;

import database.DBConnector;
import java.sql.*;

public class UserDAO {

    // User Registration
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, full_name, email, gender) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());  
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getGender());

            int affected = ps.executeUpdate();
            return affected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // User Login + Budget Load
    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setGender(rs.getString("gender"));

                // ✅ Load Budget Info
                loadUserBudget(user);

                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Budget Load Method
    private void loadUserBudget(User user) {
        String sql = "SELECT * FROM budget WHERE user_id = ?";
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();

            double total = 0;
            while (rs.next()) {
                String category = rs.getString("category");
                double amount = rs.getDouble("monthly_limit");

                switch (category.toLowerCase()) {
                    case "food":
                        user.setFoodBudget(amount);
                        break;
                    case "transport":
                        user.setTransportBudget(amount);
                        break;
                    case "shopping":
                        user.setShoppingBudget(amount);
                        break;
                    case "other":
                        user.setOtherBudget(amount);
                        break;
                }

                total += amount;
            }

            user.setTotalBudget(total);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Username check
    public boolean checkUsernameExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;  // assume exists on error
        }
    }

    // Email check
    public boolean checkEmailExists(String email) {
        String sql = "SELECT id FROM users WHERE email = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
}
