package com.cruise.Cruise.review.Services;

import com.cruise.Cruise.review.DTO.ReviewBasicDTO;
import com.cruise.Cruise.review.DTO.ReviewDTO;
import com.cruise.Cruise.review.DTO.ReviewPairDTO;
import com.cruise.Cruise.review.DTO.ReviewResponseDTO;

import java.security.Principal;
import java.util.List;

public interface IReviewService {
    ReviewDTO reviewVehicle(Long rideId, Principal user, ReviewBasicDTO review);

    ReviewDTO reviewDriver(Long rideId, Principal user, ReviewBasicDTO review);

    ReviewResponseDTO getAllVehicleReviews(Long vehicleId);

    ReviewResponseDTO getAllDriverReviews(Long driverId);

    List<ReviewPairDTO> specificRideReview(Long rideId);

}
