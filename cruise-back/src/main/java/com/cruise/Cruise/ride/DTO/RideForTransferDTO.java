package com.cruise.Cruise.ride.DTO;

import com.cruise.Cruise.models.Passenger;
import com.cruise.Cruise.models.Ride;
import com.cruise.Cruise.models.Route;

import java.util.ArrayList;
import java.util.List;


public class RideForTransferDTO {
    private Long id;
    private String startTime;
    private String endTime;
    private double totalCost;
    private UserForRideDTO driver;
    private List<UserForRideDTO> passengers;
    private int estimatedTimeInMinutes;
    private String vehicleType;
    private Boolean babyTransport;
    private Boolean petTransport;
    private RejectionForRideDTO rejection;
    private List<RouteForRideDTO> locations;
    private String status;
    private double distance;

    public RideForTransferDTO() {

    }

    public RideForTransferDTO(Ride ride) {
        this.id = ride.getId();
        this.startTime = String.valueOf(ride.getStartTime());
        this.endTime = String.valueOf(ride.getEndTime());
        this.totalCost = ride.getPrice();
        this.driver = ride.getDriver() != null ? new UserForRideDTO(ride.getDriver()) : null;

        List<UserForRideDTO> passengers = new ArrayList<>();
        for (Passenger passenger : ride.getPassengers()) {
            passengers.add(new UserForRideDTO(passenger));
        }

        this.passengers = passengers;
        this.estimatedTimeInMinutes = (int) ride.getEstimatedTime();
        this.vehicleType = ride.getVehicleType() != null ? ride.getVehicleType().getName() : "STANDARD";
        this.babyTransport = ride.getBabyInVehicle();
        this.petTransport = ride.getPetInVehicle();

        List<RouteForRideDTO> routes = new ArrayList<>();
        for (Route route : ride.getRoutes()) {
            routes.add(new RouteForRideDTO(route));
        }
        this.locations = routes;

        this.rejection = ride.getRejection() != null ? new RejectionForRideDTO(ride.getRejection()) : null;
        this.status = ride.getRideState();
        this.distance = ride.getRoutes().size() > 0 ? ride.getRoutes().iterator().next().getDistance() : 0;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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

    public Boolean getBabyTransport() {
        return babyTransport;
    }

    public void setBabyTransport(Boolean babyTransport) {
        this.babyTransport = babyTransport;
    }

    public Boolean getPetTransport() {
        return petTransport;
    }

    public void setPetTransport(Boolean petTransport) {
        this.petTransport = petTransport;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public RejectionForRideDTO getRejection() {
        return rejection;
    }

    public void setRejection(RejectionForRideDTO rejection) {
        this.rejection = rejection;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "RideForTransferDTO{" + "id=" + id + ", startTime='" + startTime + '\'' + ", endTime='" + endTime + '\'' + ", totalCost=" + totalCost + ", driver=" + driver + ", passengers=" + passengers + ", estimatedTimeInMinutes=" + estimatedTimeInMinutes + ", vehicleType='" + vehicleType + '\'' + ", babyTransport=" + babyTransport + ", petTransport=" + petTransport + ", rejection=" + rejection + ", locations=" + locations + ", status='" + status + '\'' + ", distance=" + distance + '}';
    }
}
