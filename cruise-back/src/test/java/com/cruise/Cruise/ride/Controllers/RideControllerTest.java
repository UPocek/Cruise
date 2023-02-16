package com.cruise.Cruise.ride.Controllers;

import com.cruise.Cruise.ride.DTO.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:mvc.sql")
@ActiveProfiles("hibernate")
public class RideControllerTest {

    private final String mPassengerEmail = "uros.pocek@gmail.com";
    private final Long mPassengerId = 1L;
    private final String mPassenger2Email = "tamarailic11@gmail.com";
    private final Long mPassenger2Id = 2L;
    private final String mPassenger4Email = "bojan@gmail.com";
    private final Long mPassenger4Id = 7L;
    private final String mAdminUsername = "admin";
    private final Long mAdminId = 1L;
    private final Long idOfActiveRide = 5L;
    private final Long idOfAcceptedRide = 6L;
    private final String mDriverEmail = "marko@gmail.com";
    private final String mDriver2Email = "mirko@gmail.com";
    private final Long mDriverId = 4L;
    private final Long mDriver2Id = 5L;
    private final Long idOfInReviewRide = 3L;
    private final Long idOfFinishedRide = 4L;
    private final Long idOfNonExistingRide = 123L;
    @Value("${jwt.secret}")
    private String ourSecret;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Test
    public void shouldReturnBadRequestWhenRideIdNotExisting() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(get("/api/ride/{id}", idOfNonExistingRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest()).andReturn();
        Assertions.assertEquals(result.getResponse().getErrorMessage(), "Ride with that id doesn't exist");
    }

    @Test
    public void shouldReturnUnauthorizedWhenJwtNotExisting() throws Exception {
        mockMvc.perform(get("/api/ride/{id}", idOfInReviewRide))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnRideWhenRideIdExisting() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(get("/api/ride/{id}", idOfInReviewRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(3)))
                .andExpect(jsonPath("$.startTime", Matchers.is("2023-01-25T22:52:52.038935")))
                .andExpect(jsonPath("$.endTime", Matchers.is("2023-01-25T23:52:52.038935")))
                .andExpect(jsonPath("$.totalCost", Matchers.is(543.0)))
                .andExpect(jsonPath("$.driver.id", Matchers.is(4)))
                .andExpect(jsonPath("$.estimatedTimeInMinutes", Matchers.is(11)))
                .andExpect(jsonPath("$.vehicleType", Matchers.is("STANDARD")))
                .andExpect(jsonPath("$.babyTransport", Matchers.is(false)))
                .andExpect(jsonPath("$.petTransport", Matchers.is(false)))
                .andExpect(jsonPath("$.rejection", Matchers.nullValue()))
                .andExpect(jsonPath("$.status", Matchers.is("INREVIEW")));
    }

    @Test
    public void shouldNotStartRideIfRideIdNotExisting() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/start", idOfNonExistingRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()).andReturn();
        Assertions.assertEquals(result.getResponse().getErrorMessage(), "Ride with that id does not exist");
    }

    @Test
    public void shouldNotStartRideIfRideStateNotAccepted() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/start", idOfFinishedRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest()).andReturn();
        Assertions.assertEquals(result.getResponse().getErrorMessage(), "Cannot start a ride that is not in status ACCEPTED!");

    }

    @Test
    public void shouldStartRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(put("/api/ride/{id}/start", idOfAcceptedRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(6)))
                .andExpect(jsonPath("$.startTime", Matchers.is("2023-01-25T22:52:52.038935")))
                .andExpect(jsonPath("$.endTime", Matchers.is("2023-01-25T23:52:52.038935")))
                .andExpect(jsonPath("$.totalCost", Matchers.is(543.0)))
                .andExpect(jsonPath("$.driver.id", Matchers.is(4)))
                .andExpect(jsonPath("$.estimatedTimeInMinutes", Matchers.is(11)))
                .andExpect(jsonPath("$.vehicleType", Matchers.is("STANDARD")))
                .andExpect(jsonPath("$.babyTransport", Matchers.is(false)))
                .andExpect(jsonPath("$.petTransport", Matchers.is(false)))
                .andExpect(jsonPath("$.rejection", Matchers.nullValue()))
                .andExpect(jsonPath("$.status", Matchers.is("ACTIVE")));
    }

    @Test
    public void shouldNotCancelRideWithExplanationRideIfRideIdNotExisting() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("some reason");
        MvcResult result = mockMvc.perform(put("/api/ride/{id}/cancel", idOfNonExistingRide)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(status().isNotFound()).andReturn();
        Assertions.assertEquals(result.getResponse().getErrorMessage(), "Ride does not exist!");
    }

    @Test
    public void shouldNotCancelRideWithExplanationIfRideStateNotAccepted() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("some reason");
        MvcResult result = mockMvc.perform(put("/api/ride/{id}/cancel", idOfFinishedRide)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(status().isBadRequest()).andReturn();
        Assertions.assertEquals(result.getResponse().getErrorMessage(), "Cannot cancel a ride that is not in status ACCEPTED!");
    }

    @Test
    public void shouldNotCancelRideWithExplanationIfRoleNotValid() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("some reason");
        mockMvc.perform(put("/api/ride/{id}/cancel", idOfAcceptedRide)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(status().isForbidden()).andReturn();
    }

    @Test
    public void shouldNotCancelRideWithExplanationIfNoJwt() throws Exception {
        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("some reason");
        mockMvc.perform(put("/api/ride/{id}/cancel", idOfAcceptedRide)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void shouldCancelRideWithExplanationRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("some reason");
        mockMvc.perform(put("/api/ride/{id}/cancel", idOfAcceptedRide)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(6)))
                .andExpect(jsonPath("$.startTime", Matchers.is("2023-01-25T22:52:52.038935")))
                .andExpect(jsonPath("$.totalCost", Matchers.is(543.0)))
                .andExpect(jsonPath("$.driver.id", Matchers.is(4)))
                .andExpect(jsonPath("$.estimatedTimeInMinutes", Matchers.is(11)))
                .andExpect(jsonPath("$.vehicleType", Matchers.is("STANDARD")))
                .andExpect(jsonPath("$.babyTransport", Matchers.is(false)))
                .andExpect(jsonPath("$.petTransport", Matchers.is(false)))
                .andExpect(jsonPath("$.rejection.reason", Matchers.is("some reason")))
                .andExpect(jsonPath("$.status", Matchers.is("REJECTED")));
    }

    @Test
    public void shouldNotCancelExistingRideIfRideIdNotExisting() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/withdraw", idOfNonExistingRide)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound()).andReturn();
        Assertions.assertEquals(result.getResponse().getErrorMessage(), "Ride does not exist!");
    }

    @Test
    public void shouldNotCancelExistingRideStateNotAccepted() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/withdraw", idOfFinishedRide)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest()).andReturn();
        Assertions.assertEquals(result.getResponse().getErrorMessage(), "Cannot withdraw from a ride that is not in status ACCEPTED!");
    }

    @Test
    public void shouldNotCancelExistingRideIfRoleNotValid() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(put("/api/ride/{id}/withdraw", idOfAcceptedRide)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden()).andReturn();
    }

    @Test
    public void shouldNotCancelExistingRideIfNoJwt() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/withdraw", idOfAcceptedRide))
                .andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    public void shouldCancelExistingRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(put("/api/ride/{id}/withdraw", idOfAcceptedRide)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", Matchers.is(6)))
                .andExpect(jsonPath("$.startTime", Matchers.is("2023-01-25T22:52:52.038935")))
                .andExpect(jsonPath("$.totalCost", Matchers.is(543.0)))
                .andExpect(jsonPath("$.driver.id", Matchers.is(4)))
                .andExpect(jsonPath("$.estimatedTimeInMinutes", Matchers.is(11)))
                .andExpect(jsonPath("$.vehicleType", Matchers.is("STANDARD")))
                .andExpect(jsonPath("$.babyTransport", Matchers.is(false)))
                .andExpect(jsonPath("$.petTransport", Matchers.is(false)))
                .andExpect(jsonPath("$.status", Matchers.is("CANCELED")));
    }

    @Test
    public void shouldAcceptRideAndAssignDriverToIt() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/accept", idOfInReviewRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", Matchers.is("ACCEPTED")))
                .andReturn();
        RideForTransferDTO rideThatWasAccepted = objectMapper.readValue(result.getResponse().getContentAsString(), RideForTransferDTO.class);
        Assertions.assertEquals(mDriverId, rideThatWasAccepted.getDriver().getId());
    }


    @Test
    public void shouldReturn404IfRideIdDoesNotExists() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/accept", idOfNonExistingRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("Ride does not exist!", result.getResponse().getErrorMessage());
    }

    @Test
    public void shouldReturn400IfRideNotInValidState() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/accept", idOfFinishedRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assertions.assertEquals("Cannot accept a ride that is not in status INREVIEW!", result.getResponse().getErrorMessage());
    }

    @Test
    public void shouldReturn401IfJwtIsMissing() throws Exception {
        mockMvc.perform(put("/api/ride/{id}/accept", idOfFinishedRide))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn403IfRequestedWithInvalidRole() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(put("/api/ride/{id}/accept", idOfFinishedRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldEndRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/end", idOfActiveRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status", Matchers.is("FINISHED")))
                .andReturn();
        RideForTransferDTO rideToEnd = objectMapper.readValue(result.getResponse().getContentAsString(), RideForTransferDTO.class);
        Assertions.assertEquals(LocalDateTime.parse(rideToEnd.getEndTime()).toEpochSecond(ZoneOffset.ofHours(1)), LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(1)), 10);

        Mockito.verify(simpMessagingTemplate).convertAndSend(anyString(), (Object) any());
    }

    @Test
    public void shouldReturn404IfRideToEndIdDoesNotExists() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/end", idOfNonExistingRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("Ride with that id does not exits!", result.getResponse().getErrorMessage());
    }

    @Test
    public void shouldReturn400IfRideToEndInInvalidState() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/end", idOfFinishedRide).header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assertions.assertEquals("Cannot end a ride that is not in status ACTIVE!", result.getResponse().getErrorMessage());
    }

    @Test
    public void shouldReturnAllUserFavouriteRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(get("/api/ride/favourites").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", Matchers.is(2)));
    }

    @Test
    public void shouldReturnEmptySetIfPassengerHasNoFavouriteRides() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassenger2Email)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassenger2Id)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(get("/api/ride/favourites").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()", Matchers.is(0)));
    }

    @Test
    public void shouldReturn401WhenRequestingFavouriteRidesIfJwtIsMissing() throws Exception {
        mockMvc.perform(get("/api/ride/favourites"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn403WhenRequestingFavouriteRidesIfRequestedWithInvalidRole() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(get("/api/ride/favourites").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnActiveRideWhenRequestingActiveRideForDriverWithActiveRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriverEmail)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriverId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(get("/api/ride/driver/{id}/active", mDriverId).header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.status", Matchers.is("ACTIVE")));
    }

    @Test
    public void shouldReturn404WhenRequestingActiveRideForDriverWithoutActiveRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriver2Email)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriver2Id)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(get("/api/ride/driver/{id}/active", mDriver2Id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("Active ride does not exist!", result.getResponse().getErrorMessage());

    }

    @Test
    public void shouldReturn401WhenRequestingActiveRideForDriverWithoutToken() throws Exception {
        mockMvc.perform(get("/api/ride/driver/{id}/active", mDriver2Id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn403WhenRequestingActiveRideForDriverWithInvalidRole() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassenger2Email)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassenger2Id)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(get("/api/ride/driver/{id}/active", mPassenger2Id).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnActiveRideWhenRequestingActiveRideForPassengerWithActiveRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(get("/api/ride/passenger/{id}/active", mPassengerId).header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.status", Matchers.is("ACTIVE")));
    }

    @Test
    public void shouldReturn404WhenRequestingActiveRideForPassengerWithoutActiveRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassenger2Email)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassenger2Id)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        MvcResult result = mockMvc.perform(get("/api/ride/passenger/{id}/active", mPassenger2Id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("Active ride does not exist!", result.getResponse().getErrorMessage());

    }

    @Test
    public void shouldReturn401WhenRequestingActiveRideForPassengerWithoutToken() throws Exception {
        mockMvc.perform(get("/api/ride/passenger/{id}/active", mPassengerId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn403WhenRequestingActiveRideForPassengerWithInvalidRole() throws Exception {
        String token = Jwts.builder()
                .setSubject(mDriver2Email)
                .claim("role", "ROLE_DRIVER")
                .claim("id", mDriver2Id)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(get("/api/ride/passenger/{id}/active", mDriver2Id).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturnPanicWithGoodRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        ReasonDTO reasonDTO = new ReasonDTO();
        String reason = "Mnogo panicim!";
        reasonDTO.setReason(reason);

        mockMvc.perform(put("/api/ride/{id}/panic", idOfActiveRide)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(jsonPath("$.user.email", Matchers.is(mPassengerEmail)))
                .andExpect(jsonPath("$.reason", Matchers.is(reason)))
                .andExpect(jsonPath("$.ride.status", Matchers.is("PANIC")));
    }

    @Test
    public void shouldReturn404ForPanicToNonExistingRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId).claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1))).signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        ReasonDTO reasonDTO = new ReasonDTO();
        String reason = "Mnogo panicim!";
        reasonDTO.setReason(reason);

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/panic", idOfNonExistingRide)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("Ride with that id does not exist", result.getResponse().getErrorMessage());


    }


    @Test
    public void shouldReturn404ForPanicForPassengerNotInRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassenger2Email)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassenger2Id).claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1))).signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        ReasonDTO reasonDTO = new ReasonDTO();
        String reason = "Mnogo panicim!";
        reasonDTO.setReason(reason);

        MvcResult result = mockMvc.perform(put("/api/ride/{id}/panic", idOfActiveRide)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("User is not in ride", result.getResponse().getErrorMessage());

    }

    @Test
    public void shouldReturn401ForPanicWithoutToken() throws Exception {
        ReasonDTO reasonDTO = new ReasonDTO();
        String reason = "Mnogo panicim!";
        reasonDTO.setReason(reason);

        mockMvc.perform(put("/api/ride/{id}/panic", idOfActiveRide)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn403ForPanicWithInvalidRole() throws Exception {
        String token = Jwts.builder()
                .setSubject(mAdminUsername)
                .claim("role", "ROLE_ADMIN")
                .claim("id", mAdminId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        ReasonDTO reasonDTO = new ReasonDTO();
        String reason = "Mnogo panicim!";
        reasonDTO.setReason(reason);

        mockMvc.perform(put("/api/ride/{id}/panic", idOfActiveRide)
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(reasonDTO)))
                .andExpect(status().isForbidden());
    }

    private FavouriteRideBasicDTO getFavouriteRideBasicDTO() {
        FavouriteRideBasicDTO favouriteRideBasicDTO = new FavouriteRideBasicDTO();

        favouriteRideBasicDTO.setFavoriteName("Moja omiljena ruta");

        LocationForRideDTO locationForRideDTO = new LocationForRideDTO();
        locationForRideDTO.setAddress("Neka adresa 1");
        locationForRideDTO.setLatitude(11.11);
        locationForRideDTO.setLongitude(22.22);

        LocationForRideDTO locationForRideDTO2 = new LocationForRideDTO();
        locationForRideDTO2.setAddress("Neka adresa 2");
        locationForRideDTO2.setLatitude(33.33);
        locationForRideDTO2.setLongitude(44.44);

        RouteForRideDTO routeForRideDTO = new RouteForRideDTO();
        routeForRideDTO.setDeparture(locationForRideDTO);
        routeForRideDTO.setDestination(locationForRideDTO2);

        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(routeForRideDTO);
        favouriteRideBasicDTO.setLocations(locations);

        UserForRideDTO userForRideDTO = new UserForRideDTO();
        userForRideDTO.setEmail(mPassengerEmail);
        userForRideDTO.setId(mPassengerId);

        List<UserForRideDTO> users = new ArrayList<>();
        users.add(userForRideDTO);
        favouriteRideBasicDTO.setPassengers(users);

        favouriteRideBasicDTO.setVehicleType("STANDARD");

        favouriteRideBasicDTO.setBabyTransport(false);

        favouriteRideBasicDTO.setPetTransport(true);

        favouriteRideBasicDTO.setDistance(100.3);

        return favouriteRideBasicDTO;
    }

    @Test
    public void shouldReturnFavouriteRideWhenCreatingFavouriteRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassengerEmail)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassengerId).claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1))).signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        FavouriteRideBasicDTO ride = getFavouriteRideBasicDTO();

        mockMvc.perform(post("/api/ride/favourites")
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(ride)))
                .andExpect(jsonPath("$.favoriteName", Matchers.is(ride.getFavoriteName())))
                .andExpect(jsonPath("$['locations'][0]['departure']['address']", Matchers.is(ride.getLocations().get(0).getDeparture().getAddress())))
                .andExpect(jsonPath("$['locations'][0]['departure']['longitude']", Matchers.is(ride.getLocations().get(0).getDeparture().getLongitude())))
                .andExpect(jsonPath("$['locations'][0]['departure']['latitude']", Matchers.is(ride.getLocations().get(0).getDeparture().getLatitude())))
                .andExpect(jsonPath("$['locations'][0]['destination']['address']", Matchers.is(ride.getLocations().get(0).getDestination().getAddress())))
                .andExpect(jsonPath("$['locations'][0]['destination']['longitude']", Matchers.is(ride.getLocations().get(0).getDestination().getLongitude())))
                .andExpect(jsonPath("$['locations'][0]['destination']['latitude']", Matchers.is(ride.getLocations().get(0).getDestination().getLatitude())))
                .andExpect(jsonPath("$['passengers'][0]['email']", Matchers.is(ride.getPassengers().get(0).getEmail())))
                .andExpect(jsonPath("$['passengers'][0]['id']", Matchers.is(Math.toIntExact(ride.getPassengers().get(0).getId()))))
                .andExpect(jsonPath("$['vehicleType']", Matchers.is(ride.getVehicleType())))
                .andExpect(jsonPath("$['babyTransport']", Matchers.is(ride.isBabyTransport())))
                .andExpect(jsonPath("$['petTransport']", Matchers.is(ride.isPetTransport())))
                .andExpect(jsonPath("$['distance']", Matchers.is(ride.getDistance())));
    }

    @Test
    public void shouldReturn400WhenCreatingFavouriteRideIfThereAreMoreThan10Rides() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassenger4Email)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassenger4Id).claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1))).signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        FavouriteRideBasicDTO ride = getFavouriteRideBasicDTO();

        MvcResult result = mockMvc.perform(post("/api/ride/favourites")
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(ride)))
                .andExpect(status().isBadRequest())
                .andReturn();
        Assertions.assertEquals("Number of favorite rides cannot exceed 10!", result.getResponse().getErrorMessage());
    }

    @Test
    public void shouldReturn401WhenCreatingRideWithoutToken() throws Exception {
        mockMvc.perform(post("/api/ride/favourites")
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(getFavouriteRideBasicDTO())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn403WhenCreatingRideInvalidRole() throws Exception {
        String token = Jwts.builder()
                .setSubject(mAdminUsername)
                .claim("role", "ROLE_ADMIN")
                .claim("id", mAdminId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();

        mockMvc.perform(post("/api/ride/favourites")
                        .header("Authorization", "Bearer " + token)
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .content(objectMapper.writeValueAsString(getFavouriteRideBasicDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void shouldReturn204WhenDeletingFavouriteRide() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassenger4Email)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassenger4Id).claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1))).signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();


        MvcResult result = mockMvc.perform(delete("/api/ride/favourites/{id}", 5L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent())
                .andReturn();
        Assertions.assertEquals("Successful deletion of favorite location!", result.getResponse().getContentAsString());
    }

    @Test
    public void shouldReturn404WhenDeletingFavouriteRideRideNotExist() throws Exception {
        String token = Jwts.builder()
                .setSubject(mPassenger4Email)
                .claim("role", "ROLE_PASSENGER")
                .claim("id", mPassenger4Id).claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1))).signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();


        MvcResult result = mockMvc.perform(delete("/api/ride/favourites/{id}", 55555L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("Favorite location does not exist!", result.getResponse().getErrorMessage());
    }

    @Test
    public void shouldReturn401WhenDeletingFavouriteRideWithoutToken() throws Exception {
        mockMvc.perform(delete("/api/ride/favourites/{id}", 5L))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturn403WhenDeletingFavouriteRideInvalidRole() throws Exception {
        String token = Jwts.builder()
                .setSubject(mAdminUsername)
                .claim("role", "ROLE_ADMIN")
                .claim("id", mAdminId)
                .claim("exp", LocalDateTime.now().plusHours(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .claim("iat", LocalDateTime.now().minusMinutes(5).toEpochSecond(ZoneOffset.ofHours(1)))
                .signWith(SignatureAlgorithm.HS256, ourSecret)
                .compact();


        mockMvc.perform(delete("/api/ride/favourites/{id}", 5L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andReturn();
    }


}
