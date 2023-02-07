package com.cruise.Cruise.ride.Repositories;

import com.cruise.Cruise.models.RideInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRideInvitationRepository extends JpaRepository<RideInvitation, Long> {

    List<RideInvitation> findAllByPassengerId(Long id);

    Optional<RideInvitation> findByPassengerEmailAndRideId(String passengerEmail, Long rideId);
}
