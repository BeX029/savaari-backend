package com.savaari.backend.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class AdminRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public Map<String, Object> getDashboard() {
        return jdbc.queryForMap("EXEC sp_GetAdminDashboard");
    }

    public List<Map<String, Object>> getPendingDrivers() {
        return jdbc.queryForList(
            "SELECT d.DriverID, u.FullName, u.Phone, d.CNIC, d.LicenseNumber " +
            "FROM Drivers d JOIN Users u ON u.UserID = d.UserID " +
            "WHERE d.ApprovalStatus = 'Pending'"
        );
    }

    public String approveDriver(int driverId, String decision) {
        return jdbc.queryForObject(
            "EXEC sp_ApproveDriver ?, ?",
            String.class, driverId, decision
        );
    }

    public List<Map<String, Object>> getFareConfig() {
        return jdbc.queryForList("SELECT * FROM FareConfig");
    }

    public void updateFare(String vehicleType, double baseFare, double perKmRate) {
        jdbc.update(
            "UPDATE FareConfig SET BaseFare=?, PerKmRate=? WHERE VehicleType=?",
            baseFare, perKmRate, vehicleType
        );
    }

    public Map<String, Object> topUpWallet(int userId, double amount) {
        return jdbc.queryForMap(
            "EXEC sp_TopUpWallet ?, ?", userId, amount
        );
    }

    public List<Map<String, Object>> getAllUsers() {
        return jdbc.queryForList(
            "SELECT UserID, FullName, Phone, Role, Status FROM Users"
        );
    }

    public void suspendUser(int userId, String status) {
        jdbc.update(
            "UPDATE Users SET Status=? WHERE UserID=?", status, userId
        );
    }
}