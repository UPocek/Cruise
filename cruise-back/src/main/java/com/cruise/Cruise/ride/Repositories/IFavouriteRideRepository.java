package com.cruise.Cruise.ride.Repositories;

import com.cruise.Cruise.models.FavouriteRide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFavouriteRideRepository extends JpaRepository<FavouriteRide, Long> {
}
