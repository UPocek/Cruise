package com.cruisemobile.cruise.models;

public class VehicleToDriveDTO {

    private Long id;
    private Long driverId;
    private LocationDTO currentLocation;
    private String status;

    public VehicleToDriveDTO() {
    }

    public VehicleToDriveDTO(Long id, Long driverId, LocationDTO currentLocation, String status) {
        this.id = id;
        this.driverId = driverId;
        this.currentLocation = currentLocation;
        this.status = status;
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

    public LocationDTO getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(LocationDTO currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
