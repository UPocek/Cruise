package com.cruisemobile.cruise.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleDTO {
    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("driverId")
    @Expose
    private Long driverId;

    @SerializedName("model")
    @Expose
    private String model;

    @SerializedName("vehicleType")
    @Expose
    private String vehicleType;

    @SerializedName("licenseNumber")
    @Expose
    private String licenseNumber;

    @SerializedName("passengerSeats")
    @Expose
    private int passengerSeats;

    @SerializedName("currentLocation")
    @Expose
    private LocationDTO currentLocation;

    @SerializedName("babyTransport")
    @Expose
    private Boolean babyTransport;

    @SerializedName("petTransport")
    @Expose
    private Boolean petTransport;

    public VehicleDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getPassengerSeats() {
        return passengerSeats;
    }

    public void setPassengerSeats(int passengerSeats) {
        this.passengerSeats = passengerSeats;
    }

    public LocationDTO getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationDTO currentLocation) {
        this.currentLocation = currentLocation;
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
}
