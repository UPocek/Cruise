package com.cruise.Cruise.ride.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

public class RideRequestBasicDTO {

    @NotEmpty(message = "is required!")
    private List<RouteForRideDTO> locations;
    @NotBlank(message = "is required!")
    @Pattern(regexp = "^(STANDARD|LUXURY|LUXARY|VAN)$", message = "format is not valid!")
    private String vehicleType;
    @NotNull(message = "is required!")
    private Boolean babyTransport;
    @NotNull(message = "is required!")
    private Boolean petTransport;

    public List<RouteForRideDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RouteForRideDTO> locations) {
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
