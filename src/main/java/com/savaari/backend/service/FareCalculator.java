package com.savaari.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FareCalculator {

    @Autowired
    private JdbcTemplate jdbc;

    public double calculate(String vehicleType, double distanceKm) {
        Map<String, Object> config = jdbc.queryForMap(
            "SELECT BaseFare, PerKmRate FROM FareConfig WHERE VehicleType=?",
            vehicleType
        );
        double base  = ((Number) config.get("BaseFare")).doubleValue();
        double perKm = ((Number) config.get("PerKmRate")).doubleValue();
        return base + (distanceKm * perKm);
    }

    public double sharedFare(String vehicleType, double distanceKm, int passengers) {
        return calculate(vehicleType, distanceKm) / passengers;
    }

    public double driverEarning(double totalFare) {
        return totalFare * 0.85;
    }
}