package com.cruise.Cruise.driver.DTO;

import com.cruise.Cruise.models.Ride;
import com.cruise.Cruise.models.User;
import com.cruise.Cruise.ride.DTO.RejectionForRideDTO;
import com.cruise.Cruise.ride.DTO.RouteForRideDTO;
import com.cruise.Cruise.ride.DTO.UserForRideDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DriversRideDTO {
    private Long id;
    private List<RouteForRideDTO> locations;
    private RejectionForRideDTO rejection;
    private String startTime;
    private String endTime;
    private double totalCost;
    private UserForRideDTO driver;
    private List<UserForRideDTO> passengers;
    private int estimatedTimeInMinutes;
    private String vehicleType;
    private boolean babyTransport;
    private boolean petTransport;

    public DriversRideDTO() {
    }

    public DriversRideDTO(Ride ride) {
        this.id = ride.getId();
        this.locations = new ArrayList<RouteForRideDTO>();
        this.locations.add(new RouteForRideDTO(ride.getRoutes().stream().findFirst()));
        if (ride.getRejection() == null) this.rejection = new RejectionForRideDTO();
        else this.rejection = new RejectionForRideDTO(ride.getRejection());
        this.startTime = ride.getStartTime().toString();
        this.endTime = ride.getEndTime().toString();
        this.totalCost = ride.getPrice();
        this.driver = new UserForRideDTO(ride.getDriver());
        this.passengers = new ArrayList<UserForRideDTO>();
        for (User user : ride.getPassengers())
            this.passengers.add(new UserForRideDTO(user));
        this.estimatedTimeInMinutes = (int) ride.getEstimatedTime();
        this.vehicleType = ride.getVehicleType().getName();
        this.babyTransport = ride.getBabyInVehicle();
        this.petTransport = ride.getPetInVehicle();
    }

    public DriversRideDTO(Long id, List<RouteForRideDTO> locations, RejectionForRideDTO rejection, String startTime, String endTime, double totalCost, UserForRideDTO driver, List<UserForRideDTO> passengers, int estimatedTimeInMinutes, String vehicleType, boolean babyTransport, boolean petTransport)
    {
        this.id = id;
        this.locations = locations;
        this.rejection = rejection;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalCost = totalCost;
        this.driver = driver;
        this.passengers = passengers;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.vehicleType = vehicleType;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
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

    public void setLocations(List<RouteForRideDTO> locationDTOList) {
        this.locations = locationDTOList;
    }

    public RejectionForRideDTO getRejection() {
        return rejection;
    }

    public void setRejection(RejectionForRideDTO rejection) {
        this.rejection = rejection;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = String.valueOf(startTime);
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = String.valueOf(endTime);
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public UserForRideDTO getDriver() {
        return driver;
    }

    public void setDriver(UserForRideDTO driver) {
        this.driver = driver;
    }

    public List<UserForRideDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<UserForRideDTO> passengers) {
        this.passengers = passengers;
    }

    public int getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public void setEstimatedTimeInMinutes(int estimatedTimeInMinutes) {
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
}
