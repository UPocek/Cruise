package com.cruise.Cruise.unregisteredUser.Services;

import com.cruise.Cruise.ride.DTO.LocationForRideDTO;
import com.cruise.Cruise.ride.DTO.RideEstimationDTO;
import com.cruise.Cruise.ride.DTO.RideRequestBasicDTO;
import com.cruise.Cruise.unregisteredUser.DTO.DistanceEstimationDTO;

import java.util.List;

public interface IUnregisteredUserService {

    RideEstimationDTO getRideEstimation(RideRequestBasicDTO requestBasicDTO);

    List<Double> getTimeAndDistanceEstimation(LocationForRideDTO origin, LocationForRideDTO destination, int timeInSecondsSinceMidnightJanuary1st1970);

    DistanceEstimationDTO getRideDistance(RideRequestBasicDTO rideRequestBasicDTO);
}
