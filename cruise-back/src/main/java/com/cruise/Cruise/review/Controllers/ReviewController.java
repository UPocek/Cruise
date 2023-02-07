package com.cruise.Cruise.review.Controllers;

import com.cruise.Cruise.review.DTO.ReviewBasicDTO;
import com.cruise.Cruise.review.DTO.ReviewDTO;
import com.cruise.Cruise.review.DTO.ReviewPairDTO;
import com.cruise.Cruise.review.DTO.ReviewResponseDTO;
import com.cruise.Cruise.review.Services.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/review")
public class ReviewController {
    @Autowired
    IReviewService reviewService;

    @PostMapping(value = "/{rideId}/vehicle")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public ReviewDTO reviewVehicle(@PathVariable Long rideId, @Valid @RequestBody ReviewBasicDTO reviewDTO, Principal user) {
        return reviewService.reviewVehicle(rideId, user, reviewDTO);
    }

    @PostMapping(value = "/{rideId}/driver")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public ReviewDTO reviewDriver(@PathVariable Long rideId, @Valid @RequestBody ReviewBasicDTO reviewDTO, Principal user) {
        return reviewService.reviewDriver(rideId, user, reviewDTO);
    }

    @GetMapping(value = "/vehicle/{id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_ADMIN')")
    public ReviewResponseDTO getVehicleReviews(@PathVariable Long id) {
        return reviewService.getAllVehicleReviews(id);
    }

    @GetMapping(value = "/driver/{id}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_DRIVER') || hasAuthority('ROLE_ADMIN')")
    public ReviewResponseDTO getDriverReviews(@PathVariable Long id) {
        return reviewService.getAllDriverReviews(id);
    }

    @GetMapping(value = "/{rideId}")
    @Valid
    @PreAuthorize("hasAuthority('ROLE_PASSENGER') || hasAuthority('ROLE_DRIVER')")
    public List<ReviewPairDTO> specificRideReview(@PathVariable Long rideId) {
        return reviewService.specificRideReview(rideId);
    }
}
