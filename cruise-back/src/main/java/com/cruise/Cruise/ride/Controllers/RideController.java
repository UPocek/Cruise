package com.cruise.Cruise.ride.Controllers;

import com.cruise.Cruise.driver.DTO.ReportDTO;
import com.cruise.Cruise.driver.DTO.ReportsDTO;
import com.cruise.Cruise.panic.DTO.PanicDTO;
import com.cruise.Cruise.passenger.DTO.EmailsDTO;
import com.cruise.Cruise.ride.DTO.*;
import com.cruise.Cruise.ride.Services.IReportService;
import com.cruise.Cruise.ride.Services.IRideRequestService;
import com.cruise.Cruise.ride.Services.IRideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collection;


@RestController
@RequestMapping("/api/ride")
public class RideController {
    @Autowired
    private IRideService rideService;
    @Autowired
    private IRideRequestService rideRequestService;
    @Autowired
    private IReportService reportService;

    @PostMapping
    @Valid
    public RideForTransferDTO createRide(@Valid @RequestBody RideDTO rideDTO) {
        return rideRequestService.createRideBasic(rideDTO);
    }

    @GetMapping("/driver/{driverId}/active")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_ADMIN')")
    public RideForTransferDTO getActiveRideForDriver(@PathVariable Long driverId) {
        return rideService.getRideForDriverByStatus(driverId, "ACTIVE");
    }

    @GetMapping("/passenger/{passengerId}/active")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER') || hasAuthority('ROLE_ADMIN')")
    @Valid
    public RideForTransferDTO getActiveRideForPassenger(@PathVariable Long passengerId) {
        return rideService.getRideForPassengerByStatus(passengerId, "ACTIVE");
    }

    @GetMapping("/driver/{driverId}/accepted")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_ADMIN')")
    public RideForTransferDTO getAcceptedRideForDriver(@PathVariable Long driverId) {
        return rideService.getRideForDriverByStatus(driverId, "ACCEPTED");
    }

    @GetMapping("/passenger/{passengerId}/accepted")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER') || hasAuthority('ROLE_ADMIN')")
    public RideForTransferDTO getAcceptedRideForPassenger(@PathVariable Long passengerId) {
        return rideService.getRideForPassengerByStatus(passengerId, "ACCEPTED");
    }

    @GetMapping(value = "/{id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public RideForTransferDTO getById(@PathVariable Long id) {
        return rideRequestService.getRideRequest(id);
    }

    @PutMapping(value = "/{id}/panic")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER') || hasAuthority('ROLE_DRIVER')")
    public PanicDTO panicRide(@PathVariable Long id, @Valid @RequestBody ReasonDTO reasonDTO, Principal user) {
        return rideService.panic(id, reasonDTO, user);
    }

    @PutMapping(value = "/{id}/accept")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public RideForTransferDTO acceptRide(@PathVariable Long id, Principal driver) {
        return rideService.acceptRide(id, driver);
    }

    @PutMapping(value = "/{id}/start")
    @Valid
    public RideForTransferDTO startRide(@PathVariable Long id) {
        return rideService.startRide(id);
    }

    @PutMapping(value = "/{id}/end")
    @Valid
    public RideForTransferDTO endRide(@PathVariable Long id) {
        return rideService.endRide(id);
    }

    @PutMapping(value = "/{id}/cancel")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public RideForTransferDTO cancelRideWithExplanation(@PathVariable Long id, @Valid @RequestBody ReasonDTO reason) {
        return rideService.cancelRideWithExplanation(id, reason);
    }

    @PutMapping(value = "/{id}/withdraw")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public RideForTransferDTO cancelExistingRide(@PathVariable Long id) {
        return rideService.cancelExistingRide(id);
    }

    @PostMapping("/invitation")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public void sendInvitations(@Valid @RequestBody EmailsDTO emails) {
        rideRequestService.sendInvitations(emails);
    }

    @GetMapping("/invitations/{passenger-id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public Collection<AnswerEmailDTO> getPassengerInvitations(@PathVariable("passenger-id") Long passengerId) {
        return rideRequestService.getPassengerInvites(passengerId);
    }

    @PutMapping(value = "/driver-on-address/{rideId}")
    @Valid
    public void driverArrivedToPickUpAddress(@PathVariable Long rideId) {
        rideService.notifyPassengersThatDriverArrivedToPickUpLocation(rideId);
        rideService.startRide(rideId);
    }

    @GetMapping("/reports/{id}/driver")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER')")
    public ReportsDTO getReportsByDriverId(@PathVariable Long id, @RequestParam(value = "from") String from,
                                           @RequestParam(value = "to") String to) {
        return reportService.getReports(id, from, to, "ROLE_DRIVER");
    }

    @GetMapping("/reports/{id}/passenger")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_PASSENGER')")
    public ReportsDTO getReportsByPassengerId(@PathVariable Long id, @RequestParam(value = "from") String from,
                                              @RequestParam(value = "to") String to) {
        return reportService.getReports(id, from, to, "ROLE_PASSENGER");
    }

    @GetMapping("/reports")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public ReportsDTO getAllReports(@RequestParam(value = "from") String from,
                                    @RequestParam(value = "to") String to) {
        return reportService.getAllReports(from, to);
    }

    @GetMapping("/reports/{email}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public ReportsDTO getReportsByEmail(@PathVariable String email, @RequestParam(value = "from") String from,
                                        @RequestParam(value = "to") String to) {
        return reportService.getUserReportsByEmail(email, from, to);
    }

    @GetMapping("/report/{id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public ReportDTO getUserReportsByType(@PathVariable Long id, @RequestParam(value = "from") String from,
                                          @RequestParam(value = "to") String to, @RequestParam(value = "role") String role, @RequestParam(value = "type") String type) {
        return reportService.getReportByType(id, from, to, role, type);
    }

    @PostMapping("/favourites")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public FavouriteRideDTO addNewFavouriteRide(@Valid @RequestBody FavouriteRideBasicDTO favouriteRide, Principal user) {
        return rideService.addNewFavouriteRide(favouriteRide, user);
    }

    @GetMapping("/favourites")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public Collection<FavouriteRideDTO> getAllPassengerFavouriteRides(Principal user) {
        return rideService.getAllFavouriteRidesByPassenger(user);
    }

    @DeleteMapping("/favourites/{id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public ResponseEntity<String> deleteFavouriteRide(@PathVariable Long id, Principal user) {
        rideService.deleteFavouriteRide(id, user);
        return new ResponseEntity<>("Successful deletion of favorite location!", HttpStatus.NO_CONTENT);
    }

}
