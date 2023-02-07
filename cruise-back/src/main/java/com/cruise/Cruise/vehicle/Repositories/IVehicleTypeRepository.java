package com.cruise.Cruise.vehicle.Repositories;

import com.cruise.Cruise.models.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVehicleTypeRepository extends JpaRepository<VehicleType, Long> {

    VehicleType findByName(String name);

}
