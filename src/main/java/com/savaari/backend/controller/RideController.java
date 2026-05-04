package com.savaari.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.savaari.backend.model.Ride;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    @Autowired
    private JdbcTemplate jdbc;

    List<Ride> rides = new ArrayList<>();
    int idCounter = 1;

    @PostMapping("/solo")
    public Ride bookRide(@RequestBody Ride ride) {
        int fare = 0;
        if (ride.getVehicleType().equalsIgnoreCase("Bike")) {
            fare = 50;
        } else if (ride.getVehicleType().equalsIgnoreCase("Car")) {
            fare = 100;
        }
        ride.setRideId((long) idCounter++);
        ride.setTotalFare(fare);
        if (ride.getWallet() >= fare) {
            ride.setWallet(ride.getWallet() - fare);
            ride.setStatus("Booked");
        } else {
            ride.setStatus("Failed - Insufficient Balance");
        }
        rides.add(ride);
        return ride;
    }

    @GetMapping("/all")
    public List<Ride> getAllRides() {
        return rides;
    }

    @PostMapping("/cancel/{id}")
    public String cancelRide(@PathVariable int id) {
        for (Ride r : rides) {
            if (r.getRideId() == id) {
                r.setStatus("Cancelled");
                return "Ride " + id + " cancelled";
            }
        }
        return "Ride not found";
    }

    @GetMapping("/test")
    public String test() {
        return "RideController working!";
    }

    @GetMapping("/fares")
    public List<Map<String, Object>> getFares() {
        return jdbc.queryForList("SELECT * FROM FareConfig");
    }
}