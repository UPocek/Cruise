package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.helper.IHelperService;
import com.cruise.Cruise.models.*;
import com.cruise.Cruise.panic.DTO.PanicDTO;
import com.cruise.Cruise.panic.Repositories.IPanicRepository;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.ride.DTO.*;
import com.cruise.Cruise.ride.Repositories.IFavouriteRideRepository;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.cruise.Cruise.user.Repositories.IUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Copy;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.data.domain.Sort.by;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("hibernate")
public class RideServiceTest {
    private final String ADDRESS_FOR_EXISTING_ROUTE = "Existing address";
    private final double LATITUDE_FOR_NOT_EXISTING_ROUTE = 16;
    private final double LONGITUDE_FOR_NOT_EXISTING_ROUTE = 16;
    private final double DISTANCE_FOR_NOT_EXISTING_ROUTE = 16;
    private final String EXISTING_VEHICLE_TYPE = "STANDARD";
    private final Long EXISTING_DRIVER_ID = 1L;
    private final Long EXISTING_VEHICLE_ID = 1L;
    private final Long EXISTING_RIDE_ID = 1L;
    private final Long NOT_EXISTING_RIDE_ID = -1L;
    private final String EXISTING_DRIVER_EMAIL = "existing_driver@gmail.com";
    private final String NOT_EXISTING_DRIVER_EMAIL = "not_existing_driver@gmail.com";
    private final String EXISTING_PASSENGER_EMAIL = "existing@gmail.com";
    private final Long EXISTING_PASSENGER_ID = 11L;
    private final Long NOT_EXISTING_PASSENGER_ID = 12L;
    private final String NOT_EXISTING_PASSENGER_EMAIL = "not_existing_passenger@gmail.com";
    private final double RANDOM_TIME_ESTIMATION = 10;
    private final double RANDOM_PRICE = 200;
    private final LocalDateTime RIDE_TIME = LocalDateTime.now();
    private final String INREVIEW_RIDE_STATE = "INREVIEW";
    private final String ACCEPTED_RIDE_STATE = "ACCEPTED";
    private final String ACTIVE_RIDE_STATE = "ACTIVE";
    private final String FINISHED_RIDE_STATE = "FINISHED";
    private final String REJECTED_RIDE_STATE = "REJECTED";
    private final String CANCELED_RIDE_STATE = "CANCELED";
    @Autowired
    private IRideService rideService;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private IHelperService helper;
    @MockBean
    private IRideRequestService rideRequestService;
    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;
    @MockBean
    private IRideRepository rideRepository;
    @MockBean
    private IPassengerRepository passengerRepository;
    @MockBean
    private IDriverRepository driverRepository;
    @MockBean
    private IUserRepository userRepository;
    @MockBean
    private IPanicRepository panicRepository;
    @MockBean
    private IFavouriteRideRepository favouriteRideRepository;

    @Test
    public void shouldNotAcceptRideIfRideIdNotExisting(){
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(rideRepository.findById(NOT_EXISTING_RIDE_ID)).thenReturn(Optional.empty());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.acceptRide(NOT_EXISTING_RIDE_ID, mockPrincipal);
        });
        assertThat(exception.getMessage()).contains("Ride does not exist!");
    }
    @Test
    public void shouldNotAcceptRideIfRideStateNotInReview(){
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Ride ride = new Ride();
        ride.setRideState(ACCEPTED_RIDE_STATE);
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.acceptRide(EXISTING_RIDE_ID, mockPrincipal);
        });
        assertThat(exception.getMessage()).contains("Cannot accept a ride that is not in status INREVIEW!");
    }
    @Test
    public void shouldNotAcceptRideIfDriverNotExisting(){
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(NOT_EXISTING_DRIVER_EMAIL);
        Ride ride = new Ride();
        ride.setRideState(INREVIEW_RIDE_STATE);
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));
        Mockito.when(driverRepository.findByEmail(NOT_EXISTING_DRIVER_EMAIL)).thenReturn(Optional.empty());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.acceptRide(EXISTING_RIDE_ID, mockPrincipal);
        });
        assertThat(exception.getMessage()).contains("Driver does not exist!");
    }
    @Test
    public void shouldAcceptRide(){
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_DRIVER_EMAIL);
        Driver driver = new Driver();

        Ride ride = new Ride();
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
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
        ride.setRideState(INREVIEW_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));
        Mockito.when(driverRepository.findByEmail(EXISTING_DRIVER_EMAIL)).thenReturn(Optional.of(driver));

        assertThat(rideService.acceptRide(EXISTING_RIDE_ID,mockPrincipal).getStatus()).isEqualTo(ACCEPTED_RIDE_STATE);
        Mockito.verify(rideRepository, Mockito.times(1)).save(any(Ride.class));
        Mockito.verify(driverRepository, Mockito.times(1)).save(any(Driver.class));
    }
    @Test
    public void shouldNotStartRideIfRideIdNotExisting(){
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.startRide(NOT_EXISTING_RIDE_ID);
        });
        assertThat(exception.getMessage()).contains("Ride with that id does not exist");
    }
    @Test
    public void shouldNotStartRideIfRideStateNotAccepted(){
        Ride ride = new Ride();
        ride.setRideState(INREVIEW_RIDE_STATE);
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.startRide(EXISTING_RIDE_ID);
        });
        assertThat(exception.getMessage()).contains("Cannot start a ride that is not in status ACCEPTED!");
    }
    @Test
    public void shouldStartRide(){
        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
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
        ride.setRideState(ACCEPTED_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));

        assertThat(rideService.startRide(EXISTING_RIDE_ID).getStatus()).isEqualTo(ACTIVE_RIDE_STATE);
        Mockito.verify(rideRepository, Mockito.times(1)).save(any(Ride.class));
    }

    @Test
    public void shoudNotPanicNoRide() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("Mnogo jako panicim!");
        Mockito.when(rideRepository.findById(NOT_EXISTING_RIDE_ID)).thenReturn(Optional.empty());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.panic(NOT_EXISTING_RIDE_ID, reasonDTO, mockPrincipal);
        });
        assertThat(exception.getMessage()).contains("Ride with that id does not exist");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void shoudNotPanicUserNotInRide() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(NOT_EXISTING_PASSENGER_EMAIL);

        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("Mnogo jako panicim!");

        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(1L);
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
        ride.setRideState(ACCEPTED_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));


        Passenger notExistingPassenger = new Passenger();
        notExistingPassenger.setId(1234L);
        existingPassenger.setEmail(NOT_EXISTING_PASSENGER_EMAIL);

        Mockito.when(userRepository.findByEmail(NOT_EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(notExistingPassenger));
        Mockito.when(userRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(existingPassenger));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.panic(EXISTING_RIDE_ID, reasonDTO, mockPrincipal);
        });

        assertThat(exception.getMessage()).contains("User is not in ride");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void shoudPanic() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_PASSENGER_EMAIL);

        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("Mnogo jako panicim!");

        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(1L);
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
        ride.setRideState(ACCEPTED_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));


        Mockito.when(userRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(existingPassenger));

        PanicDTO panic = rideService.panic(EXISTING_RIDE_ID, reasonDTO, mockPrincipal);
        assertThat(Objects.equals(panic.getRide().getStatus(), "PANIC"));
        assertThat(Objects.equals(panic.getReason(), reasonDTO.getReason()));
    }

    @Test
    public void shouldNotEndRideNoRide() throws Exception {
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.endRide(NOT_EXISTING_RIDE_ID);
        });
        assertThat(exception.getMessage()).contains("Ride with that id does not exits!");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldNotEndRideNotActiveRide() throws Exception {
        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(1L);
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
        ride.setRideState(ACCEPTED_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.endRide(EXISTING_RIDE_ID);
        });
        assertThat(exception.getMessage()).contains("Cannot end a ride that is not in status ACTIVE!");
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldEndRide() throws Exception {
        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(1L);
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
        ride.setRideState(ACTIVE_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));

        RideForTransferDTO endedRide = rideService.endRide(EXISTING_RIDE_ID);
        assertEquals(endedRide.getStatus(), FINISHED_RIDE_STATE);
    }

    @Test
    public void shouldNotCancelRideWithExplanationNoRide() throws Exception {
        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("Imam mnogo dobar razlog majke mi!");
        Mockito.when(rideRepository.findById(NOT_EXISTING_RIDE_ID)).thenReturn(Optional.empty());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.cancelRideWithExplanation(NOT_EXISTING_RIDE_ID, reasonDTO);
        });
        assertThat(exception.getMessage()).contains("Ride does not exist!");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldNotCancelRideWithExplanationNotAcceptedRide() throws Exception {
        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("Imam mnogo dobar razlog majke mi!");
        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(1L);
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
        ride.setRideState(FINISHED_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.cancelRideWithExplanation(EXISTING_RIDE_ID, reasonDTO);
        });
        assertThat(exception.getMessage()).contains("Cannot cancel a ride that is not in status ACCEPTED!");
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldCancelRideWithExplanation() throws Exception {
        ReasonDTO reasonDTO = new ReasonDTO();
        reasonDTO.setReason("Imam mnogo dobar razlog majke mi!");
        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(1L);
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
        ride.setRideState(ACCEPTED_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));

        RideForTransferDTO endedRide = rideService.cancelRideWithExplanation(EXISTING_RIDE_ID, reasonDTO);
        assertEquals(endedRide.getStatus(), REJECTED_RIDE_STATE);
        assertEquals(endedRide.getRejection().getReason(), reasonDTO.getReason());
    }


    @Test
    public void shouldNotCancelExistingRideNoRide() throws Exception {
        Mockito.when(rideRepository.findById(NOT_EXISTING_RIDE_ID)).thenReturn(Optional.empty());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.cancelExistingRide(NOT_EXISTING_RIDE_ID);
        });
        assertThat(exception.getMessage()).contains("Ride does not exist!");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldNotCancelExistingRideNotAcceptedRide() throws Exception {
        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(1L);
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
        ride.setRideState(FINISHED_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.cancelExistingRide(EXISTING_RIDE_ID);
        });
        assertThat(exception.getMessage()).contains("Cannot withdraw from a ride that is not in status ACCEPTED!");
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldCancelExistingRide() throws Exception {
        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(1L);
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
        ride.setRideState(ACCEPTED_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));
        Mockito.when(rideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));

        RideForTransferDTO endedRide = rideService.cancelExistingRide(EXISTING_RIDE_ID);
        assertEquals(endedRide.getStatus(), CANCELED_RIDE_STATE);
    }

    @Test
    public void shouldNotGetRideForDriverByStatusNoRide() throws Exception {
        Mockito.when(rideRepository.findByRideStateAndDriverId(FINISHED_RIDE_STATE, EXISTING_DRIVER_ID, by("startTime"))).thenReturn(new HashSet<Ride>());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.getRideForDriverByStatus(EXISTING_DRIVER_ID, FINISHED_RIDE_STATE);
        });
        assertThat(exception.getMessage()).contains("Active ride does not exist!");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldGetRideForDriverByStatus() throws Exception {
        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(1L);
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
        ride.setRideState(ACTIVE_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));

        Set<Ride> rides = new HashSet<>();
        rides.add(ride);

        Mockito.when(rideRepository.findByRideStateAndDriverId(ACTIVE_RIDE_STATE, EXISTING_DRIVER_ID, by("startTime"))).thenReturn(rides);
        RideForTransferDTO rideForTransferDTO = rideService.getRideForDriverByStatus(EXISTING_DRIVER_ID, ACTIVE_RIDE_STATE);
        assertEquals(rideForTransferDTO.getStatus(), ACTIVE_RIDE_STATE);
    }

    @Test
    public void shouldNotGetRideForPassengerByStatusNoRide() throws Exception {
        Mockito.when(rideRepository.findByRideStateAndPassengersId(FINISHED_RIDE_STATE, EXISTING_PASSENGER_ID, by("startTime"))).thenReturn(new HashSet<Ride>());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.getRideForPassengerByStatus(EXISTING_PASSENGER_ID, FINISHED_RIDE_STATE);
        });
        assertThat(exception.getMessage()).contains("Active ride does not exist!");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldGetRideForPassengerByStatus() throws Exception {
        Driver driver = new Driver();
        driver.setId(EXISTING_DRIVER_ID);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(EXISTING_VEHICLE_ID);
        Location vehicleLocation = new Location();
        vehicleLocation.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        vehicleLocation.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        vehicle.setLocation(vehicleLocation);
        driver.setVehicle(vehicle);
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setId(EXISTING_RIDE_ID);
        ride.setStartTime(RIDE_TIME);
        ride.setEndTime(RIDE_TIME);
        ride.setEstimatedTime(RANDOM_TIME_ESTIMATION);
        ride.setPrice(RANDOM_PRICE);
        Set<Passenger> passengers = new HashSet<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(EXISTING_PASSENGER_ID);
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
        ride.setRideState(ACTIVE_RIDE_STATE);
        ride.setVehicleType(new VehicleType(EXISTING_VEHICLE_TYPE));

        Set<Ride> rides = new HashSet<>();
        rides.add(ride);

        Mockito.when(rideRepository.findByRideStateAndPassengersId(ACTIVE_RIDE_STATE, EXISTING_PASSENGER_ID, by("startTime"))).thenReturn(rides);
        RideForTransferDTO rideForTransferDTO = rideService.getRideForPassengerByStatus(EXISTING_PASSENGER_ID, ACTIVE_RIDE_STATE);
        assertEquals(rideForTransferDTO.getStatus(), ACTIVE_RIDE_STATE);
    }

    @Test
    public void shouldNotAddNewFavouriteRidePassengerHave10FavouriteRides() throws Exception {


        FavouriteRide ride = new FavouriteRide();
        ride.setId(EXISTING_RIDE_ID);
        List<Passenger> passengers = new ArrayList<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(EXISTING_PASSENGER_ID);
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
        List<Route> locations = new ArrayList<>();
        locations.add(route);
        ride.setLocations(locations);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setFavoriteName("Moja omiljena");

        Set<FavouriteRide> favouriteRides = new HashSet<>();
        favouriteRides.add(ride);
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        existingPassenger.setFavouriteRides(favouriteRides);
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_PASSENGER_EMAIL);

        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(existingPassenger));

        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.addNewFavouriteRide(new FavouriteRideBasicDTO(), mockPrincipal);
        });
        assertThat(exception.getMessage()).contains("Number of favorite rides cannot exceed 10!");
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);

    }

    @Test
    public void shouldAddNewFavouriteRideWithoutMorePassenger() throws Exception {

        FavouriteRide ride = new FavouriteRide();
        ride.setId(EXISTING_RIDE_ID);
        List<Passenger> passengers = new ArrayList<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(EXISTING_PASSENGER_ID);
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
        List<Route> locations = new ArrayList<>();
        locations.add(route);
        ride.setLocations(locations);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setFavoriteName("Moja omiljena");

        Set<FavouriteRide> favouriteRides = new HashSet<>();
        favouriteRides.add(ride);
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        existingPassenger.setFavouriteRides(favouriteRides);

        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_PASSENGER_EMAIL);
        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(existingPassenger));



        FavouriteRideBasicDTO rideBasicDTO = new FavouriteRideBasicDTO();
        List<UserForRideDTO> userForRideDTOSe = new ArrayList<>();
        UserForRideDTO existingUserForRide = new UserForRideDTO();
        existingUserForRide.setId(EXISTING_PASSENGER_ID);
        existingUserForRide.setEmail(EXISTING_PASSENGER_EMAIL);
        userForRideDTOSe.add(existingUserForRide);
        UserForRideDTO notExistingUserForRide = new UserForRideDTO();
        notExistingUserForRide.setId(NOT_EXISTING_PASSENGER_ID);
        notExistingUserForRide.setEmail(NOT_EXISTING_PASSENGER_EMAIL);
        userForRideDTOSe.add(notExistingUserForRide);
        rideBasicDTO.setPassengers(userForRideDTOSe);
        LocationForRideDTO departureLocationDTO = new LocationForRideDTO();
        departureLocationDTO.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        departureLocationDTO.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        departureLocationDTO.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        LocationForRideDTO destinationLocationDTO = new LocationForRideDTO();
        destinationLocationDTO.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        destinationLocationDTO.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        destinationLocationDTO.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        RouteForRideDTO routeDTO = new RouteForRideDTO();
        routeDTO.setDeparture(departureLocationDTO);
        routeDTO.setDestination(destinationLocationDTO);
        List<RouteForRideDTO> locationsDTO = new ArrayList<>();
        locationsDTO.add(routeDTO);
        locationsDTO.add(routeDTO);
        rideBasicDTO.setLocations(locationsDTO);
        rideBasicDTO.setBabyTransport(true);
        rideBasicDTO.setPetTransport(true);
        rideBasicDTO.setFavoriteName("Moja omiljena");
        rideBasicDTO.setDistance(DISTANCE_FOR_NOT_EXISTING_ROUTE);


        Mockito.when(rideRequestService.findRouteOrCreate(routeDTO.getDeparture(), routeDTO.getDestination(), rideBasicDTO.getDistance())).thenReturn(route);
        Mockito.when(passengerRepository.findByEmail(NOT_EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.empty());
        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(existingPassenger));

        FavouriteRide biggestRide = ride;
        biggestRide.setId(9L);
        List<FavouriteRide> favouriteRidesList = new ArrayList<>();
        favouriteRidesList.add(biggestRide);
        Mockito.when(favouriteRideRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(favouriteRidesList);


        FavouriteRideDTO favouriteRide = rideService.addNewFavouriteRide(rideBasicDTO, mockPrincipal);
        assertEquals(favouriteRide.getPassengers().size(), 1);



    }


    @Test
    public void shouldAddNewFavouriteRideWithMorePassenger() throws Exception {

        FavouriteRide ride = new FavouriteRide();
        ride.setId(EXISTING_RIDE_ID);
        List<Passenger> passengers = new ArrayList<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(EXISTING_PASSENGER_ID);
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
        List<Route> locations = new ArrayList<>();
        locations.add(route);
        ride.setLocations(locations);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setFavoriteName("Moja omiljena");

        Set<FavouriteRide> favouriteRides = new HashSet<>();
        favouriteRides.add(ride);
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        existingPassenger.setFavouriteRides(favouriteRides);

        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_PASSENGER_EMAIL);
        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(existingPassenger));
        FavouriteRide biggestRide = ride;
        biggestRide.setId(9L);
        List<FavouriteRide> favouriteRidesList = new ArrayList<>();
        favouriteRidesList.add(biggestRide);
        Mockito.when(favouriteRideRepository.findAll(Sort.by(Sort.Direction.DESC, "id"))).thenReturn(favouriteRidesList);



        FavouriteRideBasicDTO rideBasicDTO = new FavouriteRideBasicDTO();
        List<UserForRideDTO> userForRideDTOSe = new ArrayList<>();
        UserForRideDTO existingUserForRide = new UserForRideDTO();
        existingUserForRide.setId(EXISTING_PASSENGER_ID);
        existingUserForRide.setEmail(EXISTING_PASSENGER_EMAIL);
        userForRideDTOSe.add(existingUserForRide);
        userForRideDTOSe.add(existingUserForRide);
        rideBasicDTO.setPassengers(userForRideDTOSe);
        LocationForRideDTO departureLocationDTO = new LocationForRideDTO();
        departureLocationDTO.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        departureLocationDTO.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        departureLocationDTO.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        LocationForRideDTO destinationLocationDTO = new LocationForRideDTO();
        destinationLocationDTO.setLatitude(LATITUDE_FOR_NOT_EXISTING_ROUTE);
        destinationLocationDTO.setLongitude(LONGITUDE_FOR_NOT_EXISTING_ROUTE);
        destinationLocationDTO.setAddress(ADDRESS_FOR_EXISTING_ROUTE);
        RouteForRideDTO routeDTO = new RouteForRideDTO();
        routeDTO.setDeparture(departureLocationDTO);
        routeDTO.setDestination(destinationLocationDTO);
        List<RouteForRideDTO> locationsDTO = new ArrayList<>();
        locationsDTO.add(routeDTO);
        locationsDTO.add(routeDTO);
        rideBasicDTO.setLocations(locationsDTO);
        rideBasicDTO.setBabyTransport(true);
        rideBasicDTO.setPetTransport(true);
        rideBasicDTO.setFavoriteName("Moja omiljena");
        rideBasicDTO.setDistance(DISTANCE_FOR_NOT_EXISTING_ROUTE);


        Mockito.when(rideRequestService.findRouteOrCreate(routeDTO.getDeparture(), routeDTO.getDestination(), rideBasicDTO.getDistance())).thenReturn(route);
        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(existingPassenger));



        FavouriteRideDTO favouriteRide = rideService.addNewFavouriteRide(rideBasicDTO, mockPrincipal);
        assertEquals(favouriteRide.getPassengers().size(), 2);

    }

    @Test
    public void shouldGetAllFavouriteRidesByPassengerWhoHaveFavouriteRides() throws Exception {
        FavouriteRide ride = new FavouriteRide();
        ride.setId(EXISTING_RIDE_ID);
        List<Passenger> passengers = new ArrayList<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(EXISTING_PASSENGER_ID);
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
        List<Route> locations = new ArrayList<>();
        locations.add(route);
        ride.setLocations(locations);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setFavoriteName("Moja omiljena");

        Set<FavouriteRide> favouriteRides = new HashSet<>();
        favouriteRides.add(ride);
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        favouriteRides.add(new FavouriteRide(ride));
        existingPassenger.setFavouriteRides(favouriteRides);

        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_PASSENGER_EMAIL);
        Mockito.when(passengerRepository.findAllPassengerFavouriteRides(EXISTING_PASSENGER_EMAIL)).thenReturn(existingPassenger.getFavouriteRides());
        Collection<FavouriteRideDTO> favouriteRidesSet = rideService.getAllFavouriteRidesByPassenger(mockPrincipal);
        assertEquals(favouriteRidesSet.size(), 9);
    }


    @Test
    public void shouldGetEmptyAllFavouriteRidesByPassengerWhoDoesNotHaveFavouriteRides() throws Exception
    {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_PASSENGER_EMAIL);
        Mockito.when(passengerRepository.findAllPassengerFavouriteRides(EXISTING_PASSENGER_EMAIL)).thenReturn(new HashSet<FavouriteRide>());
        Collection<FavouriteRideDTO> favouriteRidesSet = rideService.getAllFavouriteRidesByPassenger(mockPrincipal);
        assertThat(favouriteRidesSet.isEmpty());
    }

    @Test
    public void shouldNotDeleteFavouriteRideNoRide() throws Exception {
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_PASSENGER_EMAIL);
        Mockito.when(favouriteRideRepository.findById(NOT_EXISTING_RIDE_ID)).thenReturn(Optional.empty());
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.deleteFavouriteRide(NOT_EXISTING_RIDE_ID, mockPrincipal);
        });
        assertThat(exception.getMessage()).contains("Favorite location does not exist!");
        assertEquals(exception.getStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldNotDeleteFavouriteRideBadUser() throws Exception {
        FavouriteRide ride = new FavouriteRide();
        ride.setId(EXISTING_RIDE_ID);
        List<Passenger> passengers = new ArrayList<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(EXISTING_PASSENGER_ID);
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
        List<Route> locations = new ArrayList<>();
        locations.add(route);
        ride.setLocations(locations);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setFavoriteName("Moja omiljena");
        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_PASSENGER_EMAIL);
        Mockito.when(favouriteRideRepository.findById(NOT_EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));
        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(existingPassenger));
        ResponseStatusException exception = Assertions.assertThrows(ResponseStatusException.class,() -> {
            rideService.deleteFavouriteRide(NOT_EXISTING_RIDE_ID, mockPrincipal);
        });
        assertThat(exception.getMessage()).contains("This user has not that favourite location!");
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldDeleteFavouriteRide() throws Exception {
        FavouriteRide ride = new FavouriteRide();
        ride.setId(EXISTING_RIDE_ID);
        List<Passenger> passengers = new ArrayList<>();
        Passenger existingPassenger = new Passenger();
        existingPassenger.setId(EXISTING_PASSENGER_ID);
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
        List<Route> locations = new ArrayList<>();
        locations.add(route);
        ride.setLocations(locations);
        ride.setBabyTransport(true);
        ride.setPetTransport(true);
        ride.setFavoriteName("Moja omiljena");

        Set<FavouriteRide> favouriteRides = new HashSet<>();
        favouriteRides.add(ride);
        existingPassenger.setFavouriteRides(favouriteRides);

        Principal mockPrincipal = Mockito.mock(Principal.class);
        Mockito.when(mockPrincipal.getName()).thenReturn(EXISTING_PASSENGER_EMAIL);
        Mockito.when(favouriteRideRepository.findById(EXISTING_RIDE_ID)).thenReturn(Optional.of(ride));
        Mockito.when(passengerRepository.findByEmail(EXISTING_PASSENGER_EMAIL)).thenReturn(Optional.of(existingPassenger));
        rideService.deleteFavouriteRide(EXISTING_RIDE_ID, mockPrincipal);

        Mockito.verify(favouriteRideRepository, Mockito.times(1)).deleteById(anyLong());
        Mockito.verify(passengerRepository, Mockito.times(1)).save(any(Passenger.class));
        Mockito.verify(passengerRepository, Mockito.times(1)).flush();
        Mockito.verify(favouriteRideRepository, Mockito.times(1)).flush();

    }

}
