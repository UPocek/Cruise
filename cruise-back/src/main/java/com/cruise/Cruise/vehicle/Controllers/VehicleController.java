package com.cruise.Cruise.vehicle.Controllers;

import com.cruise.Cruise.vehicle.DTO.AllVehiclesDTO;
import com.cruise.Cruise.vehicle.DTO.LocationDTO;
import com.cruise.Cruise.vehicle.Services.IVehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {
    @Autowired
    IVehicleService vehicleService;

    @PutMapping("/{id}/location")
    @Valid
    public ResponseEntity<String> updateVehicleLocation(@PathVariable Long id, @Valid @RequestBody LocationDTO location) {
        vehicleService.updateVehicleLocation(id, location);
        return new ResponseEntity<>("Coordinates successfully updated", HttpStatus.NO_CONTENT);
    }

    @PutMapping("/all/location")
    public ResponseEntity<String> updateAllVehicleLocations(@RequestBody AllVehiclesDTO vehicles) {
        vehicleService.updateAllVehiclesLocation(vehicles);
        return new ResponseEntity<>("Coordinates successfully updated", HttpStatus.OK);
    }
}
