package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.driver.Services.ICalculateWorkingHoursService;
import com.cruise.Cruise.driver.Services.NoDriverAvailableForRideException;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.models.Location;
import com.cruise.Cruise.models.Vehicle;
import com.cruise.Cruise.models.VehicleType;
import com.cruise.Cruise.ride.DTO.LocationForRideDTO;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;
import com.cruise.Cruise.ride.DTO.RouteForRideDTO;
import com.cruise.Cruise.ride.DTO.UserForRideDTO;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("hibernate")
public class FindDriverServiceTest {

    @Autowired
    private IFindDriverService findDriverService;

    @MockBean
    private ICalculateWorkingHoursService workingHoursService;
    @MockBean
    private IDriverRepository driverRepository;
    @MockBean
    private IRideRepository rideRepository;


    @Test
    public void shouldThrowForNoActiveDrivers() {
        when(driverRepository.findByActive(true)).thenReturn(Collections.emptySet());
        RideForTransferDTO ride = new RideForTransferDTO();
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    public void shouldThrowForNoFreeDrivers() {
        Set<Driver> busyDrivers = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        driver1.setStatus("INRIDE");
        driver2.setStatus("INRIDE");
        busyDrivers.add(driver1);
        busyDrivers.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(busyDrivers);
        when(rideRepository.findByRideState("ACCEPTED")).thenReturn(Collections.emptySet());
        RideForTransferDTO ride = new RideForTransferDTO();
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    public void shouldThrowIfNoDriverWithVehicle() {
        Set<Driver> availableDrivers = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        availableDrivers.add(driver1);
        availableDrivers.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(availableDrivers);
        when(workingHoursService.getHowMuchHasDriverWorkedToday(driver1.getId())).thenReturn((long) (2 * 60 * 60 * 1000));
        when(workingHoursService.getHowMuchHasDriverWorkedToday(driver2.getId())).thenReturn((long) (2 * 60 * 60 * 1000));
        RideForTransferDTO ride = new RideForTransferDTO();
        ride.setEstimatedTimeInMinutes(30);
        ride.setBabyTransport(false);
        ride.setPetTransport(false);
        ride.setVehicleType("STANDARD");
        List<UserForRideDTO> passengersInRide = new ArrayList<>();
        passengersInRide.add(new UserForRideDTO(1L, "uros.pocek@gmail.com"));
        passengersInRide.add(new UserForRideDTO(2L, "tamarailic11@gmail.com"));
        passengersInRide.add(new UserForRideDTO(3L, "test@gmail.com"));
        ride.setPassengers(passengersInRide);
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    public void shouldThrowForAllBlockedDrivers() {
        Set<Driver> blockedDrivers = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, true);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, true);
        blockedDrivers.add(driver1);
        blockedDrivers.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(blockedDrivers);
        RideForTransferDTO ride = new RideForTransferDTO();
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    public void shouldThrowIfAllDriversAlreadyRejectedRequest() {
        Set<Driver> driverThatCanDrive = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        driverThatCanDrive.add(driver1);
        driverThatCanDrive.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(driverThatCanDrive);
        RideForTransferDTO ride = new RideForTransferDTO();
        Set<Driver> driversThatRejectedRideRequest = new HashSet<>();
        driversThatRejectedRideRequest.add(driver1);
        driversThatRejectedRideRequest.add(driver2);
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    public void shouldThrowIfNoDriverWithAdequateVehicleWrongVehicleType() {
        Set<Driver> availableDrivers = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        VehicleType van = new VehicleType(1L, "VAN", 80);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Vehicle driversVehicle = new Vehicle(1L, driver1, "Tesla", standard, "NS555UP", 4, startLocation, false, false, null);
        driver1.setVehicle(driversVehicle);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        Vehicle drivers2Vehicle = new Vehicle(2L, driver2, "Tesla Model X", van, "NS555UP", 4, startLocation, false, false, null);
        driver2.setVehicle(drivers2Vehicle);
        availableDrivers.add(driver1);
        availableDrivers.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(availableDrivers);
        RideForTransferDTO ride = new RideForTransferDTO();
        ride.setBabyTransport(false);
        ride.setPetTransport(false);
        ride.setVehicleType("LUXURY");
        List<UserForRideDTO> passengersInRide = new ArrayList<>();
        passengersInRide.add(new UserForRideDTO(1L, "uros.pocek@gmail.com"));
        passengersInRide.add(new UserForRideDTO(2L, "tamarailic11@gmail.com"));
        ride.setPassengers(passengersInRide);
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    public void shouldThrowIfNoDriverWithAdequateVehicleMissingBabyTransport() {
        Set<Driver> availableDrivers = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Vehicle driversVehicle = new Vehicle(1L, driver1, "Tesla", standard, "NS555UP", 4, startLocation, false, true, null);
        driver1.setVehicle(driversVehicle);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        Vehicle drivers2Vehicle = new Vehicle(2L, driver2, "Tesla", standard, "NS555UP", 4, startLocation, false, false, null);
        driver2.setVehicle(drivers2Vehicle);
        availableDrivers.add(driver1);
        availableDrivers.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(availableDrivers);
        RideForTransferDTO ride = new RideForTransferDTO();
        ride.setBabyTransport(true);
        ride.setPetTransport(false);
        ride.setVehicleType("STANDARD");
        List<UserForRideDTO> passengersInRide = new ArrayList<>();
        passengersInRide.add(new UserForRideDTO(1L, "uros.pocek@gmail.com"));
        passengersInRide.add(new UserForRideDTO(2L, "tamarailic11@gmail.com"));
        ride.setPassengers(passengersInRide);
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    public void shouldThrowIfNoDriverWithAdequateVehicleMissingPetTransport() {
        Set<Driver> availableDrivers = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Vehicle driversVehicle = new Vehicle(1L, driver1, "Tesla", standard, "NS555UP", 4, startLocation, true, false, null);
        driver1.setVehicle(driversVehicle);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        Vehicle drivers2Vehicle = new Vehicle(2L, driver2, "Tesla", standard, "NS555UP", 4, startLocation, false, false, null);
        driver2.setVehicle(drivers2Vehicle);
        availableDrivers.add(driver1);
        availableDrivers.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(availableDrivers);
        RideForTransferDTO ride = new RideForTransferDTO();
        ride.setBabyTransport(false);
        ride.setPetTransport(true);
        ride.setVehicleType("STANDARD");
        List<UserForRideDTO> passengersInRide = new ArrayList<>();
        passengersInRide.add(new UserForRideDTO(1L, "uros.pocek@gmail.com"));
        passengersInRide.add(new UserForRideDTO(2L, "tamarailic11@gmail.com"));
        ride.setPassengers(passengersInRide);
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    public void shouldThrowIfNoDriverWithAdequateVehicleOnVehicleWithEnoughSeats() {
        Set<Driver> availableDrivers = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Vehicle driversVehicle = new Vehicle(1L, driver1, "Tesla", standard, "NS555UP", 2, startLocation, true, true, null);
        driver1.setVehicle(driversVehicle);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        Vehicle drivers2Vehicle = new Vehicle(2L, driver2, "Tesla", standard, "NS555UP", 1, startLocation, true, true, null);
        driver2.setVehicle(drivers2Vehicle);
        availableDrivers.add(driver1);
        availableDrivers.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(availableDrivers);
        RideForTransferDTO ride = new RideForTransferDTO();
        ride.setBabyTransport(false);
        ride.setPetTransport(false);
        ride.setVehicleType("STANDARD");
        List<UserForRideDTO> passengersInRide = new ArrayList<>();
        passengersInRide.add(new UserForRideDTO(1L, "uros.pocek@gmail.com"));
        passengersInRide.add(new UserForRideDTO(2L, "tamarailic11@gmail.com"));
        passengersInRide.add(new UserForRideDTO(3L, "test@gmail.com"));
        ride.setPassengers(passengersInRide);
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    public void shouldThrowIfAllDriverCloseToEndOfWorkingTime() {
        Set<Driver> availableDrivers = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location startLocation = new Location("Bulevar oslobodjenja 40", 45.267, 19.833);
        Vehicle driversVehicle = new Vehicle(1L, driver1, "Tesla", standard, "NS555UP", 4, startLocation, true, true, null);
        driver1.setVehicle(driversVehicle);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        Vehicle drivers2Vehicle = new Vehicle(2L, driver2, "Tesla", standard, "NS555UP", 4, startLocation, true, true, null);
        driver2.setVehicle(drivers2Vehicle);
        availableDrivers.add(driver1);
        availableDrivers.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(availableDrivers);
        when(workingHoursService.getHowMuchHasDriverWorkedToday(driver1.getId())).thenReturn((long) (7 * 60 * 60 * 1000 + 30 * 60 * 1000));
        when(workingHoursService.getHowMuchHasDriverWorkedToday(driver2.getId())).thenReturn((long) (7 * 60 * 60 * 1000 + 59 * 60 * 1000));
        RideForTransferDTO ride = new RideForTransferDTO();
        ride.setEstimatedTimeInMinutes(30);
        ride.setBabyTransport(false);
        ride.setPetTransport(false);
        ride.setVehicleType("STANDARD");
        List<UserForRideDTO> passengersInRide = new ArrayList<>();
        passengersInRide.add(new UserForRideDTO(1L, "uros.pocek@gmail.com"));
        passengersInRide.add(new UserForRideDTO(2L, "tamarailic11@gmail.com"));
        passengersInRide.add(new UserForRideDTO(3L, "test@gmail.com"));
        ride.setPassengers(passengersInRide);
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
        verify(workingHoursService, atLeast(2)).getHowMuchHasDriverWorkedToday(anyLong());
    }

    @Test
    public void shouldThrowIfNoLocalDrivers() {
        Set<Driver> availableDrivers = new HashSet<>();
        Driver driver1 = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location locationOfFirstVehicle = new Location("Daleko", 55.267, 29.833);
        Vehicle driversVehicle = new Vehicle(1L, driver1, "Tesla", standard, "NS555UP", 4, locationOfFirstVehicle, true, true, null);
        driver1.setVehicle(driversVehicle);
        Location locationOfSecondVehicle = new Location("Jako daleko", 35.267, 9.833);
        Driver driver2 = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        Vehicle drivers2Vehicle = new Vehicle(2L, driver2, "Tesla", standard, "NS555UP", 4, locationOfSecondVehicle, true, true, null);
        driver2.setVehicle(drivers2Vehicle);
        availableDrivers.add(driver1);
        availableDrivers.add(driver2);
        when(driverRepository.findByActive(true)).thenReturn(availableDrivers);
        when(workingHoursService.getHowMuchHasDriverWorkedToday(driver1.getId())).thenReturn((long) (2 * 60 * 60 * 1000));
        when(workingHoursService.getHowMuchHasDriverWorkedToday(driver2.getId())).thenReturn((long) (2 * 60 * 60 * 1000));
        RideForTransferDTO ride = new RideForTransferDTO();
        List<RouteForRideDTO> rideLocations = new ArrayList<>();
        LocationForRideDTO rideDepartureLocation = new LocationForRideDTO("Bulevar oslobodjenja 40", 45.258443, 19.831852);
        LocationForRideDTO rideDestinationLocation = new LocationForRideDTO("Bulevar oslobodjenja 120", 45.358443, 19.931852);
        RouteForRideDTO rideLocation = new RouteForRideDTO(rideDepartureLocation, rideDestinationLocation);
        rideLocations.add(rideLocation);
        ride.setLocations(rideLocations);
        ride.setEstimatedTimeInMinutes(30);
        ride.setBabyTransport(false);
        ride.setPetTransport(false);
        ride.setVehicleType("STANDARD");
        List<UserForRideDTO> passengersInRide = new ArrayList<>();
        passengersInRide.add(new UserForRideDTO(1L, "uros.pocek@gmail.com"));
        passengersInRide.add(new UserForRideDTO(2L, "tamarailic11@gmail.com"));
        passengersInRide.add(new UserForRideDTO(3L, "test@gmail.com"));
        ride.setPassengers(passengersInRide);
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        Assertions.assertThrows(NoDriverAvailableForRideException.class, () -> {
            findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        });
    }

    @Test
    @DisplayName(value = "happy path return closest driver")
    public void shouldReturnCloserDriver() throws NoDriverAvailableForRideException {
        Set<Driver> availableDrivers = new HashSet<>();
        Driver closerDriver = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location locationOfFirstVehicle = new Location("Bulevar oslobodjenja 40", 45.258443, 19.831852);
        Vehicle driversVehicle = new Vehicle(1L, closerDriver, "Tesla", standard, "NS555UP", 4, locationOfFirstVehicle, true, true, null);
        closerDriver.setVehicle(driversVehicle);
        Location locationOfSecondVehicle = new Location("Bulevar oslobodjenja 100", 45.299, 19.899);
        Driver furtherDriver = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        Vehicle drivers2Vehicle = new Vehicle(2L, furtherDriver, "Tesla", standard, "NS555UP", 4, locationOfSecondVehicle, true, true, null);
        furtherDriver.setVehicle(drivers2Vehicle);
        availableDrivers.add(closerDriver);
        availableDrivers.add(furtherDriver);
        when(driverRepository.findByActive(true)).thenReturn(availableDrivers);
        when(workingHoursService.getHowMuchHasDriverWorkedToday(closerDriver.getId())).thenReturn((long) (2 * 60 * 60 * 1000));
        when(workingHoursService.getHowMuchHasDriverWorkedToday(furtherDriver.getId())).thenReturn((long) (2 * 60 * 60 * 1000));
        RideForTransferDTO ride = new RideForTransferDTO();
        List<RouteForRideDTO> rideLocations = new ArrayList<>();
        LocationForRideDTO rideDepartureLocation = new LocationForRideDTO("Bulevar oslobodjenja 40", 45.258443, 19.831852);
        LocationForRideDTO rideDestinationLocation = new LocationForRideDTO("Bulevar oslobodjenja 120", 45.358443, 19.931852);
        RouteForRideDTO rideLocation = new RouteForRideDTO(rideDepartureLocation, rideDestinationLocation);
        rideLocations.add(rideLocation);
        ride.setLocations(rideLocations);
        ride.setEstimatedTimeInMinutes(30);
        ride.setBabyTransport(false);
        ride.setPetTransport(false);
        ride.setVehicleType("STANDARD");
        List<UserForRideDTO> passengersInRide = new ArrayList<>();
        passengersInRide.add(new UserForRideDTO(1L, "uros.pocek@gmail.com"));
        passengersInRide.add(new UserForRideDTO(2L, "tamarailic11@gmail.com"));
        passengersInRide.add(new UserForRideDTO(3L, "test@gmail.com"));
        ride.setPassengers(passengersInRide);
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        RideForTransferDTO rideWithAssignedDriver = findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest);
        assertThat(rideWithAssignedDriver.getDriver()).usingRecursiveComparison().isEqualTo(closerDriver);
    }

    @Test
    @DisplayName(value = "happy path return closest driver that listens on websocket")
    public void shouldReturnCloserDriverThatListensOnWebSocket() throws NoDriverAvailableForRideException {
        Set<Driver> availableDrivers = new HashSet<>();
        Driver closerDriver = new Driver(4L, "Marko", "Markovic", null, "+381607339280", "marko@gmail.com", "Bulevar Oslobodjenja 110", "marko", true, false);
        VehicleType standard = new VehicleType(1L, "STANDARD", 80);
        Location locationOfFirstVehicle = new Location("Bulevar oslobodjenja 40", 45.258443, 19.831852);
        Vehicle driversVehicle = new Vehicle(1L, closerDriver, "Tesla", standard, "NS555UP", 4, locationOfFirstVehicle, true, true, null);
        closerDriver.setVehicle(driversVehicle);
        Location locationOfSecondVehicle = new Location("Bulevar oslobodjenja 100", 45.299, 19.899);
        Driver furtherDriver = new Driver(5L, "Mirko", "Mirkovic", null, "+381607339289", "mirko@gmail.com", "Bulevar Oslobodjenja 120", "mirko", true, false);
        Vehicle drivers2Vehicle = new Vehicle(2L, furtherDriver, "Tesla", standard, "NS555UP", 4, locationOfSecondVehicle, true, true, null);
        furtherDriver.setVehicle(drivers2Vehicle);
        availableDrivers.add(closerDriver);
        availableDrivers.add(furtherDriver);
        when(workingHoursService.getHowMuchHasDriverWorkedToday(closerDriver.getId())).thenReturn((long) (2 * 60 * 60 * 1000));
        when(workingHoursService.getHowMuchHasDriverWorkedToday(furtherDriver.getId())).thenReturn((long) (2 * 60 * 60 * 1000));
        RideForTransferDTO ride = new RideForTransferDTO();
        List<RouteForRideDTO> rideLocations = new ArrayList<>();
        LocationForRideDTO rideDepartureLocation = new LocationForRideDTO("Bulevar oslobodjenja 40", 45.258443, 19.831852);
        LocationForRideDTO rideDestinationLocation = new LocationForRideDTO("Bulevar oslobodjenja 120", 45.358443, 19.931852);
        RouteForRideDTO rideLocation = new RouteForRideDTO(rideDepartureLocation, rideDestinationLocation);
        rideLocations.add(rideLocation);
        ride.setLocations(rideLocations);
        ride.setEstimatedTimeInMinutes(30);
        ride.setBabyTransport(false);
        ride.setPetTransport(false);
        ride.setVehicleType("STANDARD");
        List<UserForRideDTO> passengersInRide = new ArrayList<>();
        passengersInRide.add(new UserForRideDTO(1L, "uros.pocek@gmail.com"));
        passengersInRide.add(new UserForRideDTO(2L, "tamarailic11@gmail.com"));
        passengersInRide.add(new UserForRideDTO(3L, "test@gmail.com"));
        ride.setPassengers(passengersInRide);
        Set<Driver> driversThatRejectedRideRequest = Collections.emptySet();
        RideForTransferDTO rideWithAssignedDriver = findDriverService.findDriverForRide(ride, driversThatRejectedRideRequest, availableDrivers);
        assertThat(rideWithAssignedDriver.getDriver()).usingRecursiveComparison().isEqualTo(closerDriver);
    }

}
