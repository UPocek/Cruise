package com.cruise.Cruise.passenger.Services;

import com.cruise.Cruise.passenger.DTO.PassengerDTO;
import com.cruise.Cruise.passenger.DTO.RegisteredPassengerDTO;

import java.util.Map;

public interface IPassengerService {

    RegisteredPassengerDTO registerPassenger(PassengerDTO user);

    Map<String, Object> getAllPassengers(int page, int size);

    void sendConfirmRegistrationEmail(RegisteredPassengerDTO newPassenger);

    void verifyUserRegistration(Long activationId);

    RegisteredPassengerDTO update(Long id, PassengerDTO passengerDTO);

    RegisteredPassengerDTO getRegisteredPassengerDTOById(Long id);

    RegisteredPassengerDTO getRegisteredPassengerDTOByEmail(String email);

    Map<String, Object> getAllPassengerRides(Long passengerId, String from, String to, String sort);

    RegisteredPassengerDTO changeBlockedStatus(String email, boolean block);

}
