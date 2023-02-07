package com.cruise.Cruise.driver.Services;

import com.cruise.Cruise.driver.DTO.*;
import com.cruise.Cruise.driver.Repositories.IDocumentRepository;
import com.cruise.Cruise.driver.Repositories.IDriverInfoRequestRepository;
import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.helper.IHelperService;
import com.cruise.Cruise.models.*;
import com.cruise.Cruise.ride.DTO.RideForTransferDTO;
import com.cruise.Cruise.ride.DTO.RideForUserDTO;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.cruise.Cruise.user.DTO.AllHistoryItemsDTO;
import com.cruise.Cruise.user.Repositories.IUserRepository;
import com.cruise.Cruise.vehicle.DTO.LocationDTO;
import com.cruise.Cruise.vehicle.DTO.VehicleDTO;
import com.cruise.Cruise.vehicle.DTO.VehicleToDriveDTO;
import com.cruise.Cruise.vehicle.Repositories.IVehicleRepository;
import com.cruise.Cruise.vehicle.Repositories.IVehicleTypeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DriverService implements IDriverService {

    @Autowired
    private IHelperService helper;
    @Autowired
    private ICalculateWorkingHoursService workingHoursService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IDriverInfoRequestRepository driverInfoRequestRepository;
    @Autowired
    private IVehicleRepository vehicleRepository;
    @Autowired
    private IDocumentRepository documentRepository;
    @Autowired
    private IVehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private IRideRepository rideRepository;

    @Override
    public Map<String, Object> getAllDrivers() {
        Map<String, Object> result = new HashMap<>();
        List<RegisteredDriverDTO> drivers = new ArrayList<>();

        List<Driver> allDrivers = driverRepository.findAll();
        for (Driver driver : allDrivers) {
            drivers.add(new RegisteredDriverDTO(driver.getId(), driver.getName(), driver.getSurname(), driver.getProfilePicture().getPictureContent(), driver.getTelephoneNumber(), driver.getEmail(), driver.getAddress(), driver.isBlocked()));
        }

        result.put("totalCount", allDrivers.size());
        result.put("results", drivers);

        return result;
    }

    @Override
    public RegisteredDriverDTO getById(Long id) {
        Optional<Driver> result = driverRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist.");
        }
        Driver driver = result.get();
        return new RegisteredDriverDTO(driver.getId(), driver.getName(), driver.getSurname(), driver.getProfilePicture().getPictureContent(), driver.getTelephoneNumber(), driver.getEmail(), driver.getAddress(), driver.isBlocked());
    }

    @Override
    public RegisteredDriverDTO getByEmail(String email) {
        Optional<Driver> driverResult = driverRepository.findByEmail(email);
        if (driverResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist.");
        }
        Driver driver = driverResult.get();
        return new RegisteredDriverDTO(driver.getId(), driver.getName(), driver.getSurname(), driver.getProfilePicture().getPictureContent(), driver.getTelephoneNumber(), driver.getEmail(), driver.getAddress(), driver.isBlocked());
    }

    @Override
    public List<DocumentDTO> getDocumentsByDriverId(Long id) {
        Optional<Driver> driverResponse = driverRepository.findById(id);
        if (driverResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist.");
        }
        List<DocumentDTO> documentDTOS = new ArrayList<>();
        Driver driver = driverResponse.get();
        for (Document document : driver.getDocuments()) {
            documentDTOS.add(new DocumentDTO(document));
        }

        return documentDTOS;
    }

    @Override
    public VehicleDTO getVehicleById(Long id) {
        Optional<Driver> result = driverRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
        Driver driver = result.get();
        Vehicle driversVehicle = driver.getVehicle();
        if (driversVehicle == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vehicle is not assigned!");
        }

        return new VehicleDTO(driversVehicle.getId(), driversVehicle.getDriver().getId(), driversVehicle.getMake(), driversVehicle.getType().getName(), driversVehicle.getLicencePlate(), driversVehicle.getSeatsNum(), new LocationDTO(driversVehicle.getLocation().getAddress(), driversVehicle.getLocation().getLatitude(), driversVehicle.getLocation().getLongitude()), driversVehicle.getBabiesAllowed(), driversVehicle.getPetsAllowed());
    }

    @Override
    public DriversRideListDTO getRidesById(Long id, int page, int size, String sort, String from, String to) {
        Optional<Driver> result = driverRepository.findById(id);
        if (result.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        Driver driver = result.get();

        DriversRideListDTO list = new DriversRideListDTO();
        List<DriversRideDTO> rides = new ArrayList<>();
//        Set<Ride> allRides = rideRepository.findByDriver(driver);
////        allRides.removeIf(ride -> ride.getStartTime().isBefore(LocalDateTime.parse(from)) || ride.getEndTime().isAfter(LocalDateTime.parse(to)));
//        Iterator<Ride> iter = allRides.iterator();
//        int i = 0;
//        while (iter.hasNext()) {
//            Ride ride = iter.next();
//            if (!Objects.equals(ride.getRideState(), "FINISHED")) continue;
//            if (ride.getStartTime().isBefore(LocalDateTime.parse(from)) || ride.getEndTime().isAfter(LocalDateTime.parse(to)))
//                continue;
//            if (page == -1 || size == -1)
//                rides.add(new DriversRideDTO(ride));
//            else if (i >= page * size && i < (page + 1) * size)
//                rides.add(new DriversRideDTO(ride));
//            i++;
//        }

        LocalDateTime fromDate;
        LocalDateTime toDate;
        if (from.equals("")) {
            fromDate = LocalDateTime.of(1970, 1, 1, 1, 1);
        } else {
            fromDate = LocalDateTime.parse(from);
        }
        if (to.equals("")) {
            toDate = LocalDateTime.now();
        } else {
            toDate = LocalDateTime.parse(to);
        }
        if (sort.equals("")) {
            sort = "startTime-asc";
        }

        String[] sortTokens = sort.split("-");
        Sort sorter = Sort.by(Sort.Direction.ASC, sortTokens[0]);
        if (sortTokens[1].equals("desc")) {
            sorter = Sort.by(Sort.Direction.DESC, sortTokens[0]);
        }

        List<DriversRideDTO> driverRides = rideRepository.findAllDriverRides(driver.getId(), fromDate, toDate, sorter);

        for (int i = 0; i < driverRides.size(); i++) {
            if (page == -1 || size == -1)
                rides.add(driverRides.get(i));
            else if (i >= page * size && i < (page + 1) * size)
                rides.add(driverRides.get(i));
        }
        list.setResults(rides);
        list.setTotalCount(rides.size());
        return list;
    }


    @Override
    public RegisteredDriverDTO createNewDriver(CreateDriverDTO newDriver) {
        Optional<User> userResponse = userRepository.findByEmail(newDriver.getEmail());
        if (userResponse.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with that email already exists!");
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(newDriver.getPassword());

        Driver driver = new Driver(newDriver, encodedPassword);
        driverRepository.save(driver);
        driverRepository.flush();

        return new RegisteredDriverDTO(driver);
    }

    @Override
    public DocumentDTO addDriverDocument(Long id, CreateDocumentDTO createDocument) {
        Optional<Driver> potentialDriver = driverRepository.findById(id);
        if (potentialDriver.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist!");
        }
        Driver driver = potentialDriver.get();
        Document document = new Document();
        document.setDriver(driver);
        document.setName(createDocument.getName());

        Picture documentPicture = new Picture();
        documentPicture.setPictureContent(createDocument.getDocumentImage());
        document.setDocumentImage(documentPicture);

        driver.addDocument(document);
        driverRepository.save(driver);
        driverRepository.flush();

        return new DocumentDTO(document);
    }

    @Override
    public VehicleDTO addDriversVehicle(Long id, CreateVehicleDTO newVehicle) {
        Vehicle vehicle = new Vehicle();
        Optional<Driver> response = driverRepository.findById(id);
        if (response.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist");
        }
        Driver driver = response.get();
        if (driver.getVehicle() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver already has a vehicle assigned");
        }
        vehicle.setDriver(driver);
        vehicle.setMake(newVehicle.getModel());
        VehicleType vehicleType = vehicleTypeRepository.findByName(newVehicle.getVehicleType());
        if (vehicleType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data. Non-existent vehicle type.");
        }
        vehicle.setType(vehicleType);
        vehicle.setLicencePlate(newVehicle.getLicenseNumber());
        vehicle.setSeatsNum(newVehicle.getPassengerSeats());
        vehicle.setLocation(new Location(newVehicle.getCurrentLocation().getAddress(), newVehicle.getCurrentLocation().getLatitude(), newVehicle.getCurrentLocation().getLongitude()));
        vehicle.setBabiesAllowed(newVehicle.getBabyTransport());
        vehicle.setPetsAllowed(newVehicle.getPetTransport());

        vehicleRepository.save(vehicle);
        vehicleRepository.flush();

        return new VehicleDTO(vehicle.getId(), vehicle.getDriver().getId(), vehicle.getMake(), vehicle.getType().getName(), vehicle.getLicencePlate(), vehicle.getSeatsNum(), new LocationDTO(vehicle.getLocation().getAddress(), vehicle.getLocation().getLatitude(), vehicle.getLocation().getLongitude()), vehicle.getBabiesAllowed(), vehicle.getPetsAllowed());
    }

    @Override
    public void setDriverActivity(Long id, boolean activityStatus) {
        Optional<Driver> result = driverRepository.findById(id);
        if (result.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist");
        }
        Driver driver = result.get();
        if (activityStatus && !driver.isActive()) {
            workingHoursService.createWorkingHour(driver, LocalDateTime.now());
        }
        driver.setActive(activityStatus);
        driver.setStatus("FREE");
        notifyDriverActivityChange(activityStatus, driver);
        driverRepository.save(driver);
        driverRepository.flush();
    }

    @Override
    public VehicleDTO updateDriversVehicle(Long id, CreateVehicleDTO newVehicle) {
        Optional<Driver> response = driverRepository.findById(id);
        if (response.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist");
        }
        Driver driver = response.get();
        Vehicle vehicle = driver.getVehicle();
        vehicle.setMake(newVehicle.getModel());
        VehicleType vehicleType = vehicleTypeRepository.findByName(newVehicle.getVehicleType());
        if (vehicleType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data. Non-existent vehicle type.");
        }
        vehicle.setType(vehicleType);
        vehicle.setLicencePlate(newVehicle.getLicenseNumber());
        vehicle.setSeatsNum(newVehicle.getPassengerSeats());
        vehicle.setLocation(new Location(newVehicle.getCurrentLocation().getAddress(), newVehicle.getCurrentLocation().getLatitude(), newVehicle.getCurrentLocation().getLongitude()));
        vehicle.setBabiesAllowed(newVehicle.getBabyTransport());
        vehicle.setPetsAllowed(newVehicle.getPetTransport());

        vehicleRepository.save(vehicle);
        vehicleRepository.flush();

        return new VehicleDTO(vehicle.getId(), vehicle.getDriver().getId(), vehicle.getMake(), vehicle.getType().getName(), vehicle.getLicencePlate(), vehicle.getSeatsNum(), new LocationDTO(vehicle.getLocation().getAddress(), vehicle.getLocation().getLatitude(), vehicle.getLocation().getLongitude()), vehicle.getBabiesAllowed(), vehicle.getPetsAllowed());
    }

    @Override
    public RegisteredDriverDTO updateDriver(Long id, CreateDriverDTO driverDTO) {
        Optional<Driver> driverResponse = driverRepository.findById(id);
        if (driverResponse.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver doesn't exist");
        }

        Driver driver = driverResponse.get();
        Optional<User> userOptional = userRepository.findByEmail(driverDTO.getEmail());
        if (!driver.getEmail().equals(driverDTO.getEmail()) && userOptional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "That email is already taken");
        }

        driver.setName(driverDTO.getName());
        driver.setSurname(driverDTO.getSurname());
        if (driverDTO.getProfilePicture() != null) {
            Picture profilePicture = driver.getProfilePicture();
            profilePicture.setPictureContent(driverDTO.getProfilePicture());
            driver.setProfilePicture(profilePicture);
        }
        driver.setTelephoneNumber(driverDTO.getTelephoneNumber());
        driver.setEmail(driverDTO.getEmail());
        driver.setAddress(driverDTO.getAddress());
        driverRepository.save(driver);
        driverRepository.flush();
        return new RegisteredDriverDTO(driver.getId(), driver.getName(), driver.getSurname(), driver.getProfilePicture().getPictureContent(), driver.getTelephoneNumber(), driver.getEmail(), driver.getAddress());
    }

    @Override
    public List<DriverInfoRequestDTO> getAllRequests() {
        List<DriverInfoRequestDTO> driverInfoRequestDTOs = new ArrayList<DriverInfoRequestDTO>();
        List<DriverInfoRequest> driverInfoRequests = driverInfoRequestRepository.findAll();
        for (DriverInfoRequest req : driverInfoRequests) {
            DriverInfoRequestDTO dto = new DriverInfoRequestDTO();
            dto.setId(req.getId());
            dto.setDriverId(req.getDriverId());
            dto.setTime(req.getTime());
            dto.setName(req.getName());
            dto.setSurname(req.getSurname());
            dto.setProfilePicture(req.getProfilePicture());
            dto.setTelephoneNumber(req.getTelephoneNumber());
            dto.setEmail(req.getEmail());
            dto.setAddress(req.getAddress());
            driverInfoRequestDTOs.add(dto);
        }
        return driverInfoRequestDTOs;
    }

    @Override
    public void deleteRequest(Long id) {
        driverInfoRequestRepository.deleteById(id);
        driverInfoRequestRepository.flush();
    }

    @Override
    public Collection<VehicleToDriveDTO> getAllActiveVehicles() {
        Collection<Driver> allActiveDrivers = getAllActiveDrivers();
        Set<VehicleToDriveDTO> allActiveVehicles = new HashSet<>();
        for (Driver driver : allActiveDrivers) {
            Vehicle vehicle = driver.getVehicle();
            if (vehicle != null) {
                allActiveVehicles.add(new VehicleToDriveDTO(vehicle.getId(), vehicle.getDriver().getId(), new LocationDTO(vehicle.getLocation().getAddress(), vehicle.getLocation().getLatitude(), vehicle.getLocation().getLongitude()), driver.getStatus()));
            }
        }
        return allActiveVehicles;
    }

    private Collection<Driver> getAllActiveDrivers() {
        return driverRepository.findByActive(true);
    }

    @Override
    public RegisteredDriverDTO block(String email, boolean block) {
        Optional<Driver> driverResult = driverRepository.findByEmail(email);
        if (driverResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist");
        }
        Driver driver = driverResult.get();
        driver.setBlocked(block);
        driverRepository.save(driver);
        driverRepository.flush();
        return new RegisteredDriverDTO(driver.getId(), driver.getName(), driver.getSurname(), driver.getProfilePicture().getPictureContent(), driver.getTelephoneNumber(), driver.getEmail(), driver.getAddress(), driver.isBlocked());
    }

    @Override
    public DriverInfoRequest requestChanges(Long id, CreateDriverDTO driverDTO) {
        DriverInfoRequest driverInfoRequest = new DriverInfoRequest();
        Optional<Driver> driverResult = driverRepository.findByEmail(driverDTO.getEmail());
        if (driverResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exits!");
        }
        Driver driver = driverResult.get();
        driverInfoRequest.setTime(LocalDateTime.now().toString());
        driverInfoRequest.setDriverId(driver.getId());
        driverInfoRequest.setName(driverDTO.getName());
        driverInfoRequest.setSurname(driverDTO.getSurname());
        driverInfoRequest.setProfilePicture(driverDTO.getProfilePicture());
        driverInfoRequest.setTelephoneNumber(driverDTO.getTelephoneNumber());
        driverInfoRequest.setEmail(driverDTO.getEmail());
        driverInfoRequest.setAddress(driverDTO.getAddress());
        driverInfoRequestRepository.save(driverInfoRequest);
        driverInfoRequestRepository.flush();
        return driverInfoRequest;
    }

    @Override
    public void deleteDriverDocument(Long id) {
        Optional<Document> response = documentRepository.findById(id);
        if (response.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Document does not exist.");
        }
        Document document = response.get();

        Optional<Driver> responseDriver = driverRepository.findById(document.getDriver().getId());
        if (responseDriver.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver does not exist.");
        }
        Driver driver = responseDriver.get();
        driver.getDocuments().remove(document);
        driverRepository.save(driver);
        driverRepository.flush();

        documentRepository.delete(document);
        documentRepository.flush();
    }

    @Override
    public boolean changeDriverActivityStatus(Long id, boolean activityStatus) {
        if (activityStatus) {
            if (!workingHoursService.canDriverCanActivate(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Driver has 8 hours of work");
            }
        }
        setDriverActivity(id, activityStatus);
        return activityStatus;
    }

    @Override
    public RideForTransferDTO getFirstAssignedRideToDriver(Principal user) {
        Optional<Driver> driverResult = driverRepository.findByEmail(user.getName());
        if (driverResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver with that email does not exist");
        }
        Driver driver = driverResult.get();
        Set<Ride> ridesCurrentlyAssignedToThisDriver = rideRepository.findByRideStateAndDriverId("INREVIEW", driver.getId(), Sort.by(Sort.Direction.DESC, "startTime"));
        if (ridesCurrentlyAssignedToThisDriver.size() == 0) {
            return null;
        }
        return new RideForTransferDTO(ridesCurrentlyAssignedToThisDriver.iterator().next());
    }

    @Override
    public AllHistoryItemsDTO getAllUserHistoryItemsByUserId(Long id, Pageable pageable, String sort) {
        if (sort.equals("")) {
            sort = "startTime-asc";
        }

        String[] sortTokens = sort.split("-");

        Sort sorter = Sort.by(Sort.Direction.ASC, sortTokens[0]);
        if (sortTokens[1].equals("desc")) {
            sorter = Sort.by(Sort.Direction.DESC, sortTokens[0]);
        }
        Pageable ridePage = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sorter);
        Page<Ride> userHistoryItems = rideRepository.findAllUserHistoryItems(id, ridePage);

        ArrayList<RideForUserDTO> historyItems = new ArrayList<>();
        for (Ride ride : userHistoryItems.getContent()) {
            historyItems.add(new RideForUserDTO(ride));
        }

        return new AllHistoryItemsDTO(historyItems, userHistoryItems.getTotalPages());

    }

    @Async
    @Scheduled(fixedRate = 60000, initialDelayString = "${timing.initialScheduledDelay}")
    public void calculateAllDriversHours() {
        List<Long> driversToBeDeactivated = workingHoursService.getDriverIdsToDeactivate();
        driverRepository.deactivateLongWorkingDrivers(driversToBeDeactivated);
    }

    private void notifyDriverActivityChange(boolean newActivityStatus, Driver driver) {
        if (newActivityStatus) {
            notifyDriverActive(driver);
        } else {
            notifyDriverInActive(driver.getVehicle().getId());
        }
    }

    private Response notifyDriverActive(Driver driver) {
        String serverURL = (String) helper.getConfigValue("PYTHON_SERVER_IP");
        String url = serverURL + "/add-vehicle";
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        Vehicle driversVehicle = driver.getVehicle();
        VehicleToDriveDTO vehicle = new VehicleToDriveDTO(driversVehicle.getId(), driver.getId(), new LocationDTO(driversVehicle.getLocation().getAddress(), driversVehicle.getLocation().getLatitude(), driversVehicle.getLocation().getLongitude()), driver.getStatus());
        String jsonBody;
        try {
            jsonBody = objectMapper.writeValueAsString(vehicle);
        } catch (JsonProcessingException e) {
            return null;
        }
        RequestBody body = RequestBody.create(jsonBody, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response;
        } catch (Exception ex) {
            return null;
        }
    }

    private Response notifyDriverInActive(Long vehicle_id) {
        String serverURL = (String) helper.getConfigValue("PYTHON_SERVER_IP");
        String url = serverURL + "/remove-vehicle/" + vehicle_id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response;
        } catch (Exception ex) {
            return null;
        }
    }
}

