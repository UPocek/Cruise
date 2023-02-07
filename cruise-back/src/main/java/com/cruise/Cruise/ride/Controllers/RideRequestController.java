package com.cruise.Cruise.ride.Controllers;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.driver.Services.NoDriverAvailableForRideException;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.ride.DTO.RejectionForRideDTO;
import com.cruise.Cruise.ride.DTO.RideDTO;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;
import com.cruise.Cruise.ride.DTO.UserForRideDTO;
import com.cruise.Cruise.ride.Services.IFindDriverService;
import com.cruise.Cruise.ride.Services.IRideRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
public class RideRequestController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private IRideRequestService rideRequestService;
    @Autowired
    private IFindDriverService findDriverService;
    @Autowired
    private IDriverRepository driverRepository;

    private Map<Long, Set<Driver>> driversThatRejectedRidesWithRideId = new HashMap<>();


    @MessageMapping("/ride-request")
    public void rideRequest(String message) {
        RideForTransferDTO rideDTO;
        try {
            rideDTO = objectMapper.readValue(message, RideForTransferDTO.class);
            processRideRequest(rideDTO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/api/ride-request")
    @PreAuthorize("hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public void rideRequestREST(@RequestBody RideForTransferDTO rideDTO) {
        processRideRequest(rideDTO);
    }

    private void processRideRequest(RideForTransferDTO rideDTO) {
        try {
            if (rideDTO.getDriver() == null) {
                processInitialPassengerRequest(rideDTO);
            } else if (rideDTO.getRejection() == null) {
                processDriverAcceptedRideRequest(rideDTO);
            } else {
                processDriverRejectedRideRequest(rideDTO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processInitialPassengerRequest(RideForTransferDTO rideDTO) throws IOException {
        try {
            if (isRideWithFriends(rideDTO)) {
                rideDTO = rideRequestService.getRideRequest(rideDTO.getId());
            } else {
                rideDTO = rideRequestService.createRideForNow(new RideDTO(rideDTO));
            }
            try {
                notifyNewPotentialDriver(rideDTO);
            } catch (NoDriverAvailableForRideException e) {
                finishRideRequestAsRejected(rideDTO);
            }
        } catch (ResponseStatusException e) {
            rideDTO.setStatus("FORBIDDEN");
            notifyPassengersForRequestResult(rideDTO);
        }
    }

    private void processDriverAcceptedRideRequest(RideForTransferDTO rideDTO) throws IOException {
        finishRideRequestAsAccepted(rideDTO);
        driversThatRejectedRidesWithRideId.remove(rideDTO.getId());
    }

    private void processDriverRejectedRideRequest(RideForTransferDTO rideDTO) throws IOException {
        Driver driverWhoRejectedRide = driverRepository.findByEmail(rideDTO.getDriver().getEmail()).get();
        if (driversThatRejectedRidesWithRideId.containsKey(rideDTO.getId())) {
            driversThatRejectedRidesWithRideId.get(rideDTO.getId()).add(driverWhoRejectedRide);
        } else {
            Set<Driver> driverThatRejectedThisRide = new HashSet<>();
            driverThatRejectedThisRide.add(driverWhoRejectedRide);
            driversThatRejectedRidesWithRideId.put(rideDTO.getId(), driverThatRejectedThisRide);
        }
        rideDTO.setRejection(new RejectionForRideDTO());
        try {
            notifyNewPotentialDriver(rideDTO);
        } catch (NoDriverAvailableForRideException e) {
            driversThatRejectedRidesWithRideId.remove(rideDTO.getId());
            finishRideRequestAsRejected(rideDTO);
        }
    }

    private void finishRideRequestAsRejected(RideForTransferDTO rideDTO) throws IOException {
        rideDTO = rideRequestService.markRideAsRejected(rideDTO);
        notifyPassengersForRequestResult(rideDTO);
    }

    private void finishRideRequestAsAccepted(RideForTransferDTO rideDTO) throws IOException {
        rideDTO = rideRequestService.markRideAsAccepted(rideDTO);
        notifyPassengersForRequestResult(rideDTO);
    }

    private void notifyPassengersForRequestResult(RideForTransferDTO rideDTO) throws IOException {
        for (UserForRideDTO passenger : rideDTO.getPassengers()) {
            if (AndroidRideRequestController.passengerSessions.containsKey(passenger.getId())) {
                WebSocketSession passengersInRideSession = AndroidRideRequestController.passengerSessions.get(passenger.getId());
                passengersInRideSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(rideDTO)));
            } else {
                simpMessagingTemplate.convertAndSend("/socket-out/" + passenger.getId(), objectMapper.writeValueAsString(rideDTO));
            }
        }
    }

    private void notifyNewPotentialDriver(RideForTransferDTO rideDTO) throws NoDriverAvailableForRideException, IOException {
        rideDTO = findDriverService.findDriverForRide(rideDTO, driversThatRejectedRidesWithRideId.get(rideDTO.getId()) != null ? driversThatRejectedRidesWithRideId.get(rideDTO.getId()) : new HashSet<>());
        rideRequestService.markRideAsInReview(rideDTO);
        if (AndroidRideRequestController.driverSessions.containsKey(rideDTO.getDriver().getId())) {
            WebSocketSession driversSelectedForRideSession = AndroidRideRequestController.driverSessions.get(rideDTO.getDriver().getId());
            driversSelectedForRideSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(rideDTO)));
        } else {
            simpMessagingTemplate.convertAndSend("/socket-out/" + rideDTO.getDriver().getId(), objectMapper.writeValueAsString(rideDTO));
        }
    }

    private boolean isRideWithFriends(RideForTransferDTO rideDTO) {
        try {
            return rideDTO.getId() != -1;
        } catch (Exception e) {
            return false;
        }
    }
}
