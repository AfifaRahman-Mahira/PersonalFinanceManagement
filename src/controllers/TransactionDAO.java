package controllers;

import database.DBConnector;

import java.sql.*;
import java.time.LocalDate;

public class TransactionDAO {

    // Add new transaction
    public boolean addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, title, category, amount, type, date, note) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, transaction.getUserId());
            stmt.setString(2, transaction.getTitle());
            stmt.setString(3, transaction.getCategory());
            stmt.setDouble(4, transaction.getAmount());
            stmt.setString(5, transaction.getType());
            stmt.setDate(6, Date.valueOf(transaction.getDate()));
            stmt.setString(7, transaction.getNote());

            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update existing transaction
   
public boolean updateTransaction(Transaction transaction) {
    String query = "UPDATE transactions SET title=?, category=?, amount=?, type=?, date=?, note=? WHERE id=?";

    try (Connection conn = DBConnector.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, transaction.getTitle());
        stmt.setString(2, transaction.getCategory());
        stmt.setDouble(3, transaction.getAmount());
        stmt.setString(4, transaction.getType());
        stmt.setDate(5, Date.valueOf(transaction.getDate()));
        stmt.setString(6, transaction.getNote());
        stmt.setInt(7, transaction.getId());

        int rows = stmt.executeUpdate();
        return rows > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }

}
}