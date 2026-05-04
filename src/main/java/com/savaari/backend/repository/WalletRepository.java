package com.savaari.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class WalletRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public double getBalance(Long userId) {
        Double result = jdbc.queryForObject(
            "SELECT Balance FROM Wallets WHERE UserID = ?",
            new Object[]{userId}, Double.class);
        return result != null ? result : 0.0;
    }

    public List<Map<String, Object>> getTransactionHistory(Long userId) {
        return jdbc.queryForList(
            "SELECT t.Type, t.Amount, t.Description, t.CreatedAt " +
            "FROM Transactions t JOIN Wallets w ON w.WalletID = t.WalletID " +
            "WHERE w.UserID = ? ORDER BY t.CreatedAt DESC", userId);
    }
}