package com.savaari.backend.controller;

import com.savaari.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbc;

    // Get wallet balance
    @GetMapping("/balance")
    public ResponseEntity<?> getBalance(
            @RequestHeader("Authorization") String header) {
        Long userId = jwtUtil.getUserId(header.substring(7));
        Map<String, Object> wallet = jdbc.queryForMap(
            "SELECT Balance FROM Wallets WHERE UserID = ?", userId
        );
        return ResponseEntity.ok(wallet);
    }

    // Transaction history
    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(
            @RequestHeader("Authorization") String header) {
        Long userId = jwtUtil.getUserId(header.substring(7));
        List<Map<String, Object>> txns = jdbc.queryForList(
            "SELECT t.Type, t.Amount, t.Description, t.CreatedAt " +
            "FROM Transactions t JOIN Wallets w ON w.WalletID = t.WalletID " +
            "WHERE w.UserID = ? ORDER BY t.CreatedAt DESC", userId
        );
        return ResponseEntity.ok(txns);
    }
}