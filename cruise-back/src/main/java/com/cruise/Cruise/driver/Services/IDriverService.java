package com.cruise.Cruise.driver.Services;

import com.cruise.Cruise.driver.DTO.*;
import com.cruise.Cruise.models.DriverInfoRequest;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;
import com.cruise.Cruise.user.DTO.AllHistoryItemsDTO;
import com.cruise.Cruise.vehicle.DTO.VehicleDTO;
import com.cruise.Cruise.vehicle.DTO.VehicleToDriveDTO;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IDriverService {

    RegisteredDriverDTO getById(Long id);

    Map<String, Object> getAllDrivers();

    List<DocumentDTO> getDocumentsByDriverId(Long id);

    VehicleDTO getVehicleById(Long id);

    DriversRideListDTO getRidesById(Long id, int page, int size, String sort, String from, String to);

    RegisteredDriverDTO getByEmail(String email);

    RegisteredDriverDTO createNewDriver(CreateDriverDTO driver);

    DocumentDTO addDriverDocument(Long id, CreateDocumentDTO document);

    VehicleDTO addDriversVehicle(Long id, CreateVehicleDTO newVehicle);

    void setDriverActivity(Long id, boolean activityStatus);

    VehicleDTO updateDriversVehicle(Long id, CreateVehicleDTO newVehicle);

    DriverInfoRequest requestChanges(Long id, CreateDriverDTO driverDTO);

    RegisteredDriverDTO updateDriver(Long id, CreateDriverDTO driverDTO);

    void deleteDriverDocument(Long id);

    List<DriverInfoRequestDTO> getAllRequests();

    void deleteRequest(Long id);

    Collection<VehicleToDriveDTO> getAllActiveVehicles();

    RegisteredDriverDTO block(String email, boolean block);

    boolean changeDriverActivityStatus(Long id, boolean activityStatus);

    RideForTransferDTO getFirstAssignedRideToDriver(Principal user);

    AllHistoryItemsDTO getAllUserHistoryItemsByUserId(Long id, Pageable pageable, String sort);
}
