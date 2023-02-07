package com.cruise.Cruise.ride.DTO;

import com.cruise.Cruise.driver.DTO.DriverBasicInfoDTO;
import com.cruise.Cruise.models.*;
import com.cruise.Cruise.passenger.DTO.PassengerBasicInfoDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RideForUserDTO {

    private Long id;
    private List<RouteForRideDTO> locations;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalCost;
    private DriverBasicInfoDTO driver;
    private List<PassengerBasicInfoDTO> passengers;
    private double estimatedTimeInMinutes;
    private String vehicleType;
    private boolean babyTransport;
    private boolean petTransport;
    private RejectionForRideDTO rejection;
    private String status;

    public RideForUserDTO() {
    }

    public RideForUserDTO(Long id, List<RouteForRideDTO> locations, LocalDateTime startTime, LocalDateTime endTime, double totalCost, DriverBasicInfoDTO driver, List<PassengerBasicInfoDTO> passengers, double estimatedTimeInMinutes, String vehicleType, boolean babyTransport, boolean petTransport, RejectionForRideDTO rejection, String status) {
        this.id = id;
        this.locations = locations;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalCost = totalCost;
        this.driver = driver;
        this.passengers = passengers;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.vehicleType = vehicleType;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
        this.rejection = rejection;
        this.status = status;
    }

    public RideForUserDTO(Ride ride) {
        Driver ridesDriver = ride.getDriver();
        DriverBasicInfoDTO driver = ridesDriver != null ? new DriverBasicInfoDTO(ridesDriver.getId(), ridesDriver.getEmail()) : null;
        List<PassengerBasicInfoDTO> passengers = new ArrayList<>();
        for (Passenger passengerFromRide : ride.getPassengers()) {
            passengers.add(new PassengerBasicInfoDTO(passengerFromRide.getId(), passengerFromRide.getEmail()));
        }
        List<RouteForRideDTO> locations = new ArrayList<>();
        for (Route route : ride.getRoutes()) {
            Location start = route.getStartLocation();
            Location end = route.getEndLocation();
            locations.add(new RouteForRideDTO(new LocationForRideDTO(start.getAddress(), start.getLatitude(), start.getLongitude()), new LocationForRideDTO(end.getAddress(), end.getLatitude(), end.getLongitude())));
        }
        Rejection rideRejection = ride.getRejection();
        RejectionForRideDTO rejection = rideRejection != null ? new RejectionForRideDTO(rideRejection) : null;

        this.id = ride.getId();
        this.locations = locations;
        this.startTime = ride.getStartTime();
        this.endTime = ride.getEndTime();
        this.totalCost = ride.getPrice();
        this.driver = driver;
        this.passengers = passengers;
        this.estimatedTimeInMinutes = ride.getEstimatedTime();
        this.vehicleType = ride.getVehicleType().toString();
        this.babyTransport = ride.getBabyInVehicle();
        this.petTransport = ride.getPetInVehicle();
        this.rejection = rejection;
        this.status = ride.getRideState();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<RouteForRideDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RouteForRideDTO> locations) {
        this.locations = locations;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public DriverBasicInfoDTO getDriver() {
        return driver;
    }

    public void setDriver(DriverBasicInfoDTO driver) {
        this.driver = driver;
    }

    public List<PassengerBasicInfoDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerBasicInfoDTO> passengers) {
        this.passengers = passengers;
    }

    public double getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public void setEstimatedTimeInMinutes(double estimatedTimeInMinutes) {
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isBabyTransport() {
        return babyTransport;
    }

    public void setBabyTransport(boolean babyTransport) {
        this.babyTransport = babyTransport;
    }

    public boolean isPetTransport() {
        return petTransport;
    }

    public void setPetTransport(boolean petTransport) {
        this.petTransport = petTransport;
    }

    public RejectionForRideDTO getRejection() {
        return rejection;
    }

    public void setRejection(RejectionForRideDTO rejection) {
        this.rejection = rejection;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
