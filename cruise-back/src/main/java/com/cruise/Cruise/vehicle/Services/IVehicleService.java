package com.cruise.Cruise.vehicle.Services;

import com.cruise.Cruise.vehicle.DTO.AllVehiclesDTO;
import com.cruise.Cruise.vehicle.DTO.LocationDTO;

public interface IVehicleService {
    void updateVehicleLocation(Long id, LocationDTO newLocation);

    void updateAllVehiclesLocation(AllVehiclesDTO vehicles);

}
