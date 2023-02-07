package com.cruise.Cruise.ride.DTO;


import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class RideDTO {
    @Valid
    @NotNull(message = "is required!")
    private List<RouteForRideDTO> locations;
    @Valid
    @NotNull(message = "is required!")
    private List<UserForRideDTO> passengers;
    @NotNull(message = "is required!")
    private String vehicleType;
    @NotNull(message = "is required!")
    private Boolean babyTransport;
    @NotNull(message = "is required!")
    private Boolean petTransport;
    @NotNull(message = "is required!")
    private Double price;
    @NotNull(message = "is required!")
    private Double timeEstimation;
    @NotNull(message = "is required!")
    private Double distance;
    @NotNull(message = "is required!")
    private String startTime;

    public RideDTO() {
    }

    public RideDTO(List<RouteForRideDTO> locations, List<UserForRideDTO> passengers, String vehicleType, boolean babyTransport, boolean petTransport, int price, double timeEstimation, int distance, String startTime) {
        this.locations = locations;
        this.passengers = passengers;
        this.vehicleType = vehicleType;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
        this.price = (double) price;
        this.timeEstimation = timeEstimation;
        this.distance = (double) distance;
        this.startTime = startTime;
    }

    public RideDTO(RideForTransferDTO rideForTransferDTO) {
        this.locations = rideForTransferDTO.getLocations();
        this.passengers = rideForTransferDTO.getPassengers();
        this.vehicleType = rideForTransferDTO.getVehicleType();
        this.babyTransport = rideForTransferDTO.getBabyTransport();
        this.petTransport = rideForTransferDTO.getPetTransport();
        this.price = rideForTransferDTO.getTotalCost();
        this.timeEstimation = (double) rideForTransferDTO.getEstimatedTimeInMinutes();
        this.distance = rideForTransferDTO.getDistance();
        this.startTime = String.valueOf(rideForTransferDTO.getStartTime());
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

    public List<RouteForRideDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RouteForRideDTO> locations) {
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
