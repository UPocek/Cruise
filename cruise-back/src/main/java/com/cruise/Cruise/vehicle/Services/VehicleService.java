package com.cruise.Cruise.vehicle.Services;

import com.cruise.Cruise.driver.Services.IDriverService;
import com.cruise.Cruise.models.Location;
import com.cruise.Cruise.models.Vehicle;
import com.cruise.Cruise.vehicle.Controllers.AndroidVehicleController;
import com.cruise.Cruise.vehicle.DTO.AllVehiclesDTO;
import com.cruise.Cruise.vehicle.DTO.LocationDTO;
import com.cruise.Cruise.vehicle.DTO.VehicleToDriveDTO;
import com.cruise.Cruise.vehicle.Repositories.IVehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@Service
public class VehicleService implements IVehicleService {

    @Autowired
    private IDriverService driverService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IVehicleRepository vehicleRepository;

    @Override
    public void updateVehicleLocation(Long id, LocationDTO newLocation) {
        Optional<Vehicle> result = vehicleRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle does not exist");
        }
        Vehicle vehicle = result.get();
        Location vehicleLocation = vehicle.getLocation();
        vehicleLocation.setLatitude(newLocation.getLatitude());
        vehicleLocation.setLongitude(newLocation.getLongitude());
        vehicleLocation.setAddress(newLocation.getAddress());
        vehicle.setLocation(vehicleLocation);
        vehicleRepository.save(vehicle);
        vehicleRepository.flush();
    }

    @Override
    public void updateAllVehiclesLocation(AllVehiclesDTO vehicles) {
        for (VehicleToDriveDTO vehicle : vehicles.getResults()) {
            updateVehicleLocation(vehicle.getId(), vehicle.getCurrentLocation());
        }
    }

    @Scheduled(fixedRate = 9000, initialDelayString = "${timing.initialScheduledDelay}")
    private void sendNewVehiclePositions() {
        if (AndroidVehicleController.openVehicleSessions.size() == 0) {
            return;
        }
        Collection<VehicleToDriveDTO> allActiveVehicles = driverService.getAllActiveVehicles();
        for (WebSocketSession session : AndroidVehicleController.openVehicleSessions) {
            try {
                session.sendMessage(new TextMessage(objectMapper.writeValueAsString(allActiveVehicles)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
