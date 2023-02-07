package com.cruise.Cruise.passenger.Controllers;

import com.cruise.Cruise.passenger.DTO.PassengerDTO;
import com.cruise.Cruise.passenger.DTO.RegisteredPassengerDTO;
import com.cruise.Cruise.passenger.Services.IPassengerService;
import com.cruise.Cruise.security.IdentityCheck;
import com.cruise.Cruise.user.DTO.NoteBasicDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/passenger")
public class PassengerController {

    @Autowired
    private IPassengerService service;

    @PostMapping
    @Valid
    public RegisteredPassengerDTO registration(@Valid @RequestBody PassengerDTO passengerDTO) {
        RegisteredPassengerDTO newPassenger = service.registerPassenger(passengerDTO);
        service.sendConfirmRegistrationEmail(newPassenger);
        return newPassenger;
    }

    @GetMapping
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, Object> getAll(@RequestParam(value = "page", required = false, defaultValue = "-1") int page, @RequestParam(value = "size", required = false, defaultValue = "-1") int size) {
        return service.getAllPassengers(page, size);
    }

    @GetMapping(value = "/activate/{activationId}")
    @Valid
    public ResponseEntity<NoteBasicDTO> activation(@PathVariable Long activationId) {
        service.verifyUserRegistration(activationId);
        return new ResponseEntity<>(new NoteBasicDTO("Successful account activation!"), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @IdentityCheck
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER') || hasAuthority('ROLE_ADMIN')")
    public RegisteredPassengerDTO update(@PathVariable Long id, @Valid @RequestBody PassengerDTO passengerDTO) {
        return service.update(id, passengerDTO);
    }

    @PutMapping("/block/{email}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RegisteredPassengerDTO block(@PathVariable String email) {
        return service.changeBlockedStatus(email, true);
    }

    @PutMapping("/unblock/{email}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public RegisteredPassengerDTO unblock(@PathVariable String email) {
        return service.changeBlockedStatus(email, false);
    }

    @GetMapping("/{id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public RegisteredPassengerDTO getById(@PathVariable Long id) {
        return service.getRegisteredPassengerDTOById(id);
    }

    @GetMapping("/email={email}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_PASSENGER')")
    public RegisteredPassengerDTO getByEmail(@PathVariable String email) {
        return service.getRegisteredPassengerDTOByEmail(email);
    }

    @GetMapping("/{id}/ride")
    @IdentityCheck
    @Valid
    @PreAuthorize("hasAuthority('ROLE_ADMIN') || hasAuthority('ROLE_PASSENGER')")
    public Map<String, Object> getPassengerRides(@PathVariable Long id, @RequestParam(value = "page", required = false, defaultValue = "-1") int page, @RequestParam(value = "size", required = false, defaultValue = "-1") int size, @RequestParam(value = "sort", required = false, defaultValue = "") String sort, @RequestParam(value = "from", required = false, defaultValue = "") String from, @RequestParam(value = "to", required = false, defaultValue = "") String to) {
        return service.getAllPassengerRides(id, from, to, sort);
    }
}
