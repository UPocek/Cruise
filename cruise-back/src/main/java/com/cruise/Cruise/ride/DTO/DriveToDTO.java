package com.cruise.Cruise.ride.DTO;

import com.cruise.Cruise.vehicle.DTO.VehicleToDriveDTO;

public class DriveToDTO {

    private Long rideId;
    private VehicleToDriveDTO vehicle;
    private LocationForRideDTO location;

    public DriveToDTO() {
    }

    public DriveToDTO(Long rideId, VehicleToDriveDTO vehicle, LocationForRideDTO location) {
        this.rideId = rideId;
        this.vehicle = vehicle;
        this.location = location;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public VehicleToDriveDTO getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleToDriveDTO vehicle) {
        this.vehicle = vehicle;
    }

    public LocationForRideDTO getLocation() {
        return location;
    }

    public void setLocation(LocationForRideDTO location) {
        this.location = location;
    }
}
