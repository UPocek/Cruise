package com.cruise.Cruise.ride.DTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

public class FavouriteRideBasicDTO {
    @NotNull(message = "is required!")
    private String favoriteName;
    @Valid
    @NotNull(message = "is required!")
    private List<RouteForRideDTO> locations;
    @Valid
    @NotNull(message = "is required!")
    private List<UserForRideDTO> passengers;
    @NotNull(message = "is required!")
    @Pattern(regexp = "^(STANDARD|LUXURY|LUXARY|VAN)$", message = "format is not valid!")
    private String vehicleType;
    @NotNull(message = "is required!")
    private Boolean babyTransport;
    @NotNull(message = "is required!")
    private Boolean petTransport;
    @NotNull(message = "is required!")
    private Double distance;

    public FavouriteRideBasicDTO() {
    }

    public FavouriteRideBasicDTO(String favoriteName, List<RouteForRideDTO> locations, List<UserForRideDTO> passengers, String vehicleType, boolean babyTransport, boolean petTransport, double distance) {
        this.favoriteName = favoriteName;
        this.locations = locations;
        this.passengers = passengers;
        this.vehicleType = vehicleType;
        this.babyTransport = babyTransport;
        this.petTransport = petTransport;
        this.distance = distance;
    }

    public String getFavoriteName() {
        return favoriteName;
    }

    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
    }

    public List<RouteForRideDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<RouteForRideDTO> locations) {
        this.locations = locations;
    }

    public List<UserForRideDTO> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<UserForRideDTO> passengers) {
        this.passengers = passengers;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
