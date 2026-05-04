package com.savaari.backend.controller;

import com.savaari.backend.model.Driver;
import com.savaari.backend.model.Ride;
import com.savaari.backend.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PutMapping("/{driverId}/online")
    public ResponseEntity<?> toggleOnlineStatus(
            @PathVariable Long driverId,
            @RequestBody Map<String, Boolean> body) {
        boolean isOnline = body.getOrDefault("online", false);
        Driver updated = driverService.toggleOnlineStatus(driverId, isOnline);
        return ResponseEntity.ok(Map.of(
                "driverId", updated.getDriverId(),
                "isOnline", updated.isOnline(),
                "message", isOnline ? "You are now online." : "You are now offline."
        ));
    }

    @GetMapping("/{driverId}/requests")
    public ResponseEntity<?> getIncomingRequests(@PathVariable Long driverId) {
        List<Ride> requests = driverService.getIncomingRequests(driverId);
        return ResponseEntity.ok(requests);
    }

    @PostMapping("/{driverId}/requests/{rideId}/accept")
    public ResponseEntity<?> acceptRide(
            @PathVariable Long driverId,
            @PathVariable Long rideId) {
        Ride ride = driverService.acceptRide(driverId, rideId);
        return ResponseEntity.ok(Map.of(
                "rideId", ride.getRideId(),
                "status", ride.getStatus(),
                "message", "Ride accepted."
        ));
    }

    @PostMapping("/{driverId}/requests/{rideId}/reject")
    public ResponseEntity<?> rejectRide(
            @PathVariable Long driverId,
            @PathVariable Long rideId) {
        driverService.rejectRide(driverId, rideId);
        return ResponseEntity.ok(Map.of("message", "Ride rejected."));
    }

    @PostMapping("/{driverId}/carpool")
    public ResponseEntity<?> postCarpoolRoute(
            @PathVariable Long driverId,
            @RequestBody Map<String, Object> body) {
        Ride posted = driverService.postCarpoolRoute(
                driverId,
                (String) body.get("originCity"),
                (String) body.get("destinationCity"),
                (String) body.get("departureTime"),
                (int) body.get("totalSeats"),
                ((Number) body.get("farePerSeat")).doubleValue(),
                (String) body.get("vehicleType"));
        return ResponseEntity.ok(Map.of(
                "status", posted.getStatus(),
                "message", "Carpool posted!"
        ));
    }

    @GetMapping("/{driverId}/earnings")
    public ResponseEntity<?> getEarnings(@PathVariable Long driverId) {
        return ResponseEntity.ok(driverService.getEarningsSummary(driverId));
    }
}