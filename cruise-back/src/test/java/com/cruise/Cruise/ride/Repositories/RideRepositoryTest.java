package com.cruise.Cruise.ride.Repositories;

import com.cruise.Cruise.driver.DTO.DriverBasicInfoDTO;
import com.cruise.Cruise.driver.DTO.DriversRideDTO;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.models.Passenger;
import com.cruise.Cruise.models.Picture;
import com.cruise.Cruise.models.Ride;
import com.cruise.Cruise.passenger.DTO.PassengerBasicInfoDTO;
import com.cruise.Cruise.ride.DTO.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Sort.by;


@DataJpaTest
@ActiveProfiles("jdbc")
public class RideRepositoryTest {

    public static Long PASSENGER_ID_WITH_2_RIDES = 1L;
    public static Long PASSENGER_ID_WITH_1_RIDE = 2L;
    public static Long PASSENGER_ID_WITH_NO_RIDES = 3L;
    public static Long DRIVER_ID_WITH_2_RIDES = 4L;
    public static Long DRIVER_ID_WITH_NO_RIDES = 5L;
    public static Long NOT_EXISTING_USER_ID = 123L;
    public static String NOT_EXISTING_RIDE_STATE = "INREVIEW";
    public static String EXISTING_RIDE_STATE = "FINISHED";
    @Autowired
    private IRideRepository rideRepository;

    @Test
    public void shouldFindRidesForExistingPassengerWithRides() {
        LocationForRideDTO startLocation = new LocationForRideDTO("Bulevar oslobodjenja 40", 45.267, 19.833);
        LocationForRideDTO endLocation1 = new LocationForRideDTO("Bulevar oslobodjenja 2", 45.265, 19.831);
        LocationForRideDTO endLocation2 = new LocationForRideDTO("Bulevar oslobodjenja 100", 45.269, 19.835);
        RouteForRideDTO route1 = new RouteForRideDTO(startLocation, endLocation1);
        RouteForRideDTO route2 = new RouteForRideDTO(startLocation, endLocation2);
        List<RouteForRideDTO> rideRoutes1 = new ArrayList<>();
        List<RouteForRideDTO> rideRoutes2 = new ArrayList<>();
        rideRoutes1.add(route1);
        rideRoutes2.add(route2);
        DriverBasicInfoDTO ridesDriver = new DriverBasicInfoDTO(DRIVER_ID_WITH_2_RIDES, "marko@gmail.com");
        PassengerBasicInfoDTO passenger1 = new PassengerBasicInfoDTO(PASSENGER_ID_WITH_2_RIDES, "uros.pocek@gmail.com");
        PassengerBasicInfoDTO passenger2 = new PassengerBasicInfoDTO(PASSENGER_ID_WITH_1_RIDE, "tamarailic11@gmail.com");
        List<PassengerBasicInfoDTO> ridesPassengers1 = new ArrayList<>();
        List<PassengerBasicInfoDTO> ridesPassengers2 = new ArrayList<>();
        ridesPassengers1.add(passenger1);
        ridesPassengers1.add(passenger2);
        ridesPassengers2.add(passenger1);
        RideForUserDTO passengersFirstRide = new RideForUserDTO(PASSENGER_ID_WITH_2_RIDES, rideRoutes1, LocalDateTime.parse("2022-12-23T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2022-12-23T23:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), 543, ridesDriver, ridesPassengers1, 11, "STANDARD", false, false, null, "FINISHED");
        RideForUserDTO passengersSecondRide = new RideForUserDTO(PASSENGER_ID_WITH_1_RIDE, rideRoutes2, LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2022-12-24T23:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), 543, ridesDriver, ridesPassengers2, 11, "STANDARD", false, false, null, "FINISHED");
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<RideForUserDTO> passengerRides = rideRepository.findByPassengersIdOrDriverId(PASSENGER_ID_WITH_2_RIDES, PASSENGER_ID_WITH_2_RIDES, queryStartTime, queryEndTime, sort);
        Assertions.assertEquals(2, passengerRides.size());
        assertThat(passengerRides.get(0)).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(passengersFirstRide);
        assertThat(passengerRides.get(1)).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(passengersSecondRide);
    }

    @Test
    public void shouldFindRidesForExistingDriverWithRides() {
        LocationForRideDTO startLocation = new LocationForRideDTO("Bulevar oslobodjenja 40", 45.267, 19.833);
        LocationForRideDTO endLocation1 = new LocationForRideDTO("Bulevar oslobodjenja 2", 45.265, 19.831);
        LocationForRideDTO endLocation2 = new LocationForRideDTO("Bulevar oslobodjenja 100", 45.269, 19.835);
        RouteForRideDTO route1 = new RouteForRideDTO(startLocation, endLocation1);
        RouteForRideDTO route2 = new RouteForRideDTO(startLocation, endLocation2);
        List<RouteForRideDTO> rideRoutes1 = new ArrayList<>();
        List<RouteForRideDTO> rideRoutes2 = new ArrayList<>();
        rideRoutes1.add(route1);
        rideRoutes2.add(route2);
        DriverBasicInfoDTO ridesDriver = new DriverBasicInfoDTO(DRIVER_ID_WITH_2_RIDES, "marko@gmail.com");
        PassengerBasicInfoDTO passenger1 = new PassengerBasicInfoDTO(PASSENGER_ID_WITH_2_RIDES, "uros.pocek@gmail.com");
        PassengerBasicInfoDTO passenger2 = new PassengerBasicInfoDTO(PASSENGER_ID_WITH_1_RIDE, "tamarailic11@gmail.com");
        List<PassengerBasicInfoDTO> ridesPassengers1 = new ArrayList<>();
        List<PassengerBasicInfoDTO> ridesPassengers2 = new ArrayList<>();
        ridesPassengers1.add(passenger2);
        ridesPassengers1.add(passenger1);
        ridesPassengers2.add(passenger1);
        RideForUserDTO passengersFirstRide = new RideForUserDTO(PASSENGER_ID_WITH_2_RIDES, rideRoutes1, LocalDateTime.parse("2022-12-23T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2022-12-23T23:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), 543, ridesDriver, ridesPassengers1, 11, "STANDARD", false, false, null, "FINISHED");
        RideForUserDTO passengersSecondRide = new RideForUserDTO(PASSENGER_ID_WITH_1_RIDE, rideRoutes2, LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.parse("2022-12-24T23:52:52.038935", DateTimeFormatter.ISO_DATE_TIME), 543, ridesDriver, ridesPassengers2, 11, "STANDARD", false, false, null, "FINISHED");
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<RideForUserDTO> driverRides = rideRepository.findByPassengersIdOrDriverId(DRIVER_ID_WITH_2_RIDES, DRIVER_ID_WITH_2_RIDES, queryStartTime, queryEndTime, sort);
        Assertions.assertEquals(2, driverRides.size());
        assertThat(driverRides.get(0)).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(passengersFirstRide);
        assertThat(driverRides.get(1)).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(passengersSecondRide);
    }

    @Test
    public void shouldNotFindRidesForNonExistingUser() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<RideForUserDTO> passengerRides = rideRepository.findByPassengersIdOrDriverId(NOT_EXISTING_USER_ID, NOT_EXISTING_USER_ID, queryStartTime, queryEndTime, sort);
        assertThat(passengerRides).isEmpty();
    }

    @Test
    public void shouldNotFindRidesForExistingPassengerWithoutRides() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<RideForUserDTO> passengerRides = rideRepository.findByPassengersIdOrDriverId(PASSENGER_ID_WITH_NO_RIDES, PASSENGER_ID_WITH_NO_RIDES, queryStartTime, queryEndTime, sort);
        assertThat(passengerRides).isEmpty();
    }

    @Test
    public void shouldNotFindRidesForExistingDriverWithoutRides() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<RideForUserDTO> driverRides = rideRepository.findByPassengersIdOrDriverId(DRIVER_ID_WITH_NO_RIDES, DRIVER_ID_WITH_NO_RIDES, queryStartTime, queryEndTime, sort);
        assertThat(driverRides).isEmpty();
    }

    @Test
    public void shouldNotFindRidesForPassengerWithRidesForButNotInSpecifiedTimeFrame() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2000-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2000-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<RideForUserDTO> passengerRides = rideRepository.findByPassengersIdOrDriverId(PASSENGER_ID_WITH_2_RIDES, PASSENGER_ID_WITH_2_RIDES, queryStartTime, queryEndTime, sort);
        assertThat(passengerRides).isEmpty();
    }

    @Test
    public void shouldFindUserHistoryItemsForExistingUser() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Ride> userRides = rideRepository.findAllUserHistoryItems(PASSENGER_ID_WITH_2_RIDES, pageable);
        Assertions.assertEquals(2, userRides.getTotalElements());
        Assertions.assertEquals(1, userRides.getTotalPages());
    }

    @Test
    public void shouldNotFindUserHistoryItemsForNonExistingUser() {
        Pageable pageable = Pageable.ofSize(10);
        Page<Ride> userRides = rideRepository.findAllUserHistoryItems(NOT_EXISTING_USER_ID, pageable);
        assertThat(userRides).isEmpty();
        Assertions.assertEquals(0, userRides.getTotalPages());
    }

    @Test
    public void shouldReturnRidesForExistingDriverWithValidState() {
        Driver validDriverWithRides = new Driver(DRIVER_ID_WITH_2_RIDES, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        Set<Ride> driverRides = rideRepository.findByRideStateAndDriver(EXISTING_RIDE_STATE, validDriverWithRides);
        Assertions.assertEquals(2, driverRides.size());
    }

    @Test
    public void shouldNotReturnRidesForExistingDriverWithInvalidState() {
        Driver validDriverWithRides = new Driver(DRIVER_ID_WITH_2_RIDES, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        Set<Ride> driverRides = rideRepository.findByRideStateAndDriver(NOT_EXISTING_RIDE_STATE, validDriverWithRides);
        assertThat(driverRides).isEmpty();
    }

    @Test
    public void shouldReturnRidesForValidDriverIdWithValidState() {
        Sort sort = by("startTime");
        Set<Ride> driverRides = rideRepository.findByRideStateAndDriverId(EXISTING_RIDE_STATE, DRIVER_ID_WITH_2_RIDES, sort);
        Assertions.assertEquals(2, driverRides.size());
        Assertions.assertTrue(driverRides.stream().toList().get(0).getStartTime().isBefore(driverRides.stream().toList().get(1).getStartTime()));
    }

    @Test
    public void shouldNotReturnRidesForInvalidDriverId() {
        Sort sort = by("startTime");
        Set<Ride> driverRides = rideRepository.findByRideStateAndDriverId(EXISTING_RIDE_STATE, NOT_EXISTING_USER_ID, sort);
        assertThat(driverRides).isEmpty();
    }

    @Test
    public void shouldNotReturnRidesForValidDriverIdWithoutRides() {
        Sort sort = by("startTime");
        Set<Ride> driverRides = rideRepository.findByRideStateAndDriverId(EXISTING_RIDE_STATE, DRIVER_ID_WITH_NO_RIDES, sort);
        assertThat(driverRides).isEmpty();
    }

    @Test
    public void shouldNotReturnRidesForValidStatusAndDriverIdWithRides() {
        Sort sort = by("startTime");
        Set<Ride> driverRides = rideRepository.findByRideStateAndDriverId(NOT_EXISTING_RIDE_STATE, DRIVER_ID_WITH_2_RIDES, sort);
        assertThat(driverRides).isEmpty();
    }

    @Test
    public void shouldFindRidesForChatForExistingPassenger() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ride> rides = rideRepository.findByPassengersIdOrDriverIdChatItems(PASSENGER_ID_WITH_2_RIDES, PASSENGER_ID_WITH_2_RIDES, pageable);
        assertThat(rides.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void shouldNotFindRidesForChatForNotExistingPassenger() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ride> rides = rideRepository.findByPassengersIdOrDriverIdChatItems(NOT_EXISTING_USER_ID, NOT_EXISTING_USER_ID, pageable);
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldNotFindRidesForChatForExistingPassengerWithoutRides() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ride> rides = rideRepository.findByPassengersIdOrDriverIdChatItems(PASSENGER_ID_WITH_NO_RIDES, PASSENGER_ID_WITH_NO_RIDES, pageable);
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldFindRidesForChatForExistingDriver() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ride> rides = rideRepository.findByPassengersIdOrDriverIdChatItems(DRIVER_ID_WITH_2_RIDES, DRIVER_ID_WITH_2_RIDES, pageable);
        assertThat(rides.getTotalElements()).isEqualTo(2);
    }

    @Test
    public void shouldNotFindRidesForChatForNotExistingDriver() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ride> rides = rideRepository.findByPassengersIdOrDriverIdChatItems(NOT_EXISTING_USER_ID, NOT_EXISTING_USER_ID, pageable);
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldNotFindRidesForChatForExistingDriverWithoutRides() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ride> rides = rideRepository.findByPassengersIdOrDriverIdChatItems(DRIVER_ID_WITH_NO_RIDES, DRIVER_ID_WITH_NO_RIDES, pageable);
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldFindRideForUserDTOListForExistingPassengerAndValidTimesSortedByStartTime() {
        LocalDateTime from = LocalDateTime.parse("2022-12-23T22:52:52.038935");
        LocalDateTime to = LocalDateTime.parse("2022-12-24T23:52:52.038935");
        List<RideForUserDTO> rides = rideRepository.findAllPassengersRides(PASSENGER_ID_WITH_2_RIDES, from, to, by("startTime"));
        assertThat(rides.size()).isEqualTo(2);
        assertThat(rides.get(0).getStartTime().isBefore(rides.get(1).getStartTime())).isTrue();
    }

    @Test
    public void shouldNotFindRideForUserDTOListForExistingPassengerWithoutRides() {
        LocalDateTime from = LocalDateTime.parse("2022-12-23T22:52:52.038935");
        LocalDateTime to = LocalDateTime.parse("2022-12-24T23:52:52.038935");
        List<RideForUserDTO> rides = rideRepository.findAllPassengersRides(PASSENGER_ID_WITH_NO_RIDES, from, to, by("startTime"));
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldNotFindRideForUserDTOListForNotExistingPassengerAndValidTimes() {
        LocalDateTime from = LocalDateTime.parse("2022-12-23T22:52:52.038935");
        LocalDateTime to = LocalDateTime.parse("2022-12-24T23:52:52.038935");
        List<RideForUserDTO> rides = rideRepository.findAllPassengersRides(NOT_EXISTING_USER_ID, from, to, by("startTime"));
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldNotFindRideForUserDTOListForExistingPassengerAndEndTimeGreaterThenStartTime() {
        LocalDateTime from = LocalDateTime.parse("2022-12-24T23:52:52.038935");
        LocalDateTime to = LocalDateTime.parse("2022-12-23T22:52:52.038935");
        List<RideForUserDTO> rides = rideRepository.findAllPassengersRides(PASSENGER_ID_WITH_2_RIDES, from, to, by("startTime"));
        assertThat(rides).isEmpty();
    }

    //Set<Ride> findByRideStateAndPassengers(String state, Passenger passenger);
    @Test
    public void shouldFindRideByExistingRideStateAndExistingPassenger() {
        Picture picture = new Picture();
        picture.setId(PASSENGER_ID_WITH_2_RIDES);
        picture.setPictureContent("");
        Passenger passenger = new Passenger();
        passenger.setId(PASSENGER_ID_WITH_2_RIDES);
        passenger.setProfilePicture(picture);
        passenger.setName("Uros");
        passenger.setSurname("Pocek");
        passenger.setTelephoneNumber("+381606337280");
        passenger.setEmail("uros.pocek@gmail.com");
        passenger.setAddress("bul. Oslobodjenja 40");
        passenger.setPassword("uros");
        passenger.setActive(true);
        passenger.setBlocked(false);

        Set<Ride> rides = rideRepository.findByRideStateAndPassengers(EXISTING_RIDE_STATE, passenger);
        assertThat(rides).isNotEmpty();
        assertThat(rides.size()).isEqualTo(2);
    }

    @Test
    public void shouldNotFindRideByExistingRideStateAndExistingPassengerWithoutRides() {
        Picture picture = new Picture();
        picture.setId(PASSENGER_ID_WITH_NO_RIDES);
        picture.setPictureContent("");
        Passenger passenger = new Passenger();
        passenger.setId(PASSENGER_ID_WITH_NO_RIDES);
        passenger.setProfilePicture(picture);
        passenger.setName("Test");
        passenger.setSurname("Test");
        passenger.setTelephoneNumber("+381608449333");
        passenger.setEmail("test@gmail.com");
        passenger.setAddress("test 1");
        passenger.setPassword("test");
        passenger.setActive(true);
        passenger.setBlocked(false);

        Set<Ride> rides = rideRepository.findByRideStateAndPassengers(EXISTING_RIDE_STATE, passenger);
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldNotFindRideByNotExistingRideStateAndExistingPassenger() {
        Picture picture = new Picture();
        picture.setId(PASSENGER_ID_WITH_2_RIDES);
        picture.setPictureContent("");
        Passenger passenger = new Passenger();
        passenger.setId(PASSENGER_ID_WITH_2_RIDES);
        passenger.setProfilePicture(picture);
        passenger.setName("Uros");
        passenger.setSurname("Pocek");
        passenger.setTelephoneNumber("+381606337280");
        passenger.setEmail("uros.pocek@gmail.com");
        passenger.setAddress("bul. Oslobodjenja 40");
        passenger.setPassword("uros");
        passenger.setActive(true);
        passenger.setBlocked(false);

        Set<Ride> rides = rideRepository.findByRideStateAndPassengers(NOT_EXISTING_RIDE_STATE, passenger);
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldNotFindRideByExistingRideStateAndNotExistingPassenger() {
        Picture picture = new Picture();
        picture.setId(NOT_EXISTING_USER_ID);
        picture.setPictureContent("");
        Passenger passenger = new Passenger();
        passenger.setId(NOT_EXISTING_USER_ID);
        passenger.setProfilePicture(picture);
        passenger.setName("Not");
        passenger.setSurname("Present");
        passenger.setTelephoneNumber("+381606337280");
        passenger.setEmail("not@gmail.com");
        passenger.setAddress("not");
        passenger.setPassword("not");
        passenger.setActive(true);
        passenger.setBlocked(false);

        Set<Ride> rides = rideRepository.findByRideStateAndPassengers(EXISTING_RIDE_STATE, passenger);
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldFindRideByExistingRideStateAndExistingPassengerIdSortedByStartTime() {
        Set<Ride> rides = rideRepository.findByRideStateAndPassengersId(EXISTING_RIDE_STATE, PASSENGER_ID_WITH_2_RIDES, by("startTime"));
        assertThat(rides).isNotEmpty();
        assertThat(rides.size()).isEqualTo(2);
        assertThat(rides.stream().toList().get(0).getStartTime().isBefore(rides.stream().toList().get(1).getStartTime())).isTrue();
    }

    @Test
    public void shouldNotFindRideByExistingRideStateAndExistingPassengerIdWithoutRides() {
        Set<Ride> rides = rideRepository.findByRideStateAndPassengersId(EXISTING_RIDE_STATE, PASSENGER_ID_WITH_NO_RIDES, by("startTime"));
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldNotFindRideByNotExistingRideStateAndExistingPassengerId() {
        Set<Ride> rides = rideRepository.findByRideStateAndPassengersId(NOT_EXISTING_RIDE_STATE, PASSENGER_ID_WITH_2_RIDES, by("startTime"));
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldNotFindRideByExistingRideStateAndNotExistingPassengerId() {
        Set<Ride> rides = rideRepository.findByRideStateAndPassengersId(EXISTING_RIDE_STATE, NOT_EXISTING_USER_ID, by("startTime"));
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldFindAllRidesForExistingDriverWithRides() {
        LocationForRideDTO startLocation = new LocationForRideDTO("Bulevar oslobodjenja 40", 45.267, 19.833);
        LocationForRideDTO endLocation1 = new LocationForRideDTO("Bulevar oslobodjenja 2", 45.265, 19.831);
        LocationForRideDTO endLocation2 = new LocationForRideDTO("Bulevar oslobodjenja 100", 45.269, 19.835);
        RouteForRideDTO route1 = new RouteForRideDTO(startLocation, endLocation1);
        RouteForRideDTO route2 = new RouteForRideDTO(startLocation, endLocation2);
        List<RouteForRideDTO> rideRoutes1 = new ArrayList<>();
        List<RouteForRideDTO> rideRoutes2 = new ArrayList<>();
        rideRoutes1.add(route1);
        rideRoutes2.add(route2);

        UserForRideDTO ridesDriver = new UserForRideDTO(DRIVER_ID_WITH_2_RIDES, "marko@gmail.com");
        UserForRideDTO passenger1 = new UserForRideDTO(PASSENGER_ID_WITH_2_RIDES, "uros.pocek@gmail.com");
        UserForRideDTO passenger2 = new UserForRideDTO(PASSENGER_ID_WITH_1_RIDE, "tamarailic11@gmail.com");
        List<UserForRideDTO> ridesPassengers1 = new ArrayList<>();
        List<UserForRideDTO> ridesPassengers2 = new ArrayList<>();
        ridesPassengers1.add(passenger2);
        ridesPassengers1.add(passenger1);
        ridesPassengers2.add(passenger1);
        DriversRideDTO passengersFirstRide = new DriversRideDTO(PASSENGER_ID_WITH_2_RIDES, rideRoutes1, new RejectionForRideDTO(), "2022-12-23T22:52:52.038935", "2022-12-23T23:52:52.038935", 543, ridesDriver, ridesPassengers1, 11, "STANDARD", false, false);
        DriversRideDTO passengersSecondRide = new DriversRideDTO(PASSENGER_ID_WITH_1_RIDE, rideRoutes2, new RejectionForRideDTO(), "2022-12-24T22:52:52.038935", "2022-12-24T23:52:52.038935", 543, ridesDriver, ridesPassengers2, 11, "STANDARD", false, false);
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<DriversRideDTO> driverRides = rideRepository.findAllDriverRides(DRIVER_ID_WITH_2_RIDES, queryStartTime, queryEndTime, sort);
        Assertions.assertEquals(2, driverRides.size());
        assertThat(driverRides.get(0)).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(passengersFirstRide);
        assertThat(driverRides.get(1)).usingRecursiveComparison().ignoringCollectionOrder().isEqualTo(passengersSecondRide);
    }

    @Test
    public void shouldNotFindAllRidesForNonExistingUser() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<DriversRideDTO> passengerRides = rideRepository.findAllDriverRides(NOT_EXISTING_USER_ID, queryStartTime, queryEndTime, sort);
        assertThat(passengerRides).isEmpty();
    }

    @Test
    public void shouldNotFindAllRidesForExistingDriverWithoutRides() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<DriversRideDTO> driverRides = rideRepository.findAllDriverRides(DRIVER_ID_WITH_NO_RIDES, queryStartTime, queryEndTime, sort);
        assertThat(driverRides).isEmpty();
    }

    @Test
    public void shouldNotFindAllRidesForDriverWithRidesForButNotInSpecifiedTimeFrame() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2000-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2000-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<DriversRideDTO> driverRides = rideRepository.findAllDriverRides(PASSENGER_ID_WITH_2_RIDES, queryStartTime, queryEndTime, sort);
        assertThat(driverRides).isEmpty();
    }

    @Test
    public void shouldFindAllRidesForReportForExistingDriverWithRides() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> driverRides = rideRepository.findRidesForDriverReport(DRIVER_ID_WITH_2_RIDES, queryStartTime, queryEndTime, sort);
        Assertions.assertEquals(2, driverRides.size());
    }

    @Test
    public void shouldNotFindAllRidesForForReportNonExistingUser() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> passengerRides = rideRepository.findRidesForDriverReport(NOT_EXISTING_USER_ID, queryStartTime, queryEndTime, sort);
        assertThat(passengerRides).isEmpty();
    }

    @Test
    public void shouldNotFindAllRidesForReportForExistingDriverWithoutRides() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> driverRides = rideRepository.findRidesForDriverReport(DRIVER_ID_WITH_NO_RIDES, queryStartTime, queryEndTime, sort);
        assertThat(driverRides).isEmpty();
    }

    @Test
    public void shouldNotFindAllRidesForReportForDriverWithRidesForButNotInSpecifiedTimeFrame() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2000-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2000-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> driverRides = rideRepository.findRidesForDriverReport(DRIVER_ID_WITH_2_RIDES, queryStartTime, queryEndTime, sort);
        assertThat(driverRides).isEmpty();
    }


    @Test
    public void shouldFindAllRidesForReportForExistingPassengerWithRides() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> passengerRides = rideRepository.findRidesForPassengerReport(PASSENGER_ID_WITH_2_RIDES, queryStartTime, queryEndTime, sort);
        Assertions.assertEquals(2, passengerRides.size());
    }

    @Test
    public void shouldNotFindAllRidesForReportForNonExistingUser() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> passengerRides = rideRepository.findRidesForPassengerReport(NOT_EXISTING_USER_ID, queryStartTime, queryEndTime, sort);
        assertThat(passengerRides).isEmpty();
    }

    @Test
    public void shouldNotFindAllRidesForReportForExistingPassengerWithoutRides() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> passengerRides = rideRepository.findRidesForPassengerReport(DRIVER_ID_WITH_NO_RIDES, queryStartTime, queryEndTime, sort);
        assertThat(passengerRides).isEmpty();
    }

    @Test
    public void shouldNotFindAllRidesForReportForPassengerWithRidesForButNotInSpecifiedTimeFrame() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2000-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2000-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> passengerRides = rideRepository.findRidesForPassengerReport(PASSENGER_ID_WITH_2_RIDES, queryStartTime, queryEndTime, sort);
        assertThat(passengerRides).isEmpty();
    }


    @Test
    public void shouldFindAllRidesForAllReportInSpecifiedTimeFrame() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2022-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2022-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> rides = rideRepository.findRidesForAllReport(queryStartTime, queryEndTime, sort);
        Assertions.assertEquals(2, rides.size());
    }

    @Test
    public void shouldNotFindAllRidesForAllReportNotInSpecifiedTimeFrame() {
        LocalDateTime queryStartTime = LocalDateTime.parse("2000-12-22T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime queryEndTime = LocalDateTime.parse("2000-12-24T22:52:52.038935", DateTimeFormatter.ISO_DATE_TIME);
        Sort sort = by("startTime");
        List<Ride> rides = rideRepository.findRidesForAllReport(queryStartTime, queryEndTime, sort);
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldReturnEmptySetForInvalidRideState() {
        Set<Ride> rides = rideRepository.findByRideState("djisadj");
        assertThat(rides).isEmpty();
    }

    @Test
    public void shouldReturnRidesForFinishedState() {
        Set<Ride> rides = rideRepository.findByRideState("FINISHED");
        Assertions.assertEquals(2, rides.size());
    }


}
