package com.savaari.backend.repository;

import com.savaari.backend.model.Ride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class RideRepository {

    @Autowired
    private JdbcTemplate jdbc;

    public List<Ride> findPendingRequestsForDriver(Long driverId) {
        return jdbc.query(
            "SELECT * FROM Rides WHERE DriverID = ? AND Status = 'REQUESTED'",
            new Object[]{driverId},
            (rs, rowNum) -> {
                Ride r = new Ride();
                r.setRideId(rs.getLong("RideID"));
                r.setDriverId(rs.getLong("DriverID"));
                r.setStatus(rs.getString("Status"));
                r.setOriginCity(rs.getString("OriginCity"));
                r.setDestinationCity(rs.getString("DestinationCity"));
                return r;
            });
    }

    public Ride findById(Long rideId) {
        return jdbc.queryForObject(
            "SELECT * FROM Rides WHERE RideID = ?",
            new Object[]{rideId},
            (rs, rowNum) -> {
                Ride r = new Ride();
                r.setRideId(rs.getLong("RideID"));
                r.setDriverId(rs.getLong("DriverID"));
                r.setStatus(rs.getString("Status"));
                r.setOriginCity(rs.getString("OriginCity"));
                r.setDestinationCity(rs.getString("DestinationCity"));
                return r;
            });
    }

    public void assignDriverToRide(Long rideId, Long driverId) {
        jdbc.update("UPDATE Rides SET DriverID=?, Status='ASSIGNED' WHERE RideID=?",
                driverId, rideId);
    }

    public Ride insertCarpoolRide(Long driverId, String originCity, String destinationCity,
            String departureTime, int totalSeats, double farePerSeat, String vehicleType) {
        jdbc.update(
            "INSERT INTO Rides (DriverID, RideType, VehicleType, OriginCity, " +
            "DestinationCity, DepartureTime, TotalFare, SeatsTotal, SeatsAvailable, Status) " +
            "VALUES (?, 'Shared', ?, ?, ?, ?, ?, ?, ?, 'Posted')",
            driverId, vehicleType, originCity, destinationCity,
            departureTime, farePerSeat * totalSeats, totalSeats, totalSeats);
        Ride r = new Ride();
        r.setStatus("Posted");
        r.setDriverId(driverId);
        return r;
    }
}