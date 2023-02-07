package com.cruise.Cruise.ride.DTO;

import com.cruise.Cruise.models.FavouriteRide;
import com.cruise.Cruise.models.Passenger;
import com.cruise.Cruise.models.Route;

import java.util.ArrayList;
import java.util.List;

public class FavouriteRideDTO {
    private Long id;
    private String favoriteName;
    private List<RouteForRideDTO> locations;
    private List<UserForRideDTO> passengers;
    private String vehicleType;
    private boolean babyTransport;
    private boolean petTransport;
    private double distance;

    public FavouriteRideDTO() {
    }

    public FavouriteRideDTO(Long id, String favoriteName, List<RouteForRideDTO> locations, List<UserForRideDTO> passengers, String vehicleType, boolean babyTransport, boolean petTransport, double distance) {
        this.id = id;
        this.favoriteName = favoriteName;
        this.locations = locations;
        this.passengers = passengers;
        this.vehicleType = vehicleType;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
        this.distance = distance;
    }

    public FavouriteRideDTO(FavouriteRide ride) {
        this.id = ride.getId();
        this.favoriteName = ride.getFavoriteName();
        List<RouteForRideDTO> locations = new ArrayList<>();
        for (Route route : ride.getLocations()) {
            locations.add(new RouteForRideDTO(route));
        }
        this.locations = locations;
        List<UserForRideDTO> passengers = new ArrayList<>();
        for (Passenger passenger : ride.getPassengers()) {
            passengers.add(new UserForRideDTO(passenger));
        }
        this.passengers = passengers;
        this.vehicleType = ride.getVehicleType();
        this.babyTransport = ride.isBabyTransport();
        this.petTransport = ride.isPetTransport();
        this.distance = ride.getDistance();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFavoriteName() {
        return favoriteName;
    }

    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
