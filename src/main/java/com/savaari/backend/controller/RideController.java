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

        if (ride.vehicleType.equalsIgnoreCase("Bike")) {
            fare = 50;
        } else if (ride.vehicleType.equalsIgnoreCase("Car")) {
            fare = 100;
        }

        ride.rideId = idCounter++;
        ride.fare = fare;

        if (ride.wallet >= fare) {
            ride.wallet = ride.wallet - fare;
            ride.status = "Booked";
        } else {
            ride.status = "Failed - Insufficient Balance";
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
            if (r.rideId == id) {
                r.status = "Cancelled";
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