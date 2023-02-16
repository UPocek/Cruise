package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.panic.DTO.PanicDTO;
import com.cruise.Cruise.ride.DTO.FavouriteRideBasicDTO;
import com.cruise.Cruise.ride.DTO.FavouriteRideDTO;
import com.cruise.Cruise.ride.DTO.ReasonDTO;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collection;

@Service
public interface IRideService {

    PanicDTO panic(Long id, ReasonDTO reasonDTO, Principal user);

    RideForTransferDTO acceptRide(Long id, Principal driver);

    RideForTransferDTO endRide(Long id);

    RideForTransferDTO cancelRideWithExplanation(Long rideId, ReasonDTO reason);

    RideForTransferDTO getRideForDriverByStatus(Long driverId, String status);

    RideForTransferDTO getRideForPassengerByStatus(Long passengerId, String status);

    RideForTransferDTO cancelExistingRide(Long rideId);

    void notifyPassengersThatDriverArrivedToPickUpLocation(Long rideId);

    RideForTransferDTO startRide(Long rideId);

    FavouriteRideDTO addNewFavouriteRide(FavouriteRideBasicDTO favouriteRide, Principal user);

    Collection<FavouriteRideDTO> getAllFavouriteRidesByPassenger(Principal user);

    void deleteFavouriteRide(Long rideId, Principal user);
}
