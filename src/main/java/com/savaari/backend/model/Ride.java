package com.savaari.backend.model;

public class Ride {
    private Long rideId;
    private Long driverId;
    private String status;
    private String originCity;
    private String destinationCity;
    private String vehicleType;
    private String rideType;
    private int seatsTotal;
    private int seatsAvailable;
    private double totalFare;

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }
    public Long getDriverId() { return driverId; }
    public void setDriverId(Long driverId) { this.driverId = driverId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }
    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }
    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }
    public String getRideType() { return rideType; }
    public void setRideType(String rideType) { this.rideType = rideType; }
    public int getSeatsTotal() { return seatsTotal; }
    public void setSeatsTotal(int seatsTotal) { this.seatsTotal = seatsTotal; }
    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }
    public double getTotalFare() { return totalFare; }
    public void setTotalFare(double totalFare) { this.totalFare = totalFare; }
}