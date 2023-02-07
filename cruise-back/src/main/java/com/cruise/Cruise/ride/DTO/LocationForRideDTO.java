package com.cruise.Cruise.ride.DTO;


import com.cruise.Cruise.models.Location;

import javax.validation.constraints.NotNull;

public class LocationForRideDTO {
    @NotNull(message = "is required!")
    private String address;
    @NotNull(message = "is required!")
    private Double latitude;
    @NotNull(message = "is required!")
    private Double longitude;

    public LocationForRideDTO() {
    }

    public LocationForRideDTO(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.address = location.getAddress();
    }

    public LocationForRideDTO(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
