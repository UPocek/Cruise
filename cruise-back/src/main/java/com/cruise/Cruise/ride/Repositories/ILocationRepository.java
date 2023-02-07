package com.cruise.Cruise.ride.Repositories;

import com.cruise.Cruise.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ILocationRepository extends JpaRepository<Location, Long> {
    Location findByLatitudeAndLongitude(double latitude, double longitude);
}
