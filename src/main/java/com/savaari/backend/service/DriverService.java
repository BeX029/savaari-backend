package com.savaari.backend.service;

import com.savaari.backend.model.Driver;
import com.savaari.backend.model.Ride;
import com.savaari.backend.repository.DriverRepository;
import com.savaari.backend.repository.RideRepository;
import com.savaari.backend.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DriverService {

    @Autowired private DriverRepository driverRepository;
    @Autowired private RideRepository rideRepository;
    @Autowired private WalletRepository walletRepository;

    public Driver toggleOnlineStatus(Long driverId, boolean isOnline) {
        Driver driver = driverRepository.findByUserId(driverId);
        if (driver == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found.");
        if (isOnline && !driver.getApprovalStatus().equals("APPROVED"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not approved yet.");
        driverRepository.setOnlineStatus(driverId, isOnline);
        driver.setOnline(isOnline);
        return driver;
    }

    public List<Ride> getIncomingRequests(Long driverId) {
        return rideRepository.findPendingRequestsForDriver(driverId);
    }

    public Ride acceptRide(Long driverId, Long rideId) {
        Ride ride = rideRepository.findById(rideId);
        if (ride == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found.");
        if (!ride.getStatus().equals("REQUESTED"))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ride no longer available.");
        Driver driver = driverRepository.findByUserId(driverId);
        if (!driver.isOnline())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Go online first.");
        rideRepository.assignDriverToRide(rideId, driverId);
        ride.setStatus("ASSIGNED");
        ride.setDriverId(driverId);
        return ride;
    }

    public void rejectRide(Long driverId, Long rideId) {
        Ride ride = rideRepository.findById(rideId);
        if (ride == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found.");
    }

    public Ride postCarpoolRoute(Long driverId, String originCity, String destinationCity,
            String departureTime, int totalSeats, double farePerSeat, String vehicleType) {
        Driver driver = driverRepository.findByUserId(driverId);
        if (driver == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found.");
        if (!driver.getApprovalStatus().equals("APPROVED"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not approved.");
        // Updated vehicle types — Rickshaw and CarEconomy support sharing
        if (!vehicleType.equalsIgnoreCase("Rickshaw") &&
            !vehicleType.equalsIgnoreCase("CarEconomy") &&
            !vehicleType.equalsIgnoreCase("Van"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only Rickshaw, CarEconomy and Van support shared rides.");
        return rideRepository.insertCarpoolRide(driverId, originCity, destinationCity,
                departureTime, totalSeats, farePerSeat, vehicleType);
    }

    public Map<String, Object> getEarningsSummary(Long driverId) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalEarned",        driverRepository.getTotalEarnings(driverId));
        summary.put("ridesCompleted",     driverRepository.getCompletedRideCount(driverId));
        summary.put("walletBalance",      walletRepository.getBalance(driverId));
        summary.put("transactionHistory", walletRepository.getTransactionHistory(driverId));
        return summary;
    }
}