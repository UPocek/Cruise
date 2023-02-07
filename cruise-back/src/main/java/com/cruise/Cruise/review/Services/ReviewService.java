package com.cruise.Cruise.review.Services;

import com.cruise.Cruise.driver.Repositories.IDriverRepository;
import com.cruise.Cruise.models.*;
import com.cruise.Cruise.passenger.Repositories.IPassengerRepository;
import com.cruise.Cruise.review.DTO.ReviewBasicDTO;
import com.cruise.Cruise.review.DTO.ReviewDTO;
import com.cruise.Cruise.review.DTO.ReviewPairDTO;
import com.cruise.Cruise.review.DTO.ReviewResponseDTO;
import com.cruise.Cruise.review.Repositories.IReviewRepository;
import com.cruise.Cruise.ride.Repositories.IRideRepository;
import com.cruise.Cruise.vehicle.Repositories.IVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReviewService implements IReviewService {

    @Autowired
    private IReviewRepository reviewRepository;
    @Autowired
    private IRideRepository rideRepository;
    @Autowired
    private IPassengerRepository passengerRepository;
    @Autowired
    private IDriverRepository driverRepository;
    @Autowired
    private IVehicleRepository vehicleRepository;

    @Override
    public ReviewDTO reviewVehicle(Long rideId, Principal user, ReviewBasicDTO reviewDTO) {
        int howMuchDaysInPastCanPassengerRateVehicle = 3;
        Optional<Ride> rideResult = rideRepository.findById(rideId);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
        Ride ride = rideResult.get();
        if (isTooLateToLeaveReview(ride, howMuchDaysInPastCanPassengerRateVehicle)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride could not be reviewed. More then " + howMuchDaysInPastCanPassengerRateVehicle + " days have passed");
        }
        Optional<Passenger> passengerResult = passengerRepository.findByEmail(user.getName());
        if (passengerResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger with that id does not exist");
        }
        Passenger passenger = passengerResult.get();
        if (!ride.getPassengers().contains(passenger)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger not in that ride");
        }
        Review review = new Review();
        review.setReviewFor("VEHICLE");
        review.setRide(ride);
        review.setMark(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setPassenger(passenger);
        reviewRepository.save(review);
        reviewRepository.flush();
        Vehicle vehicleFromRide = ride.getDriver().getVehicle();
        vehicleFromRide.addReview(review);
        vehicleRepository.save(vehicleFromRide);
        vehicleRepository.flush();
        return new ReviewDTO(review);
    }


    @Override
    public ReviewDTO reviewDriver(Long rideId, Principal user, ReviewBasicDTO reviewDTO) {
        int howMuchDaysInPastCanPassengerRateDriver = 3;
        Optional<Ride> rideResult = rideRepository.findById(rideId);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
        Ride ride = rideResult.get();
        if (isTooLateToLeaveReview(ride, howMuchDaysInPastCanPassengerRateDriver)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride could not be reviewed. More then " + howMuchDaysInPastCanPassengerRateDriver + " days have passed");
        }
        Optional<Passenger> passengerResult = passengerRepository.findByEmail(user.getName());
        if (passengerResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger with that id does not exist");
        }
        Passenger passenger = passengerResult.get();
        if (!ride.getPassengers().contains(passenger)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger not in that ride");
        }
        Review review = new Review();
        review.setReviewFor("DRIVER");
        review.setRide(ride);
        review.setMark(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setPassenger(passenger);
        reviewRepository.save(review);
        reviewRepository.flush();
        Driver driverFromRide = ride.getDriver();
        driverFromRide.addReview(review);
        driverRepository.save(driverFromRide);
        driverRepository.flush();
        return new ReviewDTO(review);
    }

    @Override
    public ReviewResponseDTO getAllVehicleReviews(Long vehicleId) {
        ReviewResponseDTO reviewResponse = new ReviewResponseDTO();
        List<ReviewDTO> vehicleReviews = new ArrayList<>();
        Set<Review> reviews = vehicleRepository.findAllVehicleReviews(vehicleId);
        for (Review review : reviews) {
            vehicleReviews.add(new ReviewDTO(review));
        }
        reviewResponse.setTotalCount(vehicleReviews.size());
        reviewResponse.setResults(vehicleReviews);
        return reviewResponse;
    }

    @Override
    public ReviewResponseDTO getAllDriverReviews(Long driverId) {
        ReviewResponseDTO reviewResponse = new ReviewResponseDTO();
        List<ReviewDTO> driverReviews = new ArrayList<>();
        Set<Review> reviews = driverRepository.findAllDriverReviews(driverId);
        for (Review review : reviews) {
            driverReviews.add(new ReviewDTO(review));
        }
        reviewResponse.setTotalCount(driverReviews.size());
        reviewResponse.setResults(driverReviews);
        return reviewResponse;
    }

    @Override
    public List<ReviewPairDTO> specificRideReview(Long rideId) {
        Optional<Ride> rideResult = rideRepository.findById(rideId);
        if (rideResult.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride does not exist!");
        }
        Ride ride = rideResult.get();
        Map<Long, ReviewPairDTO> temp = new HashMap<>();
        for (Review review : ride.getReviews()) {
            Long reviewerId = review.getPassenger().getId();
            if (!temp.containsKey(reviewerId)) {
                temp.put(reviewerId, new ReviewPairDTO());
            }
            if (review.getReviewFor().equals("DRIVER")) {
                temp.get(reviewerId).setDriverReview(new ReviewDTO(review));
            } else {
                temp.get(reviewerId).setVehicleReview(new ReviewDTO(review));
            }
        }
        return new ArrayList<>(temp.values());
    }

    private boolean isTooLateToLeaveReview(Ride ride, int howMuchDaysInPastCanPassengerRate) {
        return Duration.between(ride.getEndTime(), LocalDateTime.now()).compareTo(Duration.ofDays(howMuchDaysInPastCanPassengerRate)) > 0;
    }
}
