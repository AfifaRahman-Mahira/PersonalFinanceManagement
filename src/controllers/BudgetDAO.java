package controllers;

import database.DBConnector;

import java.math.BigDecimal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public boolean addOrUpdateBudget(Budget budget) {
        // Check if budget already exists for this user and category
        String selectSql = "SELECT id FROM budget WHERE user_id = ? AND category = ?";
        String insertSql = "INSERT INTO budget (user_id, category, monthly_limit) VALUES (?, ?, ?)";
        String updateSql = "UPDATE budget SET monthly_limit = ? WHERE id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
            selectPs.setInt(1, budget.getUserId());
            selectPs.setString(2, budget.getCategory());
            ResultSet rs = selectPs.executeQuery();
            if (rs.next()) {
                // Update existing
                int id = rs.getInt("id");
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setBigDecimal(1, budget.getMonthlyLimit());
                    updatePs.setInt(2, id);
                    int affected = updatePs.executeUpdate();
                    return affected > 0;
                }
            } else {
                // Insert new
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setInt(1, budget.getUserId());
                    insertPs.setString(2, budget.getCategory());
                    insertPs.setBigDecimal(3, budget.getMonthlyLimit());
                    int affected = insertPs.executeUpdate();
                    return affected > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Budget> getBudgetsByUserId(int userId) {
        List<Budget> budgets = new ArrayList<>();
        String sql = "SELECT * FROM budget WHERE user_id = ?";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Budget budget = new Budget();
                budget.setId(rs.getInt("id"));
                budget.setUserId(rs.getInt("user_id"));
                budget.setCategory(rs.getString("category"));
                budget.setMonthlyLimit(rs.getBigDecimal("monthly_limit"));
                budgets.add(budget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return budgets;
    }
}
