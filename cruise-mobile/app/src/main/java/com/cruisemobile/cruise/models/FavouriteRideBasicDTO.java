package com.cruisemobile.cruise.models;

import java.util.List;

public class FavouriteRideBasicDTO {

    private String favoriteName;
    private List<LocationPairDTO> locations;
    private List<UserForRideDTO> passengers;
    private String vehicleType;
    private Boolean babyTransport;
    private Boolean petTransport;
    private Double distance;

    public FavouriteRideBasicDTO() {
    }

    public FavouriteRideBasicDTO(String favoriteName, List<LocationPairDTO> locations, List<UserForRideDTO> passengers, String vehicleType, Boolean babyTransport, Boolean petTransport, Double distance) {
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

    public List<LocationPairDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationPairDTO> locations) {
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
