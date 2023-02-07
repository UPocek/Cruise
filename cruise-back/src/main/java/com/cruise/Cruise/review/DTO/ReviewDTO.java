package com.cruise.Cruise.review.DTO;

import com.cruise.Cruise.models.Review;
import com.cruise.Cruise.ride.DTO.UserForRideDTO;

public class ReviewDTO {
    private Long id;
    private double rating;
    private String comment;
    private UserForRideDTO passenger;
    private String reviewFor;

    public ReviewDTO() {
    }

    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.rating = review.getMark();
        this.comment = review.getComment();
        this.passenger = new UserForRideDTO(review.getPassenger());
        this.reviewFor = review.getReviewFor();
    }

    public ReviewDTO(Long id, double rating, String comment, UserForRideDTO passenger, String reviewFor) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.passenger = passenger;
        this.reviewFor = reviewFor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserForRideDTO getPassenger() {
        return passenger;
    }

    public void setPassenger(UserForRideDTO passenger) {
        this.passenger = passenger;
    }

    public String getReviewFor() {
        return reviewFor;
    }

    public void setReviewFor(String reviewFor) {
        this.reviewFor = reviewFor;
    }
}
