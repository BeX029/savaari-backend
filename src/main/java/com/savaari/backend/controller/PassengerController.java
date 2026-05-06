package com.savaari.backend.controller;

import com.savaari.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/passenger")
public class PassengerController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JdbcTemplate jdbc;

    // Passenger dashboard
    @GetMapping("/home")
    public ResponseEntity<?> home(
            @RequestHeader("Authorization") String header) {
        String token = header.substring(7);
        String role  = jwtUtil.getRole(token);
        Long userId  = jwtUtil.getUserId(token);

        if (!role.equals("PASSENGER")) {
            return ResponseEntity.status(403)
                .body(Map.of("error", "Access denied"));
        }

        Map<String, Object> wallet = jdbc.queryForMap(
            "SELECT w.Balance, u.FullName FROM Wallets w " +
            "JOIN Users u ON u.UserID = w.UserID WHERE w.UserID = ?", userId
        );
        return ResponseEntity.ok(Map.of(
            "message",     "Welcome " + wallet.get("FullName"),
            "walletBalance", wallet.get("Balance")
        ));
    }

    // Find solo drivers
    @GetMapping("/drivers/solo")
    public ResponseEntity<?> findSoloDrivers(
            @RequestParam String vehicleType) {
        List<Map<String, Object>> drivers = jdbc.queryForList(
            "EXEC sp_FindSoloDrivers ?", vehicleType
        );
        return ResponseEntity.ok(drivers);
    }

    // Find shared rides
    @GetMapping("/rides/shared")
    public ResponseEntity<?> findSharedRides(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String vehicleType) {
        List<Map<String, Object>> rides = jdbc.queryForList(
            "EXEC sp_FindSharedRides ?, ?, ?",
            origin, destination, vehicleType
        );
        return ResponseEntity.ok(rides);
    }

    // Book ride
    @PostMapping("/book")
    public ResponseEntity<?> bookRide(
            @RequestHeader("Authorization") String header,
            @RequestBody Map<String, Object> body) {
        Long passengerId = jwtUtil.getUserId(header.substring(7));
        Long rideId      = ((Number) body.get("rideId")).longValue();
        double fareShare = ((Number) body.get("fareShare")).doubleValue();

        Map<String, Object> result = jdbc.queryForMap(
            "EXEC sp_BookRide ?, ?, ?", rideId, passengerId, fareShare
        );
        return ResponseEntity.ok(result);
    }

    // Cancel booking
    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        jdbc.queryForMap("EXEC sp_CancelRide ?", bookingId);
        return ResponseEntity.ok(Map.of("message", "Booking cancelled"));
    }

    // Booking history
    @GetMapping("/bookings")
    public ResponseEntity<?> getBookings(
            @RequestHeader("Authorization") String header) {
        Long passengerId = jwtUtil.getUserId(header.substring(7));
        List<Map<String, Object>> bookings = jdbc.queryForList(
            "SELECT b.BookingID, b.FareShare, b.Status, b.BookedAt, " +
            "r.OriginCity, r.DestinationCity, r.VehicleType " +
            "FROM Bookings b JOIN Rides r ON r.RideID = b.RideID " +
            "WHERE b.PassengerID = ? ORDER BY b.BookedAt DESC",
            passengerId
        );
        return ResponseEntity.ok(bookings);
    }
}