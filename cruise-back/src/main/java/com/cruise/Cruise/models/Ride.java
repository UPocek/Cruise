package com.cruise.Cruise.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ride")
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double price;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    private Driver driver;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "passengers",
            joinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "passenger_id", referencedColumnName = "id"))
    private Set<Passenger> passengers = new HashSet<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ride_routes",
            joinColumns = @JoinColumn(name = "ride_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "route_id", referencedColumnName = "id"))
    private Set<Route> routes = new HashSet<>();
    private double estimatedTime;
    @OneToMany(mappedBy = "ride", fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();
    private String rideState;
    @OneToOne(mappedBy = "ride", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Rejection rejection;
    private Boolean isPanic;
    private Boolean isBabyInVehicle;
    private Boolean isPetInVehicle;
    @OneToOne
    private VehicleType vehicleType;

    public Ride() {
    }

    public Ride(Long id, LocalDateTime startTime, LocalDateTime endTime, double price, Driver driver, Set<Passenger> passengers, Set<Route> routes, double estimatedTime, Set<Review> reviews, String rideState, Rejection rejection, Boolean isPanic, Boolean isBabyInVehicle, Boolean isPetInVehicle, VehicleType vehicleType) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.driver = driver;
        this.passengers = passengers;
        this.routes = routes;
        this.estimatedTime = estimatedTime;
        this.reviews = reviews;
        this.rideState = rideState;
        this.rejection = rejection;
        this.isPanic = isPanic;
        this.isBabyInVehicle = isBabyInVehicle;
        this.isPetInVehicle = isPetInVehicle;
        this.vehicleType = vehicleType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Set<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(Set<Passenger> passengers) {
        this.passengers = passengers;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

    public double getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(double estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public String getRideState() {
        return rideState;
    }

    public void setRideState(String rideState) {
        this.rideState = rideState;
    }

    public Rejection getRejection() {
        return rejection;
    }

    public void setRejection(Rejection rejection) {
        this.rejection = rejection;
    }

    public Boolean getPanic() {
        return isPanic;
    }

    public void setPanic(Boolean panic) {
        isPanic = panic;
    }

    public Boolean getBabyInVehicle() {
        return isBabyInVehicle;
    }

    public void setBabyInVehicle(Boolean babyInVehicle) {
        isBabyInVehicle = babyInVehicle;
    }

    public Boolean getPetInVehicle() {
        return isPetInVehicle;
    }

    public void setPetInVehicle(Boolean petInVehicle) {
        isPetInVehicle = petInVehicle;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
}
