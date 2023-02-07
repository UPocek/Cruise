package com.cruise.Cruise.ride.Services;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.driver.Services.NoDriverAvailableForRideException;
import com.cruise.Cruise.helper.IHelperService;
import com.cruise.Cruise.models.Route;
import com.cruise.Cruise.models.*;
import com.cruise.Cruise.passenger.DTO.EmailsDTO;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.ride.DTO.*;
import com.cruise.Cruise.ride.Repositories.ILocationRepository;
import com.cruise.Cruise.ride.Repositories.IRideInvitationRepository;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.cruise.Cruise.ride.Repositories.IRoutesRepository;
import com.cruise.Cruise.vehicle.DTO.LocationDTO;
import com.cruise.Cruise.vehicle.DTO.VehicleToDriveDTO;
import com.cruise.Cruise.vehicle.Repositories.IVehicleTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.helpers.mail.Mail;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RideRequestService implements IRideRequestService {

    @Autowired
    private IHelperService helper;
    @Autowired
    private IFindDriverService findDriverService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private IRoutesRepository routesRepository;
    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private IVehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private IRideInvitationRepository rideInvitationRepository;
    @Autowired
    private ILocationRepository locationRepository;
    @Autowired
    private IDriverRepository driverRepository;

    @Override
    public RideForTransferDTO createRideBasic(RideDTO rideDTO) {
        if (LocalDateTime.parse(rideDTO.getStartTime()).isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't request ride in past time");
        }
        if (LocalDateTime.parse(rideDTO.getStartTime()).isAfter(LocalDateTime.now().plusMinutes(15))) {
            return createFutureRide(rideDTO);
        }
        return createPendingRide(rideDTO);
    }

    @Override
    public RideForTransferDTO createRideForNow(RideDTO rideDTO) {
        Ride ride = new Ride();
        ride.setStartTime(LocalDateTime.now());
        ride.setEndTime(LocalDateTime.now().plusMinutes((long) rideDTO.getTimeEstimation()));
        ride.setPrice(rideDTO.getPrice());
        ride.setDriver(null);

        Set<Passenger> passengers = getRidePassengers(rideDTO.getPassengers());
        if (!checkIfRideCanBeRequested(passengers)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't request ride because passenger is already in ride process");
        }
        ride.setPassengers(passengers);

        Set<Route> routes = new HashSet<>();
        for (RouteForRideDTO routeDTO : rideDTO.getLocations()) {
            Route route = findRouteOrCreate(routeDTO.getDeparture(), routeDTO.getDestination(), rideDTO.getDistance());
            routes.add(route);
        }
        ride.setRoutes(routes);

        ride.setEstimatedTime(rideDTO.getTimeEstimation());
        Set<Review> reviews = new HashSet<>();
        ride.setReviews(reviews);
        ride.setRideState("INREVIEW");
        ride.setRejection(null);
        ride.setPanic(false);
        ride.setBabyInVehicle(rideDTO.isBabyTransport());
        ride.setPetInVehicle(rideDTO.isPetTransport());
        ride.setVehicleType(getVehicleType(rideDTO.getVehicleType()));

        rideRepository.save(ride);
        rideRepository.flush();

        return new RideForTransferDTO(ride);
    }

    private RideForTransferDTO createFutureRide(RideDTO rideDTO) {
        if (LocalDateTime.parse(rideDTO.getStartTime()).isAfter(LocalDateTime.now().plusHours(5))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't request ride more then 5 hours in future");
        }
        Ride ride = new Ride();
        ride.setStartTime(LocalDateTime.parse(rideDTO.getStartTime()));
        ride.setEndTime(LocalDateTime.parse(rideDTO.getStartTime()).plusMinutes((long) rideDTO.getTimeEstimation()));
        ride.setPrice(rideDTO.getPrice());
        ride.setDriver(null);

        Set<Passenger> passengers = new HashSet<>();
        UserForRideDTO inviter = rideDTO.getPassengers().remove(0);
        Optional<Passenger> passengerOptional = passengerRepository.findByEmail(inviter.getEmail());
        if (passengerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger doesn't exist");
        }
        Passenger passenger = passengerOptional.get();
        if (!checkIfPassengerCanRequestRide(passenger)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create a ride while you have one already pending!");
        }
        passengers.add(passenger);
        ride.setPassengers(passengers);

        Set<Route> routes = new HashSet<>();
        for (RouteForRideDTO routeDTO : rideDTO.getLocations()) {
            Route route = findRouteOrCreate(routeDTO.getDeparture(), routeDTO.getDestination(), rideDTO.getDistance());
            routes.add(route);
        }
        ride.setRoutes(routes);

        ride.setEstimatedTime(rideDTO.getTimeEstimation());
        Set<Review> reviews = new HashSet<>();
        ride.setReviews(reviews);
        ride.setRideState("FUTURE");
        ride.setRejection(null);
        ride.setPanic(false);
        ride.setBabyInVehicle(rideDTO.isBabyTransport());
        ride.setPetInVehicle(rideDTO.isPetTransport());
        ride.setVehicleType(getVehicleType(rideDTO.getVehicleType()));

        rideRepository.save(ride);
        rideRepository.flush();

        return new RideForTransferDTO(ride);
    }

    @Override
    public boolean checkIfRideCanBeRequested(Set<Passenger> passengers) {
        for (Passenger passenger : passengers) {
            if (!checkIfPassengerCanRequestRide(passenger)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkIfPassengerCanRequestRide(Passenger passenger) {
        Set<Ride> passengerRides = new HashSet<>();
        Set<Ride> passengerActiveRides = rideRepository.findByRideStateAndPassengers("ACTIVE", passenger);
        Set<Ride> passengerAcceptedRides = rideRepository.findByRideStateAndPassengers("ACCEPTED", passenger);
        Set<Ride> passengerInreviewRides = rideRepository.findByRideStateAndPassengers("INREVIEW", passenger);
        Set<Ride> passengerFutureRides = rideRepository.findByRideStateAndPassengers("FUTURE", passenger);
        passengerRides.addAll(passengerActiveRides);
        passengerRides.addAll(passengerAcceptedRides);
        passengerRides.addAll(passengerInreviewRides);
        passengerRides.addAll(passengerFutureRides);
        return passengerRides.size() == 0;
    }

    @Override
    public RideForTransferDTO markRideAsRejected(RideForTransferDTO rideResponse) {
        Optional<Ride> rideResult = rideRepository.findById(rideResponse.getId());
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exists");
        }
        Ride ride = rideResult.get();
        ride.setRideState("REJECTED");
        rideRepository.save(ride);
        rideRepository.flush();
        return new RideForTransferDTO(ride);
    }

    @Override
    public RideForTransferDTO markRideAsAccepted(RideForTransferDTO rideResponse) {
        Optional<Ride> rideResult = rideRepository.findById(rideResponse.getId());
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exists");
        }
        Optional<Driver> driverResult = driverRepository.findById(rideResponse.getDriver().getId());
        if (driverResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver with that id does not exists");
        }
        Driver driver = driverResult.get();
        driver.setStatus("INRIDE");
        Ride ride = rideResult.get();
        ride.setRideState("ACCEPTED");
        ride.setDriver(driver);
        rideRepository.save(ride);
        rideRepository.flush();
        driverRepository.save(driver);
        driverRepository.flush();
        notifyForNewRideRequest(ride);
        return new RideForTransferDTO(ride);
    }

    @Override
    public RideForTransferDTO createPendingRide(RideDTO rideDTO) {
        Ride ride = new Ride();
        ride.setStartTime(LocalDateTime.parse(rideDTO.getStartTime()));
        ride.setEndTime(LocalDateTime.parse(rideDTO.getStartTime()).plusMinutes((long) rideDTO.getTimeEstimation()));
        ride.setPrice(rideDTO.getPrice());
        ride.setDriver(null);

        Set<Passenger> passengers = new HashSet<>();
        UserForRideDTO inviter = rideDTO.getPassengers().remove(0);
        Optional<Passenger> passengerOptional = passengerRepository.findByEmail(inviter.getEmail());
        if (passengerOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger doesn't exist");
        }
        Passenger passenger = passengerOptional.get();
        if (!checkIfPassengerCanRequestRide(passenger)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create a ride while you have one already pending!");
        }
        passengers.add(passenger);
        ride.setPassengers(passengers);

        Set<Route> routes = new HashSet<>();
        for (RouteForRideDTO routeDTO : rideDTO.getLocations()) {
            Route route = findRouteOrCreate(routeDTO.getDeparture(), routeDTO.getDestination(), rideDTO.getDistance());
            routes.add(route);
        }
        ride.setRoutes(routes);

        ride.setEstimatedTime(rideDTO.getTimeEstimation());
        Set<Review> reviews = new HashSet<>();
        ride.setReviews(reviews);
        ride.setRideState("PENDING");
        ride.setRejection(null);
        ride.setPanic(false);
        ride.setBabyInVehicle(rideDTO.isBabyTransport());
        ride.setPetInVehicle(rideDTO.isPetTransport());
        ride.setVehicleType(getVehicleType(rideDTO.getVehicleType()));

        rideRepository.save(ride);
        rideRepository.flush();
        return new RideForTransferDTO(ride);
    }

    @Override
    public RideForTransferDTO getRideRequest(Long id) {
        Optional<Ride> rideOptional = this.rideRepository.findById(id);
        if (rideOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride with that id doesn't exist");
        }
        Ride ride = rideOptional.get();
        return new RideForTransferDTO(ride);
    }

    @Override
    public void addPassengerToRide(String passengerEmail, Long rideId) {
        Optional<Passenger> passengerResponse = passengerRepository.findByEmail(passengerEmail);
        if (passengerResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger doesn't exist");
        }
        Passenger passenger = passengerResponse.get();

        if (!checkIfPassengerCanRequestRide(passenger)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This passenger is already in ride process");
        }

        Optional<Ride> rideResponse = rideRepository.findById(rideId);
        if (rideResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride doesn't exist");
        }
        Ride ride = rideResponse.get();

        if (!ride.getRideState().equals("PENDING") && !ride.getRideState().equals("FUTURE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride is already in process");
        }
        Set<Passenger> passengers = ride.getPassengers();
        passengers.add(passenger);
        ride.setPassengers(passengers);
        rideRepository.save(ride);
        rideRepository.flush();
    }

    @Override
    public Collection<AnswerEmailDTO> getPassengerInvites(Long id) {
        ArrayList<AnswerEmailDTO> answerEmailDTOS = new ArrayList<>();
        Collection<RideInvitation> invitations = rideInvitationRepository.findAllByPassengerId(id);
        for (RideInvitation invitation : invitations) {
            if (invitation.getCreateTime().plusSeconds(invitation.getLifespanInSeconds()).isBefore(LocalDateTime.now())) {
                deleteRideInvitation(invitation.getRide().getId(), invitation.getPassenger().getEmail());
            } else if (isRideOngoing(invitation.getRide().getId())) {
                deleteRideInvitation(invitation.getRide().getId(), invitation.getPassenger().getEmail());
            } else {
                AnswerEmailDTO answerEmailDTO = new AnswerEmailDTO();
                answerEmailDTO.setReceiverPassengerEmail(invitation.getPassenger().getEmail());
                answerEmailDTO.setSenderPassengerEmail(invitation.getInvitingPassenger().getEmail());
                answerEmailDTO.setRideId(invitation.getRide().getId());
                answerEmailDTOS.add(answerEmailDTO);
            }
        }
        return answerEmailDTOS;
    }

    @Override
    public void deleteRideInvitation(Long rideId, String receiverPassengerEmail) {
        Optional<RideInvitation> rideInvitationResponse = rideInvitationRepository.findByPassengerEmailAndRideId(receiverPassengerEmail, rideId);
        if (rideInvitationResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride invitation doesn't exist");
        }
        RideInvitation rideInvitation = rideInvitationResponse.get();

        rideInvitationRepository.delete(rideInvitation);
        rideInvitationRepository.flush();
    }

    @Override
    public void sendInvitations(EmailsDTO emails) {
        Optional<Passenger> invitingPassengerResponse = passengerRepository.findById(emails.getInvitingPassenger());
        if (invitingPassengerResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inviting passenger doesn't exist");
        }
        Passenger invitingPassenger = invitingPassengerResponse.get();

        Optional<Ride> rideResponse = rideRepository.findById(emails.getRideId());
        if (rideResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Requested ride doesn't exist");
        }
        Ride ride = rideResponse.get();

        Set<Passenger> passengers = new HashSet<>();
        for (String email : emails.getEmails()) {
            Optional<Passenger> passengerResponse = passengerRepository.findByEmail(email);
            if (passengerResponse.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invited passenger doesn't exist");
            }
            passengers.add(passengerResponse.get());
        }

        if (!checkIfRideCanBeRequested(passengers)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't send invitation because passenger is already in ride process");
        }

        for (Passenger passenger : passengers) {
            sendRideInvitation(invitingPassenger, passenger, ride);
        }
    }

    @Scheduled(fixedRate = 60000, initialDelayString = "${timing.initialScheduledDelay}")
    public void processFutureRideRequests() {
        long minutesToLookForDriverInAdvance = 15L;
        long lastCallBeforeMarkingRideAsRejected = 5L;
        Set<Ride> scheduledRides = rideRepository.findByRideState("FUTURE");
        for (Ride ride : scheduledRides) {
            if (Duration.between(LocalDateTime.now(), ride.getStartTime()).compareTo(Duration.ofMinutes(minutesToLookForDriverInAdvance)) <= 0) {
                boolean driverFound = assignDriverToRide(ride);
                if (driverFound) {
                    remindPassengersForUpcomingRide(ride);
                }
            }
            if (Duration.between(LocalDateTime.now(), ride.getStartTime()).compareTo(Duration.ofMinutes(lastCallBeforeMarkingRideAsRejected)) < 0) {
                rejectRide(ride);
            }
        }
    }

    @Scheduled(fixedRate = 60000, initialDelayString = "${timing.initialScheduledDelay}")
    public void remindPassengersForRide() {
        long reminderNotificationsInterval = 5L;
        long minutesBeforeRideForLastReminder = 4L;
        long minutesInAdvancedForFirstReminder = 10L;
        Set<Ride> scheduledRides = rideRepository.findByRideState("ACCEPTED");
        for (Ride ride : scheduledRides) {
            if (!driverStillAvailable(ride.getDriver())) {
                ride.setDriver(null);
                assignDriverToRide(ride);
            }
            if (Duration.between(LocalDateTime.now(), ride.getStartTime()).compareTo(Duration.ofMinutes(minutesBeforeRideForLastReminder)) < 0 && ride.getDriver() == null) {
                rejectRide(ride);
            } else if (Duration.between(LocalDateTime.now(), ride.getStartTime()).compareTo(Duration.ofMinutes(minutesInAdvancedForFirstReminder)) < 0 && Duration.between(LocalDateTime.now(), ride.getStartTime()).compareTo(Duration.ofMinutes(minutesBeforeRideForLastReminder)) >= 0 && Duration.between(LocalDateTime.now(), ride.getStartTime()).toMinutes() % reminderNotificationsInterval == 0) {
                remindPassengersForUpcomingRide(ride);
            }
        }
    }

    @Scheduled(fixedRate = 60000, initialDelayString = "${timing.initialScheduledDelay}")
    public void rejectStaleRideRequests() {
        long minutesAfterRideIsConsideredStale = 20L;
        Set<Ride> rideRequestsNotAssigned = rideRepository.findByRideState("INREVIEW");
        for (Ride ride : rideRequestsNotAssigned) {
            if (ride.getStartTime().compareTo(LocalDateTime.now()) < 0 && Duration.between(ride.getStartTime(), LocalDateTime.now()).compareTo(Duration.ofMinutes(minutesAfterRideIsConsideredStale)) > 0) {
                rejectRide(ride);
            }
        }
    }

    @Override
    public boolean assignDriverToRide(Ride ride) {
        try {
            RideForTransferDTO rideForTransferDTO = findDriverService.findDriverForRide(new RideForTransferDTO(ride), new HashSet<>());
            ride.setDriver(driverRepository.findById(rideForTransferDTO.getDriver().getId()).get());
            ride.setRideState("ACCEPTED");
            rideRepository.save(ride);
            rideRepository.flush();
            return true;
        } catch (NoDriverAvailableForRideException ignored) {
            return false;
        }
    }

    private void rejectRide(Ride ride) {
        notifyPassengersThatWeCantFindDriverForTheirRide(ride);
        ride.setRideState("REJECTED");
        rideRepository.save(ride);
        rideRepository.flush();
    }

    private boolean driverStillAvailable(Driver driver) {
        Collection<Driver> allActiveDrivers = driverRepository.findByActive(true);
        for (Driver activeDriver : allActiveDrivers) {
            if (Objects.equals(activeDriver.getId(), driver.getId())) {
                return true;
            }
        }
        return false;
    }

    private void remindPassengersForUpcomingRide(Ride ride) {
        for (Passenger passenger : ride.getPassengers()) {
            sendRideReminder(ride, passenger);
        }
    }

    private void notifyPassengersThatWeCantFindDriverForTheirRide(Ride ride) {
        for (Passenger passenger : ride.getPassengers()) {
            sendRideRejectedNotification(ride, passenger);
        }
    }

    private Set<Passenger> getRidePassengers(List<UserForRideDTO> passengers) {
        Set<Passenger> ridePassengers = new HashSet<>();
        for (UserForRideDTO passengerForRide : passengers) {
            Optional<Passenger> result = this.passengerRepository.findByEmail(passengerForRide.getEmail());
            if (result.isPresent()) {
                ridePassengers.add(result.get());
            }else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger doesn't exist");
            }
        }
        return ridePassengers;
    }

    public Route findRouteOrCreate(LocationForRideDTO departureDto, LocationForRideDTO destinationDto, double distance) {
        Location departure = createLocation(departureDto);
        Location destination = createLocation(destinationDto);

        Route route = this.routesRepository.findByStartLocationLongitudeAndStartLocationLatitudeAndEndLocationLongitudeAndEndLocationLatitude(departure.getLongitude(), departure.getLatitude(), destination.getLongitude(), destination.getLatitude());
        if (route == null) {
            route = createRoute(departure, destination, distance);
        }
        return route;
    }

    @Override
    public RideForTransferDTO markRideAsInReview(RideForTransferDTO rideResponse) {
        Optional<Ride> rideResult = rideRepository.findById(rideResponse.getId());
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride with that id does not exists");
        }
        Optional<Driver> driverResult = driverRepository.findById(rideResponse.getDriver().getId());
        if (driverResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver with that id does not exists");
        }
        Driver driver = driverResult.get();
        Ride ride = rideResult.get();
        ride.setDriver(driver);
        ride.setRideState("INREVIEW");
        rideRepository.save(ride);
        rideRepository.flush();
        return new RideForTransferDTO(ride);
    }

    private Route createRoute(Location departure, Location destination, double distance) {
        Route newRoute = new Route();
        newRoute.setStartLocation(departure);
        newRoute.setEndLocation(destination);
        newRoute.setDistance(distance);
        this.routesRepository.save(newRoute);
        this.routesRepository.flush();
        return newRoute;
    }

    private Location createLocation(LocationForRideDTO locationDto) {
        Location newLocation = new Location();
        newLocation.setAddress(locationDto.getAddress());
        newLocation.setLatitude(locationDto.getLatitude());
        newLocation.setLongitude(locationDto.getLongitude());
        this.locationRepository.save(newLocation);
        this.locationRepository.flush();
        return newLocation;
    }

    private VehicleType getVehicleType(String vehicleType) {
        VehicleType type = this.vehicleTypeRepository.findByName(vehicleType);
        if (type == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid vehicleType");
        }
        return type;
    }

    private void sendRideInvitation(Passenger invitingPassenger, Passenger passenger, Ride ride) {
        makeInvitation(invitingPassenger, passenger, ride);
        String confirmationLink = Objects.requireNonNull(helper.getConfigValue("INVITATION_URL")).toString();
        String fallBackMailTemplate = "You are invited to ride \n If you want to see it, click: ";
        String mailTemplate = helper.prepareMailTemplate(confirmationLink, "src/main/resources/static/invitationEmailTemplate.html", fallBackMailTemplate);
        Mail mail = helper.prepareMail(Objects.requireNonNull(helper.getConfigValue("EMAIL")).toString(), passenger.getEmail(), Objects.requireNonNull(helper.getConfigValue("EMAIL_SUBJECT")).toString(), mailTemplate);
        String apiKey = Objects.requireNonNull(helper.getConfigValue("SENDGRID_API_KEY")).toString();
        helper.sendEmail(apiKey, mail);
    }

    private void sendRideReminder(Ride ride, Passenger passenger) {
        String fallBackMailTemplate = "Reminder - your ride from " + ride.getRoutes().stream().toList().get(0).getStartLocation().getAddress() + " to " + ride.getRoutes().stream().toList().get(0).getEndLocation().getAddress() + " will start in " + (Duration.between(LocalDateTime.now(), ride.getStartTime())) + ". \n Make sure to be at the pickup location at " + ride.getStartTime();
        List<String> data = new ArrayList<>();
        data.add("from " + ride.getRoutes().stream().toList().get(0).getStartLocation().getAddress() + " to " + ride.getRoutes().stream().toList().get(0).getEndLocation().getAddress());
        data.add(String.valueOf(Duration.between(LocalDateTime.now(), ride.getStartTime())));
        data.add(ride.getDriver().getFullName());
        data.add(ride.getDriver().getVehicle().getMake() + " licence plate " + ride.getDriver().getVehicle().getLicencePlate());
        String mailTemplate = helper.prepareComplexMailTemplate(data, "src/main/resources/static/rideReminderTemplate.html", fallBackMailTemplate);
        Mail mail = helper.prepareMail(Objects.requireNonNull(helper.getConfigValue("EMAIL")).toString(), passenger.getEmail(), Objects.requireNonNull(helper.getConfigValue("RIDE_REMINDER")).toString(), mailTemplate);
        String apiKey = Objects.requireNonNull(helper.getConfigValue("SENDGRID_API_KEY")).toString();
        helper.sendEmail(apiKey, mail);
    }

    private void sendRideRejectedNotification(Ride ride, Passenger passenger) {
        String fallBackMailTemplate = "You ride was rejected - we couldn't find you a driver";
        List<String> data = new ArrayList<>();
        data.add("No drivers available at this point");
        String mailTemplate = helper.prepareComplexMailTemplate(data, "src/main/resources/static/rideRejectedTemplate.html", fallBackMailTemplate);
        Mail mail = helper.prepareMail(Objects.requireNonNull(helper.getConfigValue("EMAIL")).toString(), passenger.getEmail(), Objects.requireNonNull(helper.getConfigValue("RIDE_REJECTED")).toString(), mailTemplate);
        String apiKey = Objects.requireNonNull(helper.getConfigValue("SENDGRID_API_KEY")).toString();
        helper.sendEmail(apiKey, mail);
    }

    private void makeInvitation(Passenger invitingPassenger, Passenger passenger, Ride ride) {
        RideInvitation invitation = new RideInvitation();
        invitation.setInvitingPassenger(invitingPassenger);
        invitation.setPassenger(passenger);
        invitation.setRide(ride);
        invitation.setCreateTime(LocalDateTime.now());
        invitation.setLifespanInSeconds(Long.parseLong(Objects.requireNonNull(helper.getConfigValue("TOKEN_LIFESPAN")).toString()));
        rideInvitationRepository.save(invitation);
        rideInvitationRepository.flush();
    }

    private boolean isRideOngoing(Long id) {
        Optional<Ride> rideResponse = rideRepository.findById(id);
        if (rideResponse.isEmpty()) {
            return true;
        }
        Ride ride = rideResponse.get();
        if (!Objects.equals(ride.getRideState(), "PENDING") && !Objects.equals(ride.getRideState(), "FUTURE")) {
            return true;
        }
        return false;
    }

    private Response notifyForNewRideRequest(Ride ride) {
        String serverURL = (String) helper.getConfigValue("PYTHON_SERVER_IP");
        String url = serverURL + "/ride-request";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        Vehicle driversVehicle = ride.getDriver().getVehicle();
        VehicleToDriveDTO vehicle = new VehicleToDriveDTO(driversVehicle.getId(), ride.getDriver().getId(), new LocationDTO(driversVehicle.getLocation().getAddress(), driversVehicle.getLocation().getLatitude(), driversVehicle.getLocation().getLongitude()), ride.getRideState());
        LocationForRideDTO passengerLocation = new LocationForRideDTO(ride.getRoutes().stream().toList().get(0).getStartLocation());
        DriveToDTO driveToDTO = new DriveToDTO(ride.getId(), vehicle, passengerLocation);
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
}
