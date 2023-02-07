package com.cruise.Cruise.vehicle.Repositories;

import com.cruise.Cruise.models.Review;
import com.cruise.Cruise.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("SELECT v.reviews FROM Vehicle v WHERE v.id = :vehicleId")
    Set<Review> findAllVehicleReviews(Long vehicleId);
}
