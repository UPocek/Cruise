package com.cruise.Cruise.ride.Controllers;

import com.cruise.Cruise.ride.DTO.*;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:mvc.sql")
@ActiveProfiles("hibernate")
public class RideRequestControllerTest {

    private final String mPassengerEmail = "uros.pocek@gmail.com";
    private final String mPassenger2Email = "tamarailic11@gmail.com";
    private final String mDriverEmail = "marko@gmail.com";
    private final Long mPassengerId = 1L;
    private final Long mPassenger2Id = 2L;
    private final Long mDriverId = 4L;
    private final Long mDriver2Id = 5L;
    private final Long idOfInReviewRide = 3L;
    @Value("${jwt.secret}")
    private String ourSecret;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;
    @SpyBean
    private IRideRepository rideRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldProcessPassengerRequestForNewRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassenger2Email)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassenger2Id)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        RideForTransferDTO passengerRideRequest = new RideForTransferDTO();
        LocationForRideDTO startLocation = new LocationForRideDTO("Bulevar Oslobodjenja 40", 45.2396, 19.8227);
        LocationForRideDTO endLocation = new LocationForRideDTO("Bulevar Oslobodjenja 50", 45.2496, 19.8327);
        RouteForRideDTO route = new RouteForRideDTO(startLocation, endLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(route);
        passengerRideRequest.setLocations(locations);
        UserForRideDTO passenger2 = new UserForRideDTO(mPassenger2Id, mPassenger2Email);
        List<UserForRideDTO> passengers = new ArrayList<>();
        passengers.add(passenger2);
        passengerRideRequest.setPassengers(passengers);
        passengerRideRequest.setEstimatedTimeInMinutes(11);
        passengerRideRequest.setTotalCost(573);
        passengerRideRequest.setVehicleType("STANDARD");
        passengerRideRequest.setPetTransport(false);
        passengerRideRequest.setBabyTransport(false);
        passengerRideRequest.setStartTime(LocalDateTime.now().toString());

        mockMvc.perform(post("/api/ride-request")
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .content(objectMapper.writeValueAsString(passengerRideRequest)))
                .andExpect(status().isOk());

        Mockito.verify(rideRepository, Mockito.atLeastOnce()).save(any());
        Mockito.verify(simpMessagingTemplate).convertAndSend(eq("/socket-out/" + mDriverId), (Object) any());
    }

    @Test
    public void shouldProcessDriversNoResponse() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        RideForTransferDTO driversResponse = new RideForTransferDTO();
        LocationForRideDTO startLocation = new LocationForRideDTO("Bulevar Oslobodjenja 40", 45.2396, 19.8227);
        LocationForRideDTO endLocation = new LocationForRideDTO("Bulevar Oslobodjenja 50", 45.2496, 19.8327);
        RouteForRideDTO route = new RouteForRideDTO(startLocation, endLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(route);
        driversResponse.setLocations(locations);
        UserForRideDTO passenger2 = new UserForRideDTO(mPassenger2Id, mPassenger2Email);
        List<UserForRideDTO> passengers = new ArrayList<>();
        passengers.add(passenger2);
        driversResponse.setPassengers(passengers);
        driversResponse.setEstimatedTimeInMinutes(11);
        driversResponse.setTotalCost(573);
        driversResponse.setVehicleType("STANDARD");
        driversResponse.setPetTransport(false);
        driversResponse.setBabyTransport(false);
        driversResponse.setStartTime(LocalDateTime.now().toString());
        RejectionForRideDTO rejection = new RejectionForRideDTO("Valid reason", LocalDateTime.now().toString());
        driversResponse.setRejection(rejection);
        UserForRideDTO driver = new UserForRideDTO(mDriverId, mDriverEmail);
        driversResponse.setDriver(driver);
        driversResponse.setId(idOfInReviewRide);

        mockMvc.perform(post("/api/ride-request")
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .content(objectMapper.writeValueAsString(driversResponse)))
                .andExpect(status().isOk());

        Mockito.verify(simpMessagingTemplate).convertAndSend(eq("/socket-out/" + mDriver2Id), (Object) any());
    }

    @Test
    public void shouldProcessDriversYesResponse() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        RideForTransferDTO driversResponse = new RideForTransferDTO();
        LocationForRideDTO startLocation = new LocationForRideDTO("Bulevar Oslobodjenja 40", 45.2396, 19.8227);
        LocationForRideDTO endLocation = new LocationForRideDTO("Bulevar Oslobodjenja 50", 45.2496, 19.8327);
        RouteForRideDTO route = new RouteForRideDTO(startLocation, endLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(route);
        driversResponse.setLocations(locations);
        UserForRideDTO passenger1 = new UserForRideDTO(mPassengerId, mPassengerEmail);
        List<UserForRideDTO> passengers = new ArrayList<>();
        passengers.add(passenger1);
        driversResponse.setPassengers(passengers);
        driversResponse.setEstimatedTimeInMinutes(11);
        driversResponse.setTotalCost(573);
        driversResponse.setVehicleType("STANDARD");
        driversResponse.setPetTransport(false);
        driversResponse.setBabyTransport(false);
        driversResponse.setStartTime(LocalDateTime.now().toString());
        UserForRideDTO driver = new UserForRideDTO(mDriverId, mDriverEmail);
        driversResponse.setDriver(driver);
        driversResponse.setId(idOfInReviewRide);

        mockMvc.perform(post("/api/ride-request")
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .content(objectMapper.writeValueAsString(driversResponse)))
                .andExpect(status().isOk());

        Mockito.verify(simpMessagingTemplate).convertAndSend(eq("/socket-out/" + mPassengerId), (Object) any());
    }

    @Test
    public void shouldRejectRideRequestIfUserNotLoggedIn() throws Exception {
        RideForTransferDTO driversResponse = new RideForTransferDTO();
        LocationForRideDTO startLocation = new LocationForRideDTO("Bulevar Oslobodjenja 40", 45.2396, 19.8227);
        LocationForRideDTO endLocation = new LocationForRideDTO("Bulevar Oslobodjenja 50", 45.2496, 19.8327);
        RouteForRideDTO route = new RouteForRideDTO(startLocation, endLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(route);
        driversResponse.setLocations(locations);
        UserForRideDTO passenger1 = new UserForRideDTO(mPassengerId, mPassengerEmail);
        List<UserForRideDTO> passengers = new ArrayList<>();
        passengers.add(passenger1);
        driversResponse.setPassengers(passengers);
        driversResponse.setEstimatedTimeInMinutes(11);
        driversResponse.setTotalCost(573);
        driversResponse.setVehicleType("STANDARD");
        driversResponse.setPetTransport(false);
        driversResponse.setBabyTransport(false);
        driversResponse.setStartTime(LocalDateTime.now().toString());
        UserForRideDTO driver = new UserForRideDTO(mDriverId, mDriverEmail);
        driversResponse.setDriver(driver);
        driversResponse.setId(idOfInReviewRide);

        mockMvc.perform(post("/api/ride-request")
                        .header("Content-Type", "application/json")
                        .content(objectMapper.writeValueAsString(driversResponse)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldNotProcessPassengerRequestForNewRideIfAlreadyRideInProgress() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        RideForTransferDTO passengerRideRequest = new RideForTransferDTO();
        LocationForRideDTO startLocation = new LocationForRideDTO("Bulevar Oslobodjenja 40", 45.2396, 19.8227);
        LocationForRideDTO endLocation = new LocationForRideDTO("Bulevar Oslobodjenja 50", 45.2496, 19.8327);
        RouteForRideDTO route = new RouteForRideDTO(startLocation, endLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(route);
        passengerRideRequest.setLocations(locations);
        UserForRideDTO passenger1 = new UserForRideDTO(mPassengerId, mPassengerEmail);
        List<UserForRideDTO> passengers = new ArrayList<>();
        passengers.add(passenger1);
        passengerRideRequest.setPassengers(passengers);
        passengerRideRequest.setEstimatedTimeInMinutes(11);
        passengerRideRequest.setTotalCost(573);
        passengerRideRequest.setVehicleType("STANDARD");
        passengerRideRequest.setPetTransport(false);
        passengerRideRequest.setBabyTransport(false);
        passengerRideRequest.setStartTime(LocalDateTime.now().toString());

        mockMvc.perform(post("/api/ride-request")
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json")
                        .content(objectMapper.writeValueAsString(passengerRideRequest)))
                .andExpect(status().isOk());

        Mockito.verify(rideRepository, Mockito.times(4)).save(any());
        Mockito.verify(simpMessagingTemplate).convertAndSend(eq("/socket-out/" + mPassengerId), (Object) any());
    }

}
