package com.cruise.Cruise.unregisteredUser.Controllers;

import com.cruise.Cruise.ride.DTO.RideEstimationDTO;
import com.cruise.Cruise.ride.DTO.RideRequestBasicDTO;
import com.cruise.Cruise.security.IdentityCheck;
import com.cruise.Cruise.unregisteredUser.DTO.DistanceEstimationDTO;
import com.cruise.Cruise.unregisteredUser.Services.IUnregisteredUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/unregisteredUser")
public class UnregisteredUserController {

    @Autowired
    IUnregisteredUserService service;

    @IdentityCheck()
    @PostMapping
    @Valid
    public RideEstimationDTO getRideEstimation(@Valid @RequestBody RideRequestBasicDTO rideRequestBasicDTO) {
        return service.getRideEstimation(rideRequestBasicDTO);
    }

    @PostMapping("/distance")
    @Valid
    public DistanceEstimationDTO getDistance(@Valid @RequestBody RideRequestBasicDTO rideRequestBasicDTO) {
        return service.getRideDistance(rideRequestBasicDTO);
    }

}
