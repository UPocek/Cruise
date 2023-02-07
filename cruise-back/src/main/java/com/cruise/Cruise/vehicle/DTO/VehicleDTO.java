package com.cruise.Cruise.vehicle.DTO;

public class VehicleDTO {

    private Long id;
    private Long driverId;
    private String model;
    private String vehicleType;
    private String licenseNumber;
    private int passengerSeats;
    private LocationDTO currentLocation;
    private Boolean babyTransport;
    private Boolean petTransport;

    public VehicleDTO() {
    }

    public VehicleDTO(Long id, Long driverId, String model, String vehicleType, String licenseNumber, int passengerSeats, LocationDTO currentLocation, Boolean babyTransport, Boolean petTransport) {
        this.id = id;
        this.driverId = driverId;
        this.model = model;
        this.vehicleType = vehicleType;
        this.licenseNumber = licenseNumber;
        this.passengerSeats = passengerSeats;
        this.currentLocation = currentLocation;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
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
