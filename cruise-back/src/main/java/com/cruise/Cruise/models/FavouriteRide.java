package com.cruise.Cruise.models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "favourite_ride")
public class FavouriteRide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String favoriteName;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(name = "favouriteRideRoutes",
            joinColumns = @JoinColumn(name = "favourite_ride_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"))
    private List<Route> locations;

    @ManyToMany(cascade = CascadeType.REFRESH)
    @JoinTable(name = "passengersInRide",
            joinColumns = @JoinColumn(name = "favourite_ride_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"))
    private List<Passenger> passengers;
    private String vehicleType;
    private boolean babyTransport;
    private boolean petTransport;
    private double distance;

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

    public List<Route> getLocations() {
        return locations;
    }

    public void setLocations(List<Route> locations) {
        this.locations = locations;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
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

    public FavouriteRide(FavouriteRide other) {
        this.id = other.id;
        this.favoriteName = other.favoriteName;
        this.locations = new ArrayList<>(other.locations);
        this.passengers = new ArrayList<>(other.passengers);
        this.vehicleType = other.vehicleType;
        this.babyTransport = other.babyTransport;
        this.petTransport = other.petTransport;
        this.distance = other.distance;
    }

    public FavouriteRide() {

    }




}
