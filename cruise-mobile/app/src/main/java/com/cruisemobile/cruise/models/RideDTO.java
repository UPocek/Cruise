package com.cruisemobile.cruise.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RideDTO {
    @SerializedName("passengers")
    @Expose
    @Nullable
    private UserForRideDTO[] passengers;

    @SerializedName("locations")
    @Expose
    @Nullable
    private LocationPairDTO[] locations;

    @SerializedName("vehicleType")
    @Expose
    @Nullable
    private String vehicleType;

    @SerializedName("babyTransport")
    @Expose
    @Nullable
    private boolean babyTransport;

    @SerializedName("petTransport")
    @Expose
    @Nullable
    private boolean petTransport;

    @SerializedName("estimatedTimeInMinutes")
    @Expose
    @Nullable
    private int estimatedTimeInMinutes;

    @SerializedName("status")
    @Expose
    @Nullable
    private String status;

    @SerializedName("id")
    @Expose
    @Nullable
    private Long id;

    @SerializedName("startTime")
    @Expose
    @Nullable
    private String startTime;

    @SerializedName("endTime")
    @Expose
    @Nullable
    private String endTime;

    @SerializedName("totalCost")
    @Expose
    @Nullable
    private double totalCost;

    @SerializedName("driver")
    @Expose
    @Nullable
    private UserForRideDTO driver;

    @SerializedName("rejection")
    @Expose
    @Nullable
    private RejectionDTO rejection;

    @SerializedName("distance")
    @Expose
    @Nullable
    private double distance;

    public RideDTO() {
    }

    public RideDTO(@Nullable UserForRideDTO[] passengers, @Nullable LocationPairDTO[] locations, @Nullable String vehicleType, boolean babyTransport, boolean petTransport, int estimatedTimeInMinutes, @Nullable String status, @Nullable Long id, @Nullable String startTime, @Nullable String endTime, double totalCost, @Nullable UserForRideDTO driver, @Nullable RejectionDTO rejection, double distance) {
        this.passengers = passengers;
        this.locations = locations;
        this.vehicleType = vehicleType;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalCost = totalCost;
        this.driver = driver;
        this.rejection = rejection;
        this.distance = distance;
    }

    public UserForRideDTO[] getPassengers() {
        return passengers;
    }

    public void setPassengers(UserForRideDTO[] passengers) {
        this.passengers = passengers;
    }

    public LocationPairDTO[] getLocations() {
        return locations;
    }

    public void setLocations(LocationPairDTO[] locations) {
        this.locations = locations;
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

    public int getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public void setEstimatedTimeInMinutes(int estimatedTimeInMinutes) {
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
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

    public RejectionDTO getRejection() {
        return rejection;
    }

    public void setRejection(RejectionDTO rejection) {
        this.rejection = rejection;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
