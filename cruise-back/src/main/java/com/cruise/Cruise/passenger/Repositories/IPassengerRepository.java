package com.cruise.Cruise.passenger.Repositories;

import com.cruise.Cruise.models.FavouriteRide;
import com.cruise.Cruise.models.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface IPassengerRepository extends JpaRepository<Passenger, Long> {
    Optional<Passenger> findByEmail(String email);

    @Query("SELECT p.favouriteRides FROM Passenger p WHERE p.email = :email")
    Set<FavouriteRide> findAllPassengerFavouriteRides(String email);

}
