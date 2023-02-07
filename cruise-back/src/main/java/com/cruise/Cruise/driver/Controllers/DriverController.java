package com.cruise.Cruise.driver.Controllers;

import com.cruise.Cruise.driver.DTO.*;
import com.cruise.Cruise.driver.Services.ICalculateWorkingHoursService;
import com.cruise.Cruise.driver.Services.IDriverService;
import com.cruise.Cruise.models.DriverInfoRequest;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;
import com.cruise.Cruise.security.IdentityCheck;
import com.cruise.Cruise.user.DTO.AllHistoryItemsDTO;
import com.cruise.Cruise.vehicle.DTO.VehicleDTO;
import com.cruise.Cruise.vehicle.DTO.VehicleToDriveDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/driver")
public class DriverController {

    @Autowired
    IDriverService driverService;

    @Autowired
    ICalculateWorkingHoursService calculateWorkingHoursService;

    //POST /api/driver
    @PostMapping()
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RegisteredDriverDTO createDriver(@Valid @RequestBody CreateDriverDTO newDriver) {
        return driverService.createNewDriver(newDriver);
    }

    //GET /api/driver
    @GetMapping()
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, Object> getAllDrivers(@RequestParam(value = "page", required = false, defaultValue = "0") int page, @RequestParam(value = "size", required = false, defaultValue = "0") int size) {
        return driverService.getAllDrivers();
    }

    //GET /api/driver/{id}
    @GetMapping("/{id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public RegisteredDriverDTO getDriverById(@PathVariable Long id) {
        return driverService.getById(id);
    }

    //PUT /api/driver/{id}
    @PutMapping("/{id}")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RegisteredDriverDTO updateDriver(@PathVariable Long id, @Valid @RequestBody CreateDriverDTO driverDTO) {
        return driverService.updateDriver(id, driverDTO);
    }

    //GET /api/driver/{id}/documents
    @GetMapping("/{id}/documents")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public List<DocumentDTO> getDocumentsByDriverId(@PathVariable Long id) {
        return driverService.getDocumentsByDriverId(id);
    }

    //POST /api/driver/{id}/documents
    @PostMapping("/{id}/documents")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public DocumentDTO createDriverDocuments(@PathVariable Long id, @Valid @RequestBody CreateDocumentDTO createDriver) {
        return driverService.addDriverDocument(id, createDriver);
    }

    //DELETE /api/driver/document/{document-id}
    @DeleteMapping("/document/{document-id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public ResponseEntity<String> deleteDriverDocuments(@PathVariable(name = "document-id") Long id) {
        driverService.deleteDriverDocument(id);
        return new ResponseEntity<>("Driver document deleted successfully", HttpStatus.NO_CONTENT);
    }

    //GET /api/driver/{id}/vehicle
    @GetMapping("/{id}/vehicle")
    @Valid
    public VehicleDTO getVehicleById(@PathVariable Long id) {
        return driverService.getVehicleById(id);
    }

    //POST /api/driver/{id}/vehicle
    @PostMapping("/{id}/vehicle")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public VehicleDTO addDriversVehicle(@PathVariable Long id, @Valid @RequestBody CreateVehicleDTO vehicle) {
        return driverService.addDriversVehicle(id, vehicle);
    }

    //PUT /api/driver/{id}/vehicle
    @PutMapping("/{id}/vehicle")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public VehicleDTO updateDriversVehicle(@PathVariable Long id, @Valid @RequestBody CreateVehicleDTO vehicle) {
        return driverService.updateDriversVehicle(id, vehicle);
    }

    //GET /api/driver/{id}/working-hour
    @GetMapping("/{id}/working-hour")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public Map<String, Object> getWorkingHoursById(@PathVariable Long id, @RequestParam(value = "page", required = false, defaultValue = "0") int page, @RequestParam(value = "size", required = false, defaultValue = "0") int size, @RequestParam(value = "from", required = false, defaultValue = "") String from, @RequestParam(value = "to", required = false, defaultValue = "") String to) {
        return calculateWorkingHoursService.getAllWorkingHoursByDriverId(id);
    }

    //POST /api/driver/{id}/working-hour
    @PostMapping("/{id}/working-hour")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public WorkingHoursDTO addDriversWorkingHours(@Valid @RequestBody WorkingHourStartDTO start, @PathVariable Long id) {
        return calculateWorkingHoursService.addDriversWorkingHours(id, start);
    }

    //GET /api/driver/{id}/ride
    @GetMapping(value = "/{id}/ride")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_ADMIN')")
    public DriversRideListDTO getDriverRides(@PathVariable Long id, @RequestParam(value = "page", required = false, defaultValue = "0") int page, @RequestParam(value = "size", required = false, defaultValue = "0") int size, @RequestParam(value = "from", required = false, defaultValue = "") String from, @RequestParam(value = "to", required = false, defaultValue = "") String to, @RequestParam(value = "sort", required = false, defaultValue = "") String sort) {
        return driverService.getRidesById(id, page, size, sort, from, to);
    }

    //GET /api/driver/working-hour/{working-hour-id}
    @GetMapping("/working-hour/{working-hour-id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public WorkingHoursDTO getWorkingHourById(@PathVariable("working-hour-id") Long working_hour_id) {
        return calculateWorkingHoursService.getWorkingHourById(working_hour_id);
    }

    //PUT /api/driver/working-hour/{working-hour-id}
    @PutMapping("/working-hour/{working-hour-id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public WorkingHoursDTO updateDriversWorkingHour(@PathVariable("working-hour-id") Long working_hour_id, @Valid @RequestBody WorkingHourEndDTO workingHoursDTO) {
        return calculateWorkingHoursService.updateDriversWorkingHour(working_hour_id, workingHoursDTO);
    }

    @GetMapping("/email={email}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public RegisteredDriverDTO getByEmail(@PathVariable String email) {
        return driverService.getByEmail(email);
    }

    @PutMapping("/{id}/{requestId}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RegisteredDriverDTO update(@PathVariable Long id, @PathVariable Long requestId, @RequestBody CreateDriverDTO driverDTO) {
        driverService.deleteRequest(requestId);
        return driverService.updateDriver(id, driverDTO);
    }

    @PutMapping("/{id}/activate")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public boolean activateDriver(@PathVariable Long id, @RequestParam("activityStatus") boolean activityStatus) {
        return driverService.changeDriverActivityStatus(id, activityStatus);
    }

    @DeleteMapping("rejectChanges/{id}")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteRequest(@PathVariable Long id) {
        driverService.deleteRequest(id);
    }

    @GetMapping("/changeRequests")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<DriverInfoRequestDTO> getAllRequests() {
        return driverService.getAllRequests();
    }

    @PostMapping("/requestChanges/{id}")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public DriverInfoRequest requestChanges(@PathVariable Long id, @Valid @RequestBody CreateDriverDTO driverDTO) {
        return driverService.requestChanges(id, driverDTO);
    }

    @GetMapping("/all-active-vehicles")
    public Collection<VehicleToDriveDTO> getAllActiveVehicles() {
        return driverService.getAllActiveVehicles();
    }

    @PutMapping("/block/{email}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RegisteredDriverDTO block(@PathVariable String email) {
        return driverService.block(email, true);
    }

    @PutMapping("/unblock/{email}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RegisteredDriverDTO unblock(@PathVariable String email) {
        return driverService.block(email, false);
    }

    @GetMapping("/{id}/workingTime")
    @Valid
    @IdentityCheck
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER')")
    public WorkingTimeDurationDTO getDriversWorkingTime(@PathVariable Long id) {
        return calculateWorkingHoursService.getDriverWorkingTime(id);
    }

    @GetMapping("/assignedRides")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public RideForTransferDTO getAssignedRide(Principal user) {
        return driverService.getFirstAssignedRideToDriver(user);
    }

    @GetMapping("/{id}/history-items")
    @IdentityCheck
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public AllHistoryItemsDTO getAllUserHistoryItemsByUserId(@PathVariable Long id, Pageable pageable, @RequestParam(value = "sort") String sort) {
        return driverService.getAllUserHistoryItemsByUserId(id, pageable, sort);
    }
}
