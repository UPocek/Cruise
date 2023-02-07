package com.cruise.Cruise.driver.DTO;

import com.cruise.Cruise.vehicle.DTO.LocationDTO;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class CreateVehicleDTO {
    @NotNull(message = "is required!")
    @Length(min = 2, max = 20, message = "must have from 2 to 20 characters")
    private String model;
    @NotNull(message = "is required!")
    @Pattern(regexp = "^(STANDARD|LUXURY|LUXARY|VAN)$", message = "format is not valid!")
    private String vehicleType;
    @NotNull(message = "is required!")
    @Length(min = 2, max = 20, message = "must have from 2 to 20 characters")
    private String licenseNumber;
    @NotNull(message = "is required!")
    @Min(value = 1, message = "must be equal or greater than 1")
    private Integer passengerSeats;
    @NotNull(message = "is required!")
    private LocationDTO currentLocation;
    @NotNull(message = "is required!")
    private Boolean babyTransport;
    @NotNull(message = "is required!")
    private Boolean petTransport;

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
