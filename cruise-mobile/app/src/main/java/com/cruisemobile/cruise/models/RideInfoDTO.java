package com.cruisemobile.cruise.models;

import java.util.List;

public class RideInfoDTO {
    List<LocationPairDTO> locations;
    String vehicleType;
    boolean babyTransport;
    boolean petTransport;

    public RideInfoDTO() {
    }

    public RideInfoDTO(List<LocationPairDTO> locations, String vehicleType, boolean babyTransport, boolean petTransport) {
        this.locations = locations;
        this.vehicleType = vehicleType;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
    }

    public List<LocationPairDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationPairDTO> locations) {
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
}
