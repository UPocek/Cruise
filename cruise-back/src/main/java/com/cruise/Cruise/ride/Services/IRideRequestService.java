package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.models.Passenger;
import com.cruise.Cruise.models.Ride;
import com.cruise.Cruise.models.Route;
import com.cruise.Cruise.passenger.DTO.EmailsDTO;
import com.cruise.Cruise.ride.DTO.AnswerEmailDTO;
import com.cruise.Cruise.ride.DTO.LocationForRideDTO;
import com.cruise.Cruise.ride.DTO.RideDTO;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;

import java.util.Collection;
import java.util.Set;

public interface IRideRequestService {

    RideForTransferDTO createRideBasic(RideDTO rideDTO);

    RideForTransferDTO createRideForNow(RideDTO rideDTO);

    void addPassengerToRide(String passengerEmail, Long rideId);

    RideForTransferDTO createPendingRide(RideDTO rideDTO);

    RideForTransferDTO getRideRequest(Long id);

    RideForTransferDTO markRideAsRejected(RideForTransferDTO rideResponse);

    RideForTransferDTO markRideAsAccepted(RideForTransferDTO rideResponse);

    void sendInvitations(EmailsDTO emails);

    Collection<AnswerEmailDTO> getPassengerInvites(Long id);

    void deleteRideInvitation(Long rideId, String receiverPassengerId);

    boolean checkIfPassengerCanRequestRide(Passenger passenger);

    boolean checkIfRideCanBeRequested(Set<Passenger> passengers);

    Route findRouteOrCreate(LocationForRideDTO departureDto, LocationForRideDTO destinationDto, double distance);

    RideForTransferDTO markRideAsInReview(RideForTransferDTO ride);
    boolean assignDriverToRide(Ride ride);


}
