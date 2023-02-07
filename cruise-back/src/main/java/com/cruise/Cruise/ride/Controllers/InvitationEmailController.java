package com.cruise.Cruise.ride.Controllers;

import com.cruise.Cruise.models.Passenger;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.ride.DTO.AnswerEmailDTO;
import com.cruise.Cruise.ride.Services.IRideRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Controller
public class InvitationEmailController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private IRideRequestService rideRequestService;
    @Autowired
    private IPassengerRepository passengerRepository;

    @MessageMapping("/email-invitation")
    public void rideRequest(String answer) {
        AnswerEmailDTO answerEmailDTO;
        try {
            answerEmailDTO = objectMapper.readValue(answer, AnswerEmailDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        try {
            if (answerEmailDTO.getAnswer().equals("YES")) {
                rideRequestService.addPassengerToRide(answerEmailDTO.getReceiverPassengerEmail(), answerEmailDTO.getRideId());
            }
        } catch (ResponseStatusException e) {
            answerEmailDTO.setAnswer("NO");
        }
        rideRequestService.deleteRideInvitation(answerEmailDTO.getRideId(), answerEmailDTO.getReceiverPassengerEmail());
        Optional<Passenger> passengerOptional = passengerRepository.findByEmail(answerEmailDTO.getSenderPassengerEmail());
        if (passengerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger with that email doesn't exist");
        }
        Passenger passenger = passengerOptional.get();
        try {
            simpMessagingTemplate.convertAndSend("/socket-out-invite/" + passenger.getId(), objectMapper.writeValueAsString(answerEmailDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
