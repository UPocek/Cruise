package com.cruise.Cruise.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Driver driver;
    private String make;
    @OneToOne
    private VehicleType type;
    private String licencePlate;
    private int seatsNum;
    @OneToOne(cascade = CascadeType.ALL)
    private Location location;
    private Boolean isBabiesAllowed;
    private Boolean isPetsAllowed;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();

    public Vehicle() {
    }

    public Vehicle(Long id, Driver driver, String make, VehicleType type, String licencePlate, int seatsNum, Location location, Boolean isBabiesAllowed, Boolean isPetsAllowed, Set<Review> reviews) {
        this.id = id;
        this.driver = driver;
        this.make = make;
        this.type = type;
        this.licencePlate = licencePlate;
        this.seatsNum = seatsNum;
        this.location = location;
        this.isBabiesAllowed = isBabiesAllowed;
        this.isPetsAllowed = isPetsAllowed;
        this.reviews = reviews;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public VehicleType getType() {
        return type;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public int getSeatsNum() {
        return seatsNum;
    }

    public void setSeatsNum(int seatsNum) {
        this.seatsNum = seatsNum;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getBabiesAllowed() {
        return isBabiesAllowed;
    }

    public void setBabiesAllowed(Boolean babiesAllowed) {
        isBabiesAllowed = babiesAllowed;
    }

    public Boolean getPetsAllowed() {
        return isPetsAllowed;
    }

    public void setPetsAllowed(Boolean petsAllowed) {
        isPetsAllowed = petsAllowed;
    }

    public Set<Review> getReviews() {
        return reviews;
    }

    public void setReviews(Set<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }
}
