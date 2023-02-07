package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.helper.IHelperService;
import com.cruise.Cruise.models.Route;
import com.cruise.Cruise.models.*;
import com.cruise.Cruise.panic.DTO.PanicDTO;
import com.cruise.Cruise.panic.DTO.UserForPanicDTO;
import com.cruise.Cruise.panic.Repositories.IPanicRepository;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.ride.DTO.*;
import com.cruise.Cruise.ride.Repositories.IFavouriteRideRepository;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.cruise.Cruise.user.Repositories.IUserRepository;
import com.cruise.Cruise.vehicle.DTO.LocationDTO;
import com.cruise.Cruise.vehicle.DTO.VehicleToDriveDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.helpers.mail.Mail;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.domain.Sort.by;

@Service
public class RideService implements IRideService {

    @Autowired
    private IHelperService helper;
    @Autowired
    private IRideRequestService rideRequestService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IPanicRepository panicRepository;
    @Autowired
    private IFavouriteRideRepository favouriteRideRepository;


    @Override
    public PanicDTO panic(Long id, ReasonDTO reasonDTO, Principal user) {
        Optional<Ride> rideResult = rideRepository.findById(id);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exist");
        }

        Ride rideWithPanic = rideResult.get();
        Driver driverFromRide = rideWithPanic.getDriver();

        Optional<User> result = userRepository.findByEmail(user.getName());
        User userWhoPanic = result.get();
        boolean isFound = false;
        for (Passenger passenger : rideWithPanic.getPassengers()) {
            if (Objects.equals(passenger.getId(), userWhoPanic.getId())) {
                isFound = true;
                break;
            }
        }
        if (!isFound && Objects.equals(userWhoPanic.getId(), driverFromRide.getId())) {
            isFound = true;
        }
        if (!isFound)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not in ride");

        rideWithPanic.setRideState("PANIC");
        rideRepository.save(rideWithPanic);
        rideRepository.flush();

        driverFromRide.setStatus("PANIC");
        driverRepository.save(driverFromRide);
        driverRepository.flush();

        Panic panic = new Panic();
        panic.setCurrentRide(rideWithPanic);
        panic.setTime(LocalDateTime.now().toString());
        panic.setReason(reasonDTO.getReason());
        panic.setUser(userWhoPanic);
        panicRepository.save(panic);
        panicRepository.flush();

        PanicDTO panicDTO = new PanicDTO();
        panicDTO.setRide(new RideForTransferDTO(rideWithPanic));
        panicDTO.setTime(LocalDateTime.now().toString());
        panicDTO.setReason(reasonDTO.getReason());
        UserForPanicDTO userForPanicDTO = new UserForPanicDTO();
        userForPanicDTO.setName(userWhoPanic.getName());
        userForPanicDTO.setSurname(userWhoPanic.getSurname());
        userForPanicDTO.setProfilePicture(null);
        userForPanicDTO.setTelephoneNumber(userWhoPanic.getTelephoneNumber());
        userForPanicDTO.setEmail(userWhoPanic.getEmail());
        userForPanicDTO.setAddress(userWhoPanic.getAddress());
        panicDTO.setUser(userForPanicDTO);

        return panicDTO;
    }

    @Override
    public RideForTransferDTO acceptRide(Long rideId, Principal driver) {
        Optional<Ride> rideResult = rideRepository.findById(rideId);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
        Ride ride = rideResult.get();
        if (!ride.getRideState().equals("INREVIEW")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot accept a ride that is not in status INREVIEW!");
        }
        Optional<Driver> driverForRideResult = driverRepository.findByEmail(driver.getName());
        if (driverForRideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
        Driver driverForRide = driverForRideResult.get();
        ride.setDriver(driverForRide);
        ride.setRideState("ACCEPTED");
        driverForRide.setStatus("INRIDE");
        rideRepository.save(ride);
        rideRepository.flush();
        driverRepository.save(driverForRide);
        driverRepository.flush();
        return new RideForTransferDTO(ride);
    }

    @Override
    public RideForTransferDTO startRide(Long rideId) {
        Optional<Ride> rideResult = rideRepository.findById(rideId);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exist");
        }
        Ride ride = rideResult.get();
        if (!ride.getRideState().equalsIgnoreCase("ACCEPTED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot start a ride that is not in status ACCEPTED!");
        ride.setRideState("ACTIVE");
        rideRepository.save(ride);
        rideRepository.flush();
        notifyCarToDriveFromTo(ride);
        RideForTransferDTO startedRide = new RideForTransferDTO(ride);
        for (UserForRideDTO passenger : startedRide.getPassengers()) {
            simpMessagingTemplate.convertAndSend("/socket-out/ride-started/" + passenger.getId(), startedRide);
        }
        return startedRide;
    }

    @Override
    public RideForTransferDTO endRide(Long id) {
        Optional<Ride> rideResult = rideRepository.findById(id);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exits!");
        }
        Ride ride = rideResult.get();
        if (!ride.getRideState().equals("ACTIVE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot end a ride that is not in status ACTIVE!");
        }
        ride.setRideState("FINISHED");
        ride.setEndTime(LocalDateTime.now());
        Driver driver = ride.getDriver();
        driver.setStatus("FREE");
        driver.setAddress(ride.getRoutes().stream().toList().get(0).getEndLocation().getAddress());
        rideRepository.save(ride);
        rideRepository.flush();
        driverRepository.save(driver);
        driverRepository.flush();
        RideForTransferDTO endedRide = new RideForTransferDTO(ride);
        for (UserForRideDTO passenger : endedRide.getPassengers()) {
            simpMessagingTemplate.convertAndSend("/socket-out/ride-ended/" + passenger.getId(), endedRide);
        }
        return endedRide;
    }

    @Override
    public RideForTransferDTO cancelRideWithExplanation(Long rideId, ReasonDTO reason) {
        Optional<Ride> rideResult = rideRepository.findById(rideId);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
        Ride ride = rideResult.get();
        if (!ride.getRideState().equals("ACCEPTED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot cancel a ride that is not in status ACCEPTED!");
        }
        ride.setRideState("REJECTED");
        ride.setEndTime(LocalDateTime.now());
        ride.setRejection(new Rejection(ride, reason.getReason(), ride.getDriver(), LocalDateTime.now()));
        Driver driver = ride.getDriver();
        driver.setStatus("FREE");
        rideRepository.save(ride);
        rideRepository.flush();
        driverRepository.save(driver);
        driverRepository.flush();
        RideForTransferDTO canceledRide = new RideForTransferDTO(ride);
        for (UserForRideDTO passenger : canceledRide.getPassengers()) {
            simpMessagingTemplate.convertAndSend("/socket-out/cancel/" + passenger.getId(), canceledRide);
        }
        return canceledRide;
    }

    @Override
    public RideForTransferDTO cancelExistingRide(Long rideId) {
        Optional<Ride> rideResult = rideRepository.findById(rideId);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
        Ride ride = rideResult.get();
        if (!ride.getRideState().equals("ACCEPTED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot withdraw from a ride that is not in status ACCEPTED!");
        }
        ride.setRideState("CANCELED");
        ride.setEndTime(LocalDateTime.now());
        Driver driver = ride.getDriver();
        driver.setStatus("FREE");
        rideRepository.save(ride);
        rideRepository.flush();
        driverRepository.save(driver);
        driverRepository.flush();
        RideForTransferDTO canceledRide = new RideForTransferDTO(ride);
        simpMessagingTemplate.convertAndSend("/socket-out/withdraw/" + ride.getDriver().getId(), canceledRide);
        return canceledRide;
    }

    @Override
    public RideForTransferDTO getRideForDriverByStatus(Long driverId, String status) {
        Set<Ride> rides = rideRepository.findByRideStateAndDriverId(status, driverId, by("startTime"));
        for (Ride ride : rides) {
            return new RideForTransferDTO(ride);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active ride does not exist!");
    }

    @Override
    public RideForTransferDTO getRideForPassengerByStatus(Long passengerId, String status) {
        Set<Ride> rides = rideRepository.findByRideStateAndPassengersId(status, passengerId, by("startTime"));
        for (Ride ride : rides) {
            return new RideForTransferDTO(ride);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active ride does not exist!");
    }

    @Override
    public void notifyPassengersThatDriverArrivedToPickUpLocation(Long rideId) {
        Optional<Ride> rideResult = rideRepository.findById(rideId);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exist");
        }
        Ride ride = rideResult.get();
        Set<Passenger> passengers = ride.getPassengers();
        for (Passenger passenger : passengers) {
            sendDriverArrivedNotification(ride, passenger);
        }
    }

    @Override
    public FavouriteRideDTO addNewFavouriteRide(FavouriteRideBasicDTO favouriteRide, Principal user) {
        Optional<Passenger> result = passengerRepository.findByEmail(user.getName());
        Passenger adder = result.get();
        if (adder.getFavouriteRides().size() >= 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of favorite rides cannot exceed 10!");
        }
        FavouriteRide newFavouriteRide = new FavouriteRide();
        newFavouriteRide.setFavoriteName(favouriteRide.getFavoriteName());
        List<Route> locations = new ArrayList<>();
        for (RouteForRideDTO routeDTO : favouriteRide.getLocations()) {
            Route route = rideRequestService.findRouteOrCreate(routeDTO.getDeparture(), routeDTO.getDestination(), favouriteRide.getDistance());
            locations.add(route);
        }
        newFavouriteRide.setLocations(locations);
        List<Passenger> passengers = new ArrayList<>();
        for (UserForRideDTO passenger : favouriteRide.getPassengers()) {
            Optional<Passenger> passengerResult = passengerRepository.findByEmail(passenger.getEmail());
            if (passengerResult.isPresent()) {
                passengers.add(passengerResult.get());
            }
        }
        newFavouriteRide.setPassengers(passengers);
        newFavouriteRide.setBabyTransport(favouriteRide.isBabyTransport());
        newFavouriteRide.setPetTransport(favouriteRide.isPetTransport());
        newFavouriteRide.setVehicleType(favouriteRide.getVehicleType());
        newFavouriteRide.setDistance(favouriteRide.getDistance());
        FavouriteRide biggest = favouriteRideRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).get(0);
        newFavouriteRide.setId(biggest.getId() + 1);
        adder.addFavouriteRide(newFavouriteRide);

        passengerRepository.save(adder);
        passengerRepository.flush();
        favouriteRideRepository.flush();

        newFavouriteRide = adder.getFavouriteRides().stream().max(Comparator.comparing(FavouriteRide::getId)).get();
        return new FavouriteRideDTO(newFavouriteRide);
    }

    @Override
    public Collection<FavouriteRideDTO> getAllFavouriteRidesByPassenger(Principal user) {
        Set<FavouriteRideDTO> passengerFavouriteRides = new HashSet<>();
        for (FavouriteRide ride : passengerRepository.findAllPassengerFavouriteRides(user.getName())) {
            passengerFavouriteRides.add(new FavouriteRideDTO(ride));
        }
        return passengerFavouriteRides;
    }

    @Override
    public void deleteFavouriteRide(Long rideId, Principal user) {
        Optional<FavouriteRide> favouriteRideResult = favouriteRideRepository.findById(rideId);
        if (favouriteRideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite location does not exist!");
        }
        FavouriteRide favouriteRide = favouriteRideResult.get();
        Optional<Passenger> passengerResult = passengerRepository.findByEmail(user.getName());
        Passenger remover = passengerResult.get();
        boolean found = false;
        for (FavouriteRide ride : remover.getFavouriteRides()) {
            if (ride.getId() == rideId)
                found = true;
        }
        if (!found)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This user has not that favourite location!");
        remover.removeFavouriteRide(favouriteRide);
        favouriteRideRepository.deleteById(rideId);
        passengerRepository.save(remover);
        passengerRepository.flush();
        favouriteRideRepository.flush();
    }

    private Response notifyCarToDriveFromTo(Ride ride) {
        String serverURL = (String) helper.getConfigValue("PYTHON_SERVER_IP");
        String url = serverURL + "/ride-request";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        Vehicle driversVehicle = ride.getDriver().getVehicle();
        VehicleToDriveDTO vehicle = new VehicleToDriveDTO(driversVehicle.getId(), ride.getDriver().getId(), new LocationDTO(driversVehicle.getLocation().getAddress(), driversVehicle.getLocation().getLatitude(), driversVehicle.getLocation().getLongitude()), ride.getRideState());
        LocationForRideDTO destinationLocation = new LocationForRideDTO(ride.getRoutes().stream().toList().get(0).getEndLocation());
        DriveToDTO driveToDTO = new DriveToDTO(ride.getId(), vehicle, destinationLocation);
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(driveToDTO);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request;
        try {
            request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
        } catch (Exception e) {
            return null;
        }
        try (Response response = client.newCall(request).execute()) {
            return response;
        } catch (Exception ex) {
            return null;
        }
    }

    private void sendDriverArrivedNotification(Ride ride, Passenger passenger) {
        String fallBackMailTemplate = "Driver " + ride.getDriver().getFullName() + " has arrived to pick-up location \n For your ride from " + ride.getRoutes().stream().toList().get(0).getStartLocation().getAddress() + " to " + ride.getRoutes().stream().toList().get(0).getEndLocation().getAddress() + " \n Vehicle: " + ride.getDriver().getVehicle().getMake() + " with licence plate: " + ride.getDriver().getVehicle().getLicencePlate();
        List<String> data = new ArrayList<>();
        data.add(ride.getDriver().getFullName());
        data.add("from " + ride.getRoutes().stream().toList().get(0).getStartLocation().getAddress() + " to " + ride.getRoutes().stream().toList().get(0).getEndLocation().getAddress());
        data.add(ride.getDriver().getVehicle().getMake() + " with licence plate: " + ride.getDriver().getVehicle().getLicencePlate());
        String mailTemplate = helper.prepareComplexMailTemplate(data, "src/main/resources/static/driverArrivedTemplate.html", fallBackMailTemplate);
        Mail mail = helper.prepareMail(Objects.requireNonNull(helper.getConfigValue("EMAIL")).toString(), passenger.getEmail(), Objects.requireNonNull(helper.getConfigValue("DRIVER_ARRIVED_SUBJECT")).toString(), mailTemplate);
        String apiKey = Objects.requireNonNull(helper.getConfigValue("SENDGRID_API_KEY")).toString();
        helper.sendEmail(apiKey, mail);
    }

}
