package com.cruise.Cruise.vehicle.DTO;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class LocationDTO {
    @NotNull(message = "is required!")
    private String address;
    @NotNull(message = "is required!")
    @Min(-90)
    @Max(90)
    private Double latitude;
    @NotNull(message = "is required!")
    @Min(-180)
    @Max(180)
    private Double longitude;

    public LocationDTO() {
    }

    public LocationDTO(String address, double latitude, double longitude) {
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
