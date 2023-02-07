package com.cruise.Cruise.passenger.Services;

import com.cruise.Cruise.helper.IHelperService;
import com.cruise.Cruise.models.Passenger;
import com.cruise.Cruise.models.Picture;
import com.cruise.Cruise.models.User;
import com.cruise.Cruise.models.UserActivation;
import com.cruise.Cruise.passenger.DTO.PassengerDTO;
import com.cruise.Cruise.passenger.DTO.RegisteredPassengerDTO;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.passenger.Repositories.IUserActivationRepository;
import com.cruise.Cruise.ride.DTO.RideForUserDTO;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.cruise.Cruise.user.Repositories.IUserRepository;
import com.sendgrid.helpers.mail.Mail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PassengerService implements IPassengerService {

    @Autowired
    private IHelperService helper;
    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private IUserActivationRepository userActivationRepository;
    @Autowired
    private IUserRepository userRepository;

    @Override
    public RegisteredPassengerDTO registerPassenger(PassengerDTO passengerDTO) {
        if (userRepository.findByEmail(passengerDTO.getEmail()).isEmpty()) {
            Passenger passenger = new Passenger();
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            passenger.setName(passengerDTO.getName());
            passenger.setSurname(passengerDTO.getSurname());
            Picture profilePicture = new Picture();
            profilePicture.setPictureContent(passengerDTO.getProfilePicture() != null ? passengerDTO.getProfilePicture() : "");
            passenger.setProfilePicture(profilePicture);
            passenger.setTelephoneNumber(passengerDTO.getTelephoneNumber());
            passenger.setEmail(passengerDTO.getEmail());
            passenger.setAddress(passengerDTO.getAddress());
            passenger.setPassword(encoder.encode(passengerDTO.getPassword()));
            passenger.setActive(false);
            passenger.setBlocked(false);
            passengerRepository.save(passenger);
            passengerRepository.flush();
            return new RegisteredPassengerDTO(passenger.getId(), passenger.getName(), passenger.getSurname(), passenger.getProfilePicture().getPictureContent(), passenger.getTelephoneNumber(), passenger.getEmail(), passenger.getAddress(), passenger.isBlocked());
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with that email already exists!");
    }

    @Override
    public void verifyUserRegistration(Long activationId) {
        Optional<UserActivation> activationResult = userActivationRepository.findById(activationId);
        if (activationResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activation with entered id does not exist!");
        }
        UserActivation userActivation = activationResult.get();
        User passenger = userActivation.getUser();
        if (passenger.isActive()) {
            throw new ResponseStatusException(HttpStatus.OK, "User already activated");
        }
        boolean tokenIsValid = userActivation.getCreateTime().plusSeconds(userActivation.getLifespanInSeconds()).isAfter(LocalDateTime.now());
        if (!tokenIsValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activation expired. Register again!");
        }

        passenger.setActive(true);
        userActivationRepository.save(userActivation);
    }

    @Override
    public RegisteredPassengerDTO update(Long id, PassengerDTO passengerDTO) {
        Passenger passenger = getById(id);
        if (!passengerDTO.getEmail().equals(passenger.getEmail()) && userRepository.findByEmail(passengerDTO.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }
        passenger.setName(passengerDTO.getName());
        passenger.setSurname(passengerDTO.getSurname());
        Picture profilePicture = passenger.getProfilePicture();
        profilePicture.setPictureContent(passengerDTO.getProfilePicture());
        passenger.setProfilePicture(profilePicture);
        passenger.setTelephoneNumber(passengerDTO.getTelephoneNumber());
        passenger.setEmail(passengerDTO.getEmail());
        passenger.setAddress(passengerDTO.getAddress());
        passengerRepository.save(passenger);
        passengerRepository.flush();
        return new RegisteredPassengerDTO(passenger.getId(), passenger.getName(), passenger.getSurname(), passenger.getProfilePicture().getPictureContent(), passenger.getTelephoneNumber(), passenger.getEmail(), passenger.getAddress(), passenger.isBlocked());
    }

    @Override
    public RegisteredPassengerDTO getRegisteredPassengerDTOById(Long id) {
        Passenger passenger = getById(id);
        return new RegisteredPassengerDTO(passenger.getId(), passenger.getName(), passenger.getSurname(), passenger.getProfilePicture().getPictureContent(), passenger.getTelephoneNumber(), passenger.getEmail(), passenger.getAddress(), passenger.isBlocked());
    }

    @Override
    public RegisteredPassengerDTO getRegisteredPassengerDTOByEmail(String email) {
        Passenger passenger = getByEmail(email);
        return new RegisteredPassengerDTO(passenger.getId(), passenger.getName(), passenger.getSurname(), passenger.getProfilePicture().getPictureContent(), passenger.getTelephoneNumber(), passenger.getEmail(), passenger.getAddress(), passenger.isBlocked());
    }

    @Override
    public Map<String, Object> getAllPassengers(int page, int size) {
        List<Passenger> allPassengers = passengerRepository.findAll();
        List<RegisteredPassengerDTO> results = new ArrayList<>();
        if (page == -1 || size == -1)
            for (Passenger passenger : allPassengers) {
                results.add(new RegisteredPassengerDTO(passenger.getId(), passenger.getName(), passenger.getSurname(), passenger.getProfilePicture().getPictureContent(), passenger.getTelephoneNumber(), passenger.getEmail(), passenger.getAddress(), passenger.isBlocked()));
            }
        else
            for (int i = (page - 1) * size; i < page * size; i++)
                results.add(new RegisteredPassengerDTO(allPassengers.get(i).getId(), allPassengers.get(i).getName(), allPassengers.get(i).getSurname(), allPassengers.get(i).getProfilePicture().getPictureContent(), allPassengers.get(i).getTelephoneNumber(), allPassengers.get(i).getEmail(), allPassengers.get(i).getAddress(), allPassengers.get(i).isBlocked()));

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", allPassengers.size());
        response.put("results", results);
        return response;
    }

    @Override
    public Map<String, Object> getAllPassengerRides(Long passengerId, String from, String to, String sortCriteria) {
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
        if (sortCriteria.equals("")) {
            sortCriteria = "startTime-asc";
        }

        String[] sortTokens = sortCriteria.split("-");
        Sort sort = Sort.by(Sort.Direction.ASC, sortTokens[0]);
        if (sortTokens[1].equals("desc")) {
            sort = Sort.by(Sort.Direction.DESC, sortTokens[0]);
        }

        List<RideForUserDTO> passengerRides = rideRepository.findAllPassengersRides(passengerId, fromDate, toDate, sort);

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", passengerRides.size());
        response.put("results", passengerRides);
        return response;
    }

    @Override
    public RegisteredPassengerDTO changeBlockedStatus(String email, boolean block) {
        Passenger passenger = getByEmail(email);
        passenger.setBlocked(block);
        passengerRepository.save(passenger);
        passengerRepository.flush();
        return new RegisteredPassengerDTO(passenger.getId(), passenger.getName(), passenger.getSurname(), passenger.getProfilePicture().getPictureContent(), passenger.getTelephoneNumber(), passenger.getEmail(), passenger.getAddress(), passenger.isBlocked());
    }

    @Override
    public void sendConfirmRegistrationEmail(RegisteredPassengerDTO newPassenger) {
        UserActivation activationToken = prepareActivationToken(newPassenger);
        String confirmationLink = Objects.requireNonNull(helper.getConfigValue("ACCOUNT_CONFIRMATION_URL")).toString() + activationToken.getId();
        String mailTemplate = helper.prepareMailTemplate(confirmationLink, "src/main/resources/static/emailTemplate.html", "Confirm email address:\n ");
        Mail mail = helper.prepareMail(Objects.requireNonNull(helper.getConfigValue("EMAIL")).toString(), newPassenger.getEmail(), Objects.requireNonNull(helper.getConfigValue("EMAIL_SUBJECT")).toString(), mailTemplate);
        String apiKey = Objects.requireNonNull(helper.getConfigValue("SENDGRID_API_KEY")).toString();
        helper.sendEmail(apiKey, mail);
    }

    private UserActivation prepareActivationToken(RegisteredPassengerDTO passengerDTO) {
        Passenger passenger = getById(passengerDTO.getId());
        UserActivation token = new UserActivation();
        token.setUser(passenger);
        token.setCreateTime(LocalDateTime.now());
        token.setLifespanInSeconds(Long.parseLong(Objects.requireNonNull(helper.getConfigValue("TOKEN_LIFESPAN")).toString()));
        userActivationRepository.save(token);
        userActivationRepository.flush();
        return token;
    }

    private Passenger getById(Long id) {
        Optional<Passenger> found = passengerRepository.findById(id);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger with that id does not exist");
        }
        return found.get();
    }

    private Passenger getByEmail(String email) {
        Optional<Passenger> found = passengerRepository.findByEmail(email);
        if (found.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger with that email does not exist");
        }
        return found.get();
    }
}
