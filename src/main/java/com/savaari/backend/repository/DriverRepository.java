package com.savaari.backend.repository;

import com.savaari.backend.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DriverRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public Driver findByUserId(Long userId) {
        return jdbc.queryForObject(
            "EXEC sp_GetDriverByUserId ?",
            new Object[]{userId},
            (rs, rowNum) -> {
                Driver d = new Driver();
                d.setDriverId(rs.getLong("DriverID"));
                d.setUserId(rs.getLong("UserID"));
                d.setCnic(rs.getString("CNIC"));
                d.setLicenseNumber(rs.getString("LicenseNumber"));
                d.setApprovalStatus(rs.getString("ApprovalStatus"));
                d.setOnline(rs.getBoolean("IsOnline"));
                d.setAverageRating(rs.getDouble("AverageRating"));
                d.setWalletBalance(rs.getDouble("WalletBalance"));
                return d;
            });
    }

    public void setOnlineStatus(Long driverId, boolean isOnline) {
        jdbc.update("EXEC sp_SetDriverOnlineStatus ?, ?", driverId, isOnline ? 1 : 0);
    }

    public void updateApprovalStatus(Long driverId, String decision, String reason) {
        jdbc.update("EXEC sp_ApproveDriver ?, ?, ?", driverId, decision, reason);
    }

    public double getTotalEarnings(Long driverId) {
        Double result = jdbc.queryForObject(
            "EXEC sp_GetDriverTotalEarnings ?", new Object[]{driverId}, Double.class);
        return result != null ? result : 0.0;
    }

    public int getCompletedRideCount(Long driverId) {
        Integer result = jdbc.queryForObject(
            "EXEC sp_GetDriverRideCount ?", new Object[]{driverId}, Integer.class);
        return result != null ? result : 0;
    }
}