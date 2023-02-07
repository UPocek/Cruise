package com.cruise.Cruise.ride.Repositories;

import com.cruise.Cruise.models.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoutesRepository extends JpaRepository<Route, Long> {
    Route findByStartLocationLongitudeAndStartLocationLatitudeAndEndLocationLongitudeAndEndLocationLatitude(double startLocationLongitude, double startLocationLatitude, double endLocationLongitude, double endLocationLatitude);
}
