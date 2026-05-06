package com.savaari.backend.controller;

import com.savaari.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private JwtUtil jwtUtil;

    // Login — connected to real database
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");

        try {
            Map<String, Object> user = jdbc.queryForMap(
                "EXEC sp_LoginUser ?", phone
            );

            if (user == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid credentials"));
            }

            String role   = (String) user.get("Role");
            Long   userId = ((Number) user.get("UserID")).longValue();
            String name   = (String) user.get("FullName");

            // Token format: phone|role|userId
            String token = jwtUtil.generateToken(phone + "|" + role + "|" + userId);

            return ResponseEntity.ok(Map.of(
                "token",    token,
                "role",     role,
                "userId",   userId,
                "fullName", name,
                "message",  "Login successful"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401)
                .body(Map.of("error", "Invalid phone or password"));
        }
    }

    // Register passenger
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            Map<String, Object> result = jdbc.queryForMap(
                "EXEC sp_RegisterUser ?, ?, ?, ?, ?",
                body.get("fullName"),
                body.get("phone"),
                body.get("email"),
                "HASH_" + body.get("password"),
                "PASSENGER"
            );
            return ResponseEntity.ok(Map.of(
                "message", "Registered successfully!",
                "userId",  result.get("UserID")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", "Registration failed: " + e.getMessage()));
        }
    }
}