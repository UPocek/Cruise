package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.driver.Services.ICalculateWorkingHoursService;
import com.cruise.Cruise.driver.Services.NoDriverAvailableForRideException;
import com.cruise.Cruise.models.Driver;
import com.cruise.Cruise.models.Location;
import com.cruise.Cruise.models.Ride;
import com.cruise.Cruise.ride.DTO.LocationForRideDTO;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;
import com.cruise.Cruise.ride.DTO.UserForRideDTO;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FindDriverService implements IFindDriverService {

    @Autowired
    private ICalculateWorkingHoursService workingHoursService;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IRideRepository rideRepository;

    @Override
    public RideForTransferDTO findDriverForRide(RideForTransferDTO rideRequest, Set<Driver> driversThatRejectedRideRequest) throws NoDriverAvailableForRideException {
        Collection<Driver> drivers = getAllActiveDrivers();
        return applyFilters(drivers, rideRequest, driversThatRejectedRideRequest);
    }

    // Android
    @Override
    public RideForTransferDTO findDriverForRide(RideForTransferDTO rideRequest, Set<Driver> driversThatRejectedRideRequest, Set<Driver> driversListeningOnWebSocket) throws NoDriverAvailableForRideException {
        Collection<Driver> drivers = driversListeningOnWebSocket;
        return applyFilters(drivers, rideRequest, driversThatRejectedRideRequest);
    }

    private RideForTransferDTO applyFilters(Collection<Driver> drivers, RideForTransferDTO rideRequest, Set<Driver> driversThatRejectedRideRequest) throws NoDriverAvailableForRideException {
        filterOnlyFreeDrivers(drivers);
        filterOnlyDriversWithVehicle(drivers);
        filterOnlyNonBlockedDrivers(drivers);
        filterOnlyDriversThatHavenAlreadyRejectedRideRequest(drivers, driversThatRejectedRideRequest);
        filterOnlyDriversWithAdequateVehicle(drivers, rideRequest);
        filterOnlyDriversWhoseWorkingTimeDoesNotExpireDuringRide(drivers, rideRequest);
        filterOnlyLocalDrivers(drivers, rideRequest);
        Collection<Driver> finalSortedDrivers = sortDriversByHeuristic(drivers, rideRequest);

        Driver driver = finalSortedDrivers.iterator().next();
        rideRequest.setDriver(new UserForRideDTO(driver.getId(), driver.getEmail()));
        return rideRequest;
    }

    private Collection<Driver> getAllActiveDrivers() {
        return driverRepository.findByActive(true);
    }

    private void filterOnlyFreeDrivers(Collection<Driver> drivers) throws NoDriverAvailableForRideException {
        drivers.removeIf(driver -> !driver.getStatus().equals("FREE"));
        if (drivers.isEmpty()) {
            drivers = getAllActiveDrivers();
            filterOnlyDriversThatHaveNotBeenAssignedToFutureRide(drivers);
        }
    }

    private void filterOnlyDriversThatHaveNotBeenAssignedToFutureRide(Collection<Driver> drivers) throws NoDriverAvailableForRideException {
        Set<Ride> ridesToConsider = rideRepository.findByRideState("ACCEPTED");
        for (Ride ride : ridesToConsider) {
            drivers.removeIf(driver -> Objects.equals(driver.getId(), ride.getDriver().getId()));
        }
        if (drivers.isEmpty()) {
            throw new NoDriverAvailableForRideException("No free drivers available");
        }
    }

    private void filterOnlyNonBlockedDrivers(Collection<Driver> drivers) throws NoDriverAvailableForRideException {
        drivers.removeIf(driver -> driver.isBlocked());
        if (drivers.isEmpty()) {
            throw new NoDriverAvailableForRideException("No not blocked drivers available");
        }
    }

    private void filterOnlyDriversThatHavenAlreadyRejectedRideRequest(Collection<Driver> drivers, Collection<Driver> driversThatRejectedRideRequest) throws NoDriverAvailableForRideException {
        List<String> driversToRemove = new ArrayList();

        for (Driver driver : drivers) {
            for (Driver rejector : driversThatRejectedRideRequest) {
                if (driver.getEmail().equals(rejector.getEmail())) {
                    driversToRemove.add(driver.getEmail());
                    break;
                }
            }
        }
        drivers.removeIf(driver -> driversToRemove.contains(driver.getEmail()));

        if (drivers.isEmpty()) {
            throw new NoDriverAvailableForRideException("No free drivers available");
        }
    }

    private void filterOnlyDriversWithAdequateVehicle(Collection<Driver> drivers, RideForTransferDTO rideRequest) throws NoDriverAvailableForRideException {
        drivers.removeIf(driver -> rideRequest.getBabyTransport() && !driver.getVehicle().getBabiesAllowed());
        drivers.removeIf(driver -> rideRequest.getPetTransport() && !driver.getVehicle().getPetsAllowed());
        drivers.removeIf(driver -> !driver.getVehicle().getType().getName().equals(rideRequest.getVehicleType()));
        drivers.removeIf(driver -> driver.getVehicle().getSeatsNum() < rideRequest.getPassengers().size());
        if (drivers.isEmpty()) {
            throw new NoDriverAvailableForRideException("No adequate drivers available");
        }
    }

    private void filterOnlyDriversWhoseWorkingTimeDoesNotExpireDuringRide(Collection<Driver> drivers, RideForTransferDTO rideRequest) throws NoDriverAvailableForRideException {
        int millisecondsInMinute = 60000;
        drivers.removeIf(driver -> workingHoursService.getHowMuchHasDriverWorkedToday(driver.getId()) + (long) rideRequest.getEstimatedTimeInMinutes() * millisecondsInMinute >= Duration.ofHours(8).toMillis());
        if (drivers.isEmpty()) {
            throw new NoDriverAvailableForRideException("No available drivers");
        }
    }

    private void filterOnlyDriversWithVehicle(Collection<Driver> drivers) throws NoDriverAvailableForRideException {
        drivers.removeIf(driver -> driver.getVehicle() == null);
        if (drivers.isEmpty()) {
            throw new NoDriverAvailableForRideException("No available drivers");
        }
    }

    private void filterOnlyLocalDrivers(Collection<Driver> drivers, RideForTransferDTO rideRequest) throws NoDriverAvailableForRideException {
        drivers.removeIf(driver -> !isDriverLocal(driver.getVehicle().getLocation(), rideRequest.getLocations().get(0).getDeparture()));
        if (drivers.isEmpty()) {
            throw new NoDriverAvailableForRideException("No close enough drivers available");
        }
    }

    private boolean isDriverLocal(Location driversLocation, LocationForRideDTO ridesLocation) {
        double localityBounds = 0.3;
        return Math.abs(driversLocation.getLatitude() - ridesLocation.getLatitude()) < localityBounds || Math.abs(driversLocation.getLongitude() - ridesLocation.getLongitude()) < localityBounds;
    }

    private Collection<Driver> sortDriversByHeuristic(Collection<Driver> drivers, RideForTransferDTO rideRequest) throws NoDriverAvailableForRideException {
        if (freeDriversAvailable(drivers)) {
            List<Driver> remainingDrivers = new ArrayList<>(drivers.stream().toList());
            remainingDrivers.sort(new DriverDistanceComparator(rideRequest.getLocations().get(0).getDeparture()));
            return remainingDrivers;
        }
        filterOnlyDriversThatHaveNotBeenAssignedToFutureRide(drivers);
        Map<Long, Driver> sortedDrivers = new TreeMap<>();
        for (Driver driver : drivers) {
            Set<Ride> driversRides = new HashSet<>();
            Set<Ride> driversActiveRides = rideRepository.findByRideStateAndDriver("ACTIVE", driver);
            Set<Ride> driversAcceptedRides = rideRepository.findByRideStateAndDriver("ACCEPTED", driver);
            driversRides.addAll(driversActiveRides);
            driversRides.addAll(driversAcceptedRides);
            if (driversRides.size() == 1) {
                Ride ride = driversRides.iterator().next();
                long timeRemaining = Duration.between(LocalDateTime.now(), ride.getStartTime().plusMinutes((long) ride.getEstimatedTime())).toMillis();
                sortedDrivers.put(timeRemaining, driver);
            }
        }
        if (sortedDrivers.isEmpty()) {
            throw new NoDriverAvailableForRideException("No drivers available");
        }
        return sortedDrivers.values();
    }


    private boolean freeDriversAvailable(Collection<Driver> drivers) {
        for (Driver driver : drivers) {
            if (driver.getStatus().equals("FREE")) {
                return true;
            }
        }
        return false;
    }

}

class DriverDistanceComparator implements Comparator<Driver> {

    private final LocationForRideDTO location;

    DriverDistanceComparator(LocationForRideDTO locationForRideDTO) {
        this.location = locationForRideDTO;
    }

    @Override
    public int compare(Driver driver1, Driver driver2) {
        // elements are sorted in reverse order
        if (getEuclideanDistance(driver1, this.location) < getEuclideanDistance(driver2, this.location)) {
            return -1;
        } else if (getEuclideanDistance(driver1, this.location) > getEuclideanDistance(driver2, this.location)) {
            return 1;
        } else {
            return 0;
        }
    }

    private double getEuclideanDistance(Driver driver, LocationForRideDTO location) {
        return Math.sqrt(Math.pow(driver.getVehicle().getLocation().getLatitude() - location.getLatitude(), 2) + Math.pow(driver.getVehicle().getLocation().getLongitude() - location.getLongitude(), 2));
    }
}
