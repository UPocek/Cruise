package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.driver.Services.NoDriverAvailableForRideException;
import com.cruise.Cruise.helper.IHelperService;
import com.cruise.Cruise.models.*;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.ride.DTO.*;
import com.cruise.Cruise.ride.Repositories.ILocationRepository;
import com.cruise.Cruise.ride.Repositories.IRideInvitationRepository;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.cruise.Cruise.ride.Repositories.IRoutesRepository;
import com.cruise.Cruise.vehicle.Repositories.IVehicleTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("hibernate")
public class RideRequestServiceTest {
    public static double RANDOM_PRICE = 200;
    private final Long EXISTING_PASSENGER_ID = 1L;
    private final String NOT_EXISTING_PASSENGER_EMAIL = "not_existing@gmail.com";
    private final String EXISTING_PASSENGER_EMAIL = "existing@gmail.com";
    private final double RANDOM_TIME_ESTIMATION = 10;
    private final LocalDateTime PAST_TIME = LocalDateTime.now().minusMinutes(6);
    private final LocalDateTime MORE_THEN_5_HOUR_IN_FUTURE = LocalDateTime.now().plusHours(5).plusMinutes(5);
    private final LocalDateTime FUTURE_RIDE_TIME = LocalDateTime.now().plusMinutes(16);
    private final LocalDateTime PENDING_RIDE_TIME = LocalDateTime.now();
    private final Long EXISTING_ROUTE_ID = 1L;
    private final double LATITUDE_FOR_EXISTING_ROUTE = 15;
    private final double LONGITUDE_FOR_EXISTING_ROUTE = 15;
    private final String ADDRESS_FOR_EXISTING_ROUTE = "Existing address";
    private final double DISTANCE_FOR_EXISTING_ROUTE = 15;
    private final double LATITUDE_FOR_NOT_EXISTING_ROUTE = 16;
    private final double LONGITUDE_FOR_NOT_EXISTING_ROUTE = 16;
    private final double DISTANCE_FOR_NOT_EXISTING_ROUTE = 16;
    private final String EXISTING_VEHICLE_TYPE = "STANDARD";
    private final Long EXISTING_RIDE_ID = 1L;
    private final Long NOT_EXISTING_RIDE_ID = 2L;
    private final String FUTURE_RIDE_STATE = "FUTURE";
    private final String PENDING_RIDE_STATE = "PENDING";
    private final String ACTIVE_RIDE_STATE = "ACTIVE";
    private final String ACCEPTED_RIDE_STATE = "ACCEPTED";
    private final String EXISTING_RIDE_STATE = "FINSHED";
    private final String INREVIEW_RIDE_STATE = "INREVIEW";
    @Autowired
    private IRideRequestService rideRequestService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private IHelperService helper;
    @MockBean
    private IFindDriverService findDriverService;
    @MockBean
    private IPassengerRepository passengerRepository;
    @MockBean
    private IRoutesRepository routesRepository;
    @MockBean
    private IRideRepository rideRepository;
    @MockBean
    private IVehicleTypeRepository vehicleTypeRepository;
    @MockBean
    private IRideInvitationRepository rideInvitationRepository;
    @MockBean
    private ILocationRepository locationRepository;
    @MockBean
    private IDriverRepository driverRepository;

    @Test
    public void shouldReturnRideForTransferDTOWithRejectedState() {
        Long existingRideId = 1L;
        RideForTransferDTO rideForTransferToReject = new RideForTransferDTO();
        rideForTransferToReject.setId(existingRideId);
        Driver driverForRide = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        Passenger passenger1 = new Passenger();
        passenger1.setId(1L);
        passenger1.setEmail("uros.pocek@gmail.com");
        Passenger passenger2 = new Passenger();
        passenger2.setId(2L);
        passenger2.setEmail("tamarailic11@gmail.com");
        Set<Passenger> passengersFromRide = new HashSet<>();
        passengersFromRide.add(passenger1);
        passengersFromRide.add(passenger2);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Location endLocation = new Location("Bulevar oslobodjenja 2", 45.265, 19.831);
        Set<Route> rideRoutes = new HashSet<>();
        Route route = new Route(1L, startLocation, endLocation, 806);
        rideRoutes.add(route);
        Rejection rejection = new Rejection(null, "Reason", driverForRide, LocalDateTime.now());
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Ride rideThatWillBeRejected = new Ride(existingRideId, LocalDateTime.parse("2022-12-23T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2022-12-23T23:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), 543, driverForRide, passengersFromRide, rideRoutes, 11, new HashSet<>(), "FINISHED", rejection, false, false, false, standard);
        Mockito.when(rideRepository.findById(existingRideId)).thenReturn(Optional.of(rideThatWillBeRejected));
        rideForTransferToReject = rideRequestService.markRideAsRejected(rideForTransferToReject);
        Assertions.assertEquals("REJECTED", rideForTransferToReject.getStatus());
        Mockito.verify(rideRepository, Mockito.times(1)).save(rideThatWillBeRejected);
    }

    //createRideBasic
    @Test
    public void shouldNotCreateRideInPastTime() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(PAST_TIME));
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.createRideBasic(ride);
        });
        assertThat(exception.getMessage()).contains("Can't request ride in past time");
    }

    @Test
    public void shouldNotCreateRideInFutureWhenStartTimeIsMoreThenPlusFiveHours() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(MORE_THEN_5_HOUR_IN_FUTURE));
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.createRideBasic(ride);
        });
        assertThat(exception.getMessage()).contains("Can't request ride more then 5 hours in future");
    }

    @Test
    public void shouldNotCreateRideInFutureWhenPassengerNotExisting() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(FUTURE_RIDE_TIME));
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO notExistingPassenger = new UserForRideDTO();
        notExistingPassenger.setEmail(NOT_EXISTING_PASSENGER_EMAIL);
        passengers.add(notExistingPassenger);
        ride.setPassengers(passengers);

        Mockito.when(passengerRepository.findByEmail(NOT_EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.createRideBasic(ride);
        });
        assertThat(exception.getMessage()).contains("Passenger doesn't exist");
    }

    @Test
    public void shouldNotCreateRideInFutureWhenPassengerAlreadyHasScheduledRide() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(FUTURE_RIDE_TIME));
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO notExistingPassenger = new UserForRideDTO();
        notExistingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(notExistingPassenger);
        ride.setPassengers(passengers);

        Passenger passenger = new Passenger();
        passenger.setId(EXISTING_PASSENGER_ID);
        passenger.setEmail(EXISTING_PASSENGER_EMAIL);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(passenger));
        Set<Ride> rides = new HashSet<>();
        rides.add(new Ride());
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACTIVE_RIDE_STATE, passenger)).thenReturn(rides);
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACCEPTED_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(INREVIEW_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(FUTURE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.createRideBasic(ride);
        });
        assertThat(exception.getMessage()).contains("Cannot create a ride while you have one already pending!");
    }

    @Test
    public void shouldCreateRideInFutureAndFindExistingRoute() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(FUTURE_RIDE_TIME));
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO notExistingPassenger = new UserForRideDTO();
        notExistingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(notExistingPassenger);
        ride.setPassengers(passengers);

        Passenger passenger = new Passenger();
        passenger.setId(EXISTING_PASSENGER_ID);
        passenger.setEmail(EXISTING_PASSENGER_EMAIL);

        LocationForRideDTO departureLocation = new LocationForRideDTO();
        departureLocation.setLatitude(LATITUDE_FOR_EXISTING_ROUTE);
        departureLocation.setLongitude(LONGITUDE_FOR_EXISTING_ROUTE);
        LocationForRideDTO destinationLocation = new LocationForRideDTO();
        destinationLocation.setLatitude(LATITUDE_FOR_EXISTING_ROUTE);
        destinationLocation.setLongitude(LONGITUDE_FOR_EXISTING_ROUTE);
        RouteForRideDTO routeForRide = new RouteForRideDTO(departureLocation, destinationLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(routeForRide);
        ride.setLocations(locations);
        ride.setDistance(DISTANCE_FOR_EXISTING_ROUTE);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setVehicleType(EXISTING_VEHICLE_TYPE);
        Route route = new Route();
        route.setId(EXISTING_ROUTE_ID);
        route.setStartLocation(new Location(ADDRESS_FOR_EXISTING_ROUTE, LATITUDE_FOR_EXISTING_ROUTE, LONGITUDE_FOR_EXISTING_ROUTE));
        route.setEndLocation(new Location(ADDRESS_FOR_EXISTING_ROUTE, LATITUDE_FOR_EXISTING_ROUTE, LONGITUDE_FOR_EXISTING_ROUTE));
        route.setDistance(DISTANCE_FOR_EXISTING_ROUTE);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(passenger));
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACTIVE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACCEPTED_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(INREVIEW_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(FUTURE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        VehicleType vehicleType = new VehicleType(EXISTING_VEHICLE_TYPE);
        Mockito.when(vehicleTypeRepository.findByName(EXISTING_VEHICLE_TYPE)).thenReturn(vehicleType);
        Mockito.when(routesRepository.findByStartLocationLongitudeAndStartLocationLatitudeAndEndLocationLongitudeAndEndLocationLatitude(departureLocation.getLongitude(), departureLocation.getLatitude(), destinationLocation.getLongitude(), destinationLocation.getLatitude()))
                .thenReturn(route);

        assertThat(rideRequestService.createRideBasic(ride).getStatus()).isEqualTo(FUTURE_RIDE_STATE);
        Mockito.verify(routesRepository, Mockito.times(0)).save(any());
        Mockito.verify(rideRepository, Mockito.times(1)).save(any(Ride.class));
    }

    @Test
    public void shouldCreateRideInFutureAndCreateNewRoute() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(FUTURE_RIDE_TIME));
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO existingPassenger = new UserForRideDTO();
        existingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(existingPassenger);
        ride.setPassengers(passengers);

        Passenger passenger = new Passenger();
        passenger.setId(EXISTING_PASSENGER_ID);
        passenger.setEmail(EXISTING_PASSENGER_EMAIL);

        LocationForRideDTO departureLocation = new LocationForRideDTO();
        departureLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        departureLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        LocationForRideDTO destinationLocation = new LocationForRideDTO();
        destinationLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        destinationLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        RouteForRideDTO routeForRide = new RouteForRideDTO(departureLocation, destinationLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(routeForRide);
        ride.setLocations(locations);
        ride.setDistance(DISTANCE_FOR_NOT_EXISTING_ROUTE);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setVehicleType(EXISTING_VEHICLE_TYPE);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(passenger));
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACTIVE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACCEPTED_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(INREVIEW_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(FUTURE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.save(any(Ride.class))).thenReturn(null);
        VehicleType vehicleType = new VehicleType(EXISTING_VEHICLE_TYPE);
        Mockito.when(vehicleTypeRepository.findByName(EXISTING_VEHICLE_TYPE)).thenReturn(vehicleType);
        Mockito.when(routesRepository.findByStartLocationLongitudeAndStartLocationLatitudeAndEndLocationLongitudeAndEndLocationLatitude(departureLocation.getLongitude(), departureLocation.getLatitude(), destinationLocation.getLongitude(), destinationLocation.getLatitude()))
                .thenReturn(null);

        assertThat(rideRequestService.createRideBasic(ride).getStatus()).isEqualTo(FUTURE_RIDE_STATE);
        Mockito.verify(routesRepository, Mockito.times(1)).save(any(Route.class));
        Mockito.verify(rideRepository, Mockito.times(1)).save(any(Ride.class));
    }

    @Test
    public void shouldNotCreatePendingRideWhenPassengerNotExisting() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(PENDING_RIDE_TIME));
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO notExistingPassenger = new UserForRideDTO();
        notExistingPassenger.setEmail(NOT_EXISTING_PASSENGER_EMAIL);
        passengers.add(notExistingPassenger);
        ride.setPassengers(passengers);

        Mockito.when(passengerRepository.findByEmail(NOT_EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.createRideBasic(ride);
        });
        assertThat(exception.getMessage()).contains("Passenger doesn't exist");
    }

    @Test
    public void shouldNotCreatePendingRideWhenPassengerAlreadyHasScheduledRide() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(PENDING_RIDE_TIME));
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO existingPassenger = new UserForRideDTO();
        existingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(existingPassenger);
        ride.setPassengers(passengers);

        Passenger passenger = new Passenger();
        passenger.setId(EXISTING_PASSENGER_ID);
        passenger.setEmail(EXISTING_PASSENGER_EMAIL);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(passenger));
        Set<Ride> rides = new HashSet<>();
        rides.add(new Ride());
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACTIVE_RIDE_STATE, passenger)).thenReturn(rides);
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACCEPTED_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(INREVIEW_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(FUTURE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.createRideBasic(ride);
        });
        assertThat(exception.getMessage()).contains("Cannot create a ride while you have one already pending!");
    }

    @Test
    public void shouldCreatePendingRideAndFindExistingRoute() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(PENDING_RIDE_TIME));
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO existingPassenger = new UserForRideDTO();
        existingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(existingPassenger);
        ride.setPassengers(passengers);

        Passenger passenger = new Passenger();
        passenger.setId(EXISTING_PASSENGER_ID);
        passenger.setEmail(EXISTING_PASSENGER_EMAIL);

        LocationForRideDTO departureLocation = new LocationForRideDTO();
        departureLocation.setLatitude(LATITUDE_FOR_EXISTING_ROUTE);
        departureLocation.setLongitude(LONGITUDE_FOR_EXISTING_ROUTE);
        LocationForRideDTO destinationLocation = new LocationForRideDTO();
        destinationLocation.setLatitude(LATITUDE_FOR_EXISTING_ROUTE);
        destinationLocation.setLongitude(LONGITUDE_FOR_EXISTING_ROUTE);
        RouteForRideDTO routeForRide = new RouteForRideDTO(departureLocation, destinationLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(routeForRide);
        ride.setLocations(locations);
        ride.setDistance(DISTANCE_FOR_EXISTING_ROUTE);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setVehicleType(EXISTING_VEHICLE_TYPE);
        Route route = new Route();
        route.setId(EXISTING_ROUTE_ID);
        route.setStartLocation(new Location(ADDRESS_FOR_EXISTING_ROUTE, LATITUDE_FOR_EXISTING_ROUTE, LONGITUDE_FOR_EXISTING_ROUTE));
        route.setEndLocation(new Location(ADDRESS_FOR_EXISTING_ROUTE, LATITUDE_FOR_EXISTING_ROUTE, LONGITUDE_FOR_EXISTING_ROUTE));
        route.setDistance(DISTANCE_FOR_EXISTING_ROUTE);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(passenger));
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACTIVE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACCEPTED_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(INREVIEW_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(FUTURE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        VehicleType vehicleType = new VehicleType(EXISTING_VEHICLE_TYPE);
        Mockito.when(vehicleTypeRepository.findByName(EXISTING_VEHICLE_TYPE)).thenReturn(vehicleType);
        Mockito.when(routesRepository.findByStartLocationLongitudeAndStartLocationLatitudeAndEndLocationLongitudeAndEndLocationLatitude(departureLocation.getLongitude(), departureLocation.getLatitude(), destinationLocation.getLongitude(), destinationLocation.getLatitude()))
                .thenReturn(route);

        assertThat(rideRequestService.createRideBasic(ride).getStatus()).isEqualTo(PENDING_RIDE_STATE);
        Mockito.verify(routesRepository, Mockito.times(0)).save(any());
        Mockito.verify(rideRepository, Mockito.times(1)).save(any(Ride.class));
    }

    @Test
    public void shouldCreatePendingRideAndCreateNewRoute() {
        RideDTO ride = new RideDTO();
        ride.setStartTime(String.valueOf(PENDING_RIDE_TIME));
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO existingPassenger = new UserForRideDTO();
        existingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(existingPassenger);
        ride.setPassengers(passengers);

        Passenger passenger = new Passenger();
        passenger.setId(EXISTING_PASSENGER_ID);
        passenger.setEmail(EXISTING_PASSENGER_EMAIL);

        LocationForRideDTO departureLocation = new LocationForRideDTO();
        departureLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        departureLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        LocationForRideDTO destinationLocation = new LocationForRideDTO();
        destinationLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        destinationLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        RouteForRideDTO routeForRide = new RouteForRideDTO(departureLocation, destinationLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(routeForRide);
        ride.setLocations(locations);
        ride.setDistance(DISTANCE_FOR_NOT_EXISTING_ROUTE);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setVehicleType(EXISTING_VEHICLE_TYPE);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(passenger));
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACTIVE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACCEPTED_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(INREVIEW_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(FUTURE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.save(any(Ride.class))).thenReturn(null);
        VehicleType vehicleType = new VehicleType(EXISTING_VEHICLE_TYPE);
        Mockito.when(vehicleTypeRepository.findByName(EXISTING_VEHICLE_TYPE)).thenReturn(vehicleType);
        Mockito.when(routesRepository.findByStartLocationLongitudeAndStartLocationLatitudeAndEndLocationLongitudeAndEndLocationLatitude(departureLocation.getLongitude(), departureLocation.getLatitude(), destinationLocation.getLongitude(), destinationLocation.getLatitude()))
                .thenReturn(null);

        assertThat(rideRequestService.createRideBasic(ride).getStatus()).isEqualTo(PENDING_RIDE_STATE);
        Mockito.verify(routesRepository, Mockito.times(1)).save(any(Route.class));
        Mockito.verify(rideRepository, Mockito.times(1)).save(any(Ride.class));
    }

    @Test
    public void shouldNotCreateRideForNowWhenPassengerNotExisting() {
        RideDTO ride = new RideDTO();
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO notExistingPassenger = new UserForRideDTO();
        notExistingPassenger.setEmail(NOT_EXISTING_PASSENGER_EMAIL);
        passengers.add(notExistingPassenger);
        ride.setPassengers(passengers);

        Mockito.when(passengerRepository.findByEmail(NOT_EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.createRideForNow(ride);
        });
        assertThat(exception.getMessage()).contains("Passenger doesn't exist");
    }

    @Test
    public void shouldNotCreateRideForNowWhenPassengerAlreadyHasScheduledRide() {
        RideDTO ride = new RideDTO();
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO existingPassenger = new UserForRideDTO();
        existingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(existingPassenger);
        ride.setPassengers(passengers);

        Passenger passenger = new Passenger();
        passenger.setId(EXISTING_PASSENGER_ID);
        passenger.setEmail(EXISTING_PASSENGER_EMAIL);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(passenger));
        Set<Ride> rides = new HashSet<>();
        rides.add(new Ride());
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACTIVE_RIDE_STATE, passenger)).thenReturn(rides);
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACCEPTED_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(INREVIEW_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(FUTURE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.createRideForNow(ride);
        });
        assertThat(exception.getMessage()).contains("Can't request ride because passenger is already in ride process");
    }

    @Test
    public void shouldCreateRideForNowAndFindExistingRoute() {
        RideDTO ride = new RideDTO();
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO existingPassenger = new UserForRideDTO();
        existingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(existingPassenger);
        ride.setPassengers(passengers);

        Passenger passenger = new Passenger();
        passenger.setId(EXISTING_PASSENGER_ID);
        passenger.setEmail(EXISTING_PASSENGER_EMAIL);

        LocationForRideDTO departureLocation = new LocationForRideDTO();
        departureLocation.setLatitude(LATITUDE_FOR_EXISTING_ROUTE);
        departureLocation.setLongitude(LONGITUDE_FOR_EXISTING_ROUTE);
        LocationForRideDTO destinationLocation = new LocationForRideDTO();
        destinationLocation.setLatitude(LATITUDE_FOR_EXISTING_ROUTE);
        destinationLocation.setLongitude(LONGITUDE_FOR_EXISTING_ROUTE);
        RouteForRideDTO routeForRide = new RouteForRideDTO(departureLocation, destinationLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(routeForRide);
        ride.setLocations(locations);
        ride.setDistance(DISTANCE_FOR_EXISTING_ROUTE);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setVehicleType(EXISTING_VEHICLE_TYPE);
        Route route = new Route();
        route.setId(EXISTING_ROUTE_ID);
        route.setStartLocation(new Location(ADDRESS_FOR_EXISTING_ROUTE, LATITUDE_FOR_EXISTING_ROUTE, LONGITUDE_FOR_EXISTING_ROUTE));
        route.setEndLocation(new Location(ADDRESS_FOR_EXISTING_ROUTE, LATITUDE_FOR_EXISTING_ROUTE, LONGITUDE_FOR_EXISTING_ROUTE));
        route.setDistance(DISTANCE_FOR_EXISTING_ROUTE);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(passenger));
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACTIVE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACCEPTED_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(INREVIEW_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(FUTURE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        VehicleType vehicleType = new VehicleType(EXISTING_VEHICLE_TYPE);
        Mockito.when(vehicleTypeRepository.findByName(EXISTING_VEHICLE_TYPE)).thenReturn(vehicleType);
        Mockito.when(routesRepository.findByStartLocationLongitudeAndStartLocationLatitudeAndEndLocationLongitudeAndEndLocationLatitude(departureLocation.getLongitude(), departureLocation.getLatitude(), destinationLocation.getLongitude(), destinationLocation.getLatitude()))
                .thenReturn(route);

        assertThat(rideRequestService.createRideForNow(ride).getStatus()).isEqualTo(INREVIEW_RIDE_STATE);
        Mockito.verify(routesRepository, Mockito.times(0)).save(any());
        Mockito.verify(rideRepository, Mockito.times(1)).save(any(Ride.class));
    }

    @Test
    public void shouldCreateRideForNowAndCreateNewRoute() {
        RideDTO ride = new RideDTO();
        ride.setTimeEstimation(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);

        List<UserForRideDTO> passengers = new ArrayList<>();
        UserForRideDTO existingPassenger = new UserForRideDTO();
        existingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(existingPassenger);
        ride.setPassengers(passengers);

        Passenger passenger = new Passenger();
        passenger.setId(EXISTING_PASSENGER_ID);
        passenger.setEmail(EXISTING_PASSENGER_EMAIL);

        LocationForRideDTO departureLocation = new LocationForRideDTO();
        departureLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        departureLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        LocationForRideDTO destinationLocation = new LocationForRideDTO();
        destinationLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        destinationLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        RouteForRideDTO routeForRide = new RouteForRideDTO(departureLocation, destinationLocation);
        List<RouteForRideDTO> locations = new ArrayList<>();
        locations.add(routeForRide);
        ride.setLocations(locations);
        ride.setDistance(DISTANCE_FOR_NOT_EXISTING_ROUTE);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setVehicleType(EXISTING_VEHICLE_TYPE);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(passenger));
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACTIVE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(ACCEPTED_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(INREVIEW_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.findByRideStateAndPassengers(FUTURE_RIDE_STATE, passenger)).thenReturn(new HashSet<>());
        Mockito.when(rideRepository.save(any(Ride.class))).thenReturn(null);
        VehicleType vehicleType = new VehicleType(EXISTING_VEHICLE_TYPE);
        Mockito.when(vehicleTypeRepository.findByName(EXISTING_VEHICLE_TYPE)).thenReturn(vehicleType);
        Mockito.when(routesRepository.findByStartLocationLongitudeAndStartLocationLatitudeAndEndLocationLongitudeAndEndLocationLatitude(departureLocation.getLongitude(), departureLocation.getLatitude(), destinationLocation.getLongitude(), destinationLocation.getLatitude()))
                .thenReturn(null);

        assertThat(rideRequestService.createRideForNow(ride).getStatus()).isEqualTo(INREVIEW_RIDE_STATE);
        Mockito.verify(routesRepository, Mockito.times(1)).save(any(Route.class));
        Mockito.verify(rideRepository, Mockito.times(1)).save(any(Ride.class));
    }

    @Test
    public void shouldNotReturnRideWithNotExistingId() {
        Mockito.when(rideRepository.findById(NOT_EXISTING_RIDE_ID)).thenReturn(Optional.empty());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.getRideRequest(NOT_EXISTING_RIDE_ID);
        });
        assertThat(exception.getMessage()).contains("Ride with that id doesn't exist");
    }

    @Test
    public void shouldReturnRideWithExistingId() {
        Ride ride = new Ride();
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(PENDING_RIDE_TIME);
        ride.setEndTime(PENDING_RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setEmail(EXISTING_PASSENGER_EMAIL);
        passengers.add(existingPassenger);
        ride.setPassengers(passengers);
        Location departureLocation = new Location();
        departureLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        departureLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        departureLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        Location destinationLocation = new Location();
        destinationLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        destinationLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        destinationLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        Route route = new Route();
        route.setStartLocation(departureLocation);
        route.setEndLocation(destinationLocation);
        route.setDistance(DISTANCE_FOR_NOT_EXISTING_ROUTE);
        Set<Route> locations = new HashSet<>();
        locations.add(route);
        ride.setRoutes(locations);
        ride.setBabyInVehicle(true);
        ride.setPetInVehicle(true);
        ride.setRideState(EXISTING_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));

        assertThat(rideRequestService.getRideRequest(EXISTING_RIDE_ID).getId()).isEqualTo(EXISTING_ROUTE_ID);
    }

    @Test
    public void shouldThrowForNonExistingRide() {
        Long nonExistingRideId = 123L;
        RideForTransferDTO rideForTransferToReject = new RideForTransferDTO();
        rideForTransferToReject.setId(nonExistingRideId);
        Mockito.when(rideRepository.findById(nonExistingRideId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exists"));
        Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.markRideAsRejected(rideForTransferToReject);
        });
    }

    @Test
    public void shouldReturnRideForTransferDTOWithAcceptedState() {
        Long existingRideId = 1L;
        RideForTransferDTO rideForTransferToAccept = new RideForTransferDTO();
        rideForTransferToAccept.setId(existingRideId);
        rideForTransferToAccept.setDriver(new UserForRideDTO(4L, "marko@gmail.com"));
        Driver driverForRide = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Location endLocation = new Location("Bulevar oslobodjenja 2", 45.265, 19.831);
        Vehicle driversVehicle = new Vehicle(1L, driverForRide, "Tesla", standard, "NS555UP", 4, startLocation, false, false, null);
        driverForRide.setVehicle(driversVehicle);
        Passenger passenger1 = new Passenger();
        passenger1.setId(1L);
        passenger1.setEmail("uros.pocek@gmail.com");
        Passenger passenger2 = new Passenger();
        passenger2.setId(2L);
        passenger2.setEmail("tamarailic11@gmail.com");
        Set<Passenger> passengersFromRide = new HashSet<>();
        passengersFromRide.add(passenger1);
        passengersFromRide.add(passenger2);
        Set<Route> rideRoutes = new HashSet<>();
        Route route = new Route(1L, startLocation, endLocation, 806);
        rideRoutes.add(route);
        Ride rideThatWillBeAccepted = new Ride(existingRideId, LocalDateTime.parse("2022-12-23T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2022-12-23T23:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), 543, driverForRide, passengersFromRide, rideRoutes, 11, new HashSet<>(), "FINISHED", null, false, false, false, standard);
        Mockito.when(rideRepository.findById(existingRideId)).thenReturn(Optional.of(rideThatWillBeAccepted));
        Mockito.when(driverRepository.findById(driverForRide.getId())).thenReturn(Optional.of(driverForRide));
        rideForTransferToAccept = rideRequestService.markRideAsAccepted(rideForTransferToAccept);
        Assertions.assertEquals("ACCEPTED", rideForTransferToAccept.getStatus());
        Assertions.assertEquals("INRIDE", driverForRide.getStatus());
        Mockito.verify(driverRepository, Mockito.times(1)).save(driverForRide);
        Mockito.verify(rideRepository, Mockito.times(1)).save(rideThatWillBeAccepted);
    }

    @Test
    public void shouldThrowForNonExistingRideWhenTryingToAccept() {
        Long nonExistingRideId = 123L;
        RideForTransferDTO rideForTransferToAccept = new RideForTransferDTO();
        rideForTransferToAccept.setId(nonExistingRideId);
        Mockito.when(rideRepository.findById(nonExistingRideId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exists"));
        Exception e = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.markRideAsRejected(rideForTransferToAccept);
        });
        Assertions.assertTrue(e.getMessage().contains("Ride with that id does not exists"));
    }

    @Test
    public void shouldThrowForRideWithInvalidDriverIdWhenTryingToAccept() {
        Long existingRideId = 1L;
        RideForTransferDTO rideForTransferToAccept = new RideForTransferDTO();
        rideForTransferToAccept.setId(existingRideId);
        rideForTransferToAccept.setDriver(new UserForRideDTO(123L, ""));
        Passenger passenger1 = new Passenger();
        passenger1.setId(1L);
        passenger1.setEmail("uros.pocek@gmail.com");
        Passenger passenger2 = new Passenger();
        passenger2.setId(2L);
        passenger2.setEmail("tamarailic11@gmail.com");
        Set<Passenger> passengersFromRide = new HashSet<>();
        passengersFromRide.add(passenger1);
        passengersFromRide.add(passenger2);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Location endLocation = new Location("Bulevar oslobodjenja 2", 45.265, 19.831);
        Set<Route> rideRoutes = new HashSet<>();
        Route route = new Route(1L, startLocation, endLocation, 806);
        rideRoutes.add(route);
        Ride rideThatWillBeAccepted = new Ride(existingRideId, LocalDateTime.parse("2022-12-23T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2022-12-23T23:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), 543, null, passengersFromRide, rideRoutes, 11, new HashSet<>(), "FINISHED", null, false, false, false, null);
        Mockito.when(rideRepository.findById(existingRideId)).thenReturn(Optional.of(rideThatWillBeAccepted));
        Mockito.when(driverRepository.findById(anyLong())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver with that id does not exists"));
        Exception e = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.markRideAsAccepted(rideForTransferToAccept);
        });
        Assertions.assertTrue(e.getMessage().contains("Driver with that id does not exists"));
    }

    @Test
    public void shouldAssignDriverToRide() throws NoDriverAvailableForRideException {
        RideForTransferDTO rideWithDriver = new RideForTransferDTO();
        rideWithDriver.setDriver(new UserForRideDTO(4L, "marko@gmail.com"));
        Driver driverForRide = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Vehicle driversVehicle = new Vehicle(1L, driverForRide, "Tesla", standard, "NS555UP", 4, startLocation, false, false, null);
        driverForRide.setVehicle(driversVehicle);
        Mockito.when(driverRepository.findById(4L)).thenReturn(Optional.of(driverForRide));
        Mockito.when(findDriverService.findDriverForRide(any(), anySet())).thenReturn(rideWithDriver);
        Ride rideToBeAssigned = new Ride();
        Assertions.assertTrue(rideRequestService.assignDriverToRide(rideToBeAssigned));
        Assertions.assertEquals("ACCEPTED", rideToBeAssigned.getRideState());
        Mockito.verify(rideRepository).save(rideToBeAssigned);
    }

    @Test
    public void shouldThrowWhenNoDriverAvailable() throws NoDriverAvailableForRideException {
        Mockito.when(findDriverService.findDriverForRide(any(), anySet())).thenThrow(NoDriverAvailableForRideException.class);
        Ride rideToBeAssigned = new Ride();
        Assertions.assertFalse(rideRequestService.assignDriverToRide(rideToBeAssigned));
        Mockito.verify(rideRepository, never()).save(any());
    }

    @Test
    public void shouldReturnRideForTransferDTOWithInReviewState() {
        Long existingRideId = 1L;
        RideForTransferDTO rideForTransfer = new RideForTransferDTO();
        rideForTransfer.setId(existingRideId);
        rideForTransfer.setDriver(new UserForRideDTO(4L, "marko@gmail.com"));
        Driver driverForRide = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Location endLocation = new Location("Bulevar oslobodjenja 2", 45.265, 19.831);
        Vehicle driversVehicle = new Vehicle(1L, driverForRide, "Tesla", standard, "NS555UP", 4, startLocation, false, false, null);
        driverForRide.setVehicle(driversVehicle);
        Passenger passenger1 = new Passenger();
        passenger1.setId(1L);
        passenger1.setEmail("uros.pocek@gmail.com");
        Passenger passenger2 = new Passenger();
        passenger2.setId(2L);
        passenger2.setEmail("tamarailic11@gmail.com");
        Set<Passenger> passengersFromRide = new HashSet<>();
        passengersFromRide.add(passenger1);
        passengersFromRide.add(passenger2);
        Set<Route> rideRoutes = new HashSet<>();
        Route route = new Route(1L, startLocation, endLocation, 806);
        rideRoutes.add(route);
        Ride rideThatWillBeProcessed = new Ride(existingRideId, LocalDateTime.parse("2022-12-23T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2022-12-23T23:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), 543, driverForRide, passengersFromRide, rideRoutes, 11, new HashSet<>(), "FINISHED", null, false, false, false, standard);
        Mockito.when(rideRepository.findById(existingRideId)).thenReturn(Optional.of(rideThatWillBeProcessed));
        Mockito.when(driverRepository.findById(driverForRide.getId())).thenReturn(Optional.of(driverForRide));
        rideForTransfer = rideRequestService.markRideAsInReview(rideForTransfer);
        Assertions.assertEquals("INREVIEW", rideForTransfer.getStatus());
        Mockito.verify(driverRepository, never()).save(any());
        Mockito.verify(rideRepository).save(rideThatWillBeProcessed);
    }

    @Test
    public void shouldThrowForNonExistingRideWhenTryingToInReview() {
        Long nonExistingRideId = 123L;
        RideForTransferDTO rideForTransferToReview = new RideForTransferDTO();
        rideForTransferToReview.setId(nonExistingRideId);
        Mockito.when(rideRepository.findById(nonExistingRideId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exists"));
        Exception e = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.markRideAsInReview(rideForTransferToReview);
        });
        Assertions.assertTrue(e.getMessage().contains("Ride with that id does not exists"));
    }

    @Test
    public void shouldThrowForRideWithInvalidDriverIdWhenTryingToInReview() {
        Long existingRideId = 1L;
        RideForTransferDTO rideForTransferToAccept = new RideForTransferDTO();
        rideForTransferToAccept.setId(existingRideId);
        rideForTransferToAccept.setDriver(new UserForRideDTO(123L, ""));
        Passenger passenger1 = new Passenger();
        passenger1.setId(1L);
        passenger1.setEmail("uros.pocek@gmail.com");
        Passenger passenger2 = new Passenger();
        passenger2.setId(2L);
        passenger2.setEmail("tamarailic11@gmail.com");
        Set<Passenger> passengersFromRide = new HashSet<>();
        passengersFromRide.add(passenger1);
        passengersFromRide.add(passenger2);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Location endLocation = new Location("Bulevar oslobodjenja 2", 45.265, 19.831);
        Set<Route> rideRoutes = new HashSet<>();
        Route route = new Route(1L, startLocation, endLocation, 806);
        rideRoutes.add(route);
        Ride rideThatWillBeReviewed = new Ride(existingRideId, LocalDateTime.parse("2022-12-23T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2022-12-23T23:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), 543, null, passengersFromRide, rideRoutes, 11, new HashSet<>(), "FINISHED", null, false, false, false, null);
        Mockito.when(rideRepository.findById(existingRideId)).thenReturn(Optional.of(rideThatWillBeReviewed));
        Mockito.when(driverRepository.findById(anyLong())).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver with that id does not exists"));
        Exception e = Assertions.assertThrows(ResponseStatusException.class, () -> {
            rideRequestService.markRideAsInReview(rideForTransferToAccept);
        });
        Assertions.assertTrue(e.getMessage().contains("Driver with that id does not exists"));
    }
}
