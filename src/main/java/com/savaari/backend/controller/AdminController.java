package com.savaari.backend.controller;

import com.savaari.backend.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminRepository adminRepo;

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return adminRepo.getDashboard();
    }

    @GetMapping("/drivers/pending")
    public List<Map<String, Object>> pendingDrivers() {
        return adminRepo.getPendingDrivers();
    }

    @PutMapping("/drivers/{id}/approve")
    public String approveDriver(@PathVariable int id,
                                @RequestBody Map<String, String> body) {
        return adminRepo.approveDriver(id, body.get("decision"));
    }

    @GetMapping("/fare")
    public List<Map<String, Object>> getFare() {
        return adminRepo.getFareConfig();
    }

    @PutMapping("/fare")
    public String updateFare(@RequestBody Map<String, Object> body) {
        adminRepo.updateFare(
            (String) body.get("vehicleType"),
            ((Number) body.get("baseFare")).doubleValue(),
            ((Number) body.get("perKmRate")).doubleValue()
        );
        return "Fare updated";
    }

    @PostMapping("/wallet/topup")
    public Map<String, Object> topUp(@RequestBody Map<String, Object> body) {
        return adminRepo.topUpWallet(
            (Integer) body.get("userId"),
            ((Number) body.get("amount")).doubleValue()
        );
    }

    @GetMapping("/users")
    public List<Map<String, Object>> users() {
        return adminRepo.getAllUsers();
    }

    @PutMapping("/users/{id}/suspend")
    public String suspendUser(@PathVariable int id,
                              @RequestBody Map<String, String> body) {
        adminRepo.suspendUser(id, body.get("status"));
        return "Done";
    }
}