package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.driver.Services.NoDriverAvailableForRideException;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;

import java.util.Set;

public interface IFindDriverService {
    RideForTransferDTO findDriverForRide(RideForTransferDTO rideDTO, Set<Driver> driversThatRejectedRideRequest, Set<Driver> driversListeningOnWebSocket) throws NoDriverAvailableForRideException;

    RideForTransferDTO findDriverForRide(RideForTransferDTO rideDTO, Set<Driver> driversThatRejectedRideRequest) throws NoDriverAvailableForRideException;
}
