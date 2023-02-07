package com.cruisemobile.cruise.models;

import java.util.List;

public class RideForFutureDTO {

    private List<LocationPairDTO> locations;

    private List<UserForRideDTO> passengers;

    private String vehicleType;

    private boolean babyTransport;

    private boolean petTransport;

    private double price;

    private Double timeEstimation;

    private Double distance;

    private String startTime;

    public RideForFutureDTO() {
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTimeEstimation() {
        return timeEstimation;
    }

    public void setTimeEstimation(double timeEstimation) {
        this.timeEstimation = timeEstimation;
    }

    public List<LocationPairDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationPairDTO> locations) {
        this.locations = locations;
    }

    public List<UserForRideDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<UserForRideDTO> passengers) {
        this.passengers = passengers;
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
