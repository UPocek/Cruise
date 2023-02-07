package com.cruise.Cruise.review.DTO;

public class SpecificRideReviewDTO {
    private ReviewDTO vehicleReview;
    private ReviewDTO driverReview;

    public SpecificRideReviewDTO() {
    }

    public SpecificRideReviewDTO(ReviewDTO vehicleReview, ReviewDTO driverReview) {
        this.vehicleReview = vehicleReview;
        this.driverReview = driverReview;
    }

    public ReviewDTO getVehicleReview() {
        return vehicleReview;
    }

    public void setVehicleReview(ReviewDTO vehicleReview) {
        this.vehicleReview = vehicleReview;
    }

    public ReviewDTO getDriverReview() {
        return driverReview;
    }

    public void setDriverReview(ReviewDTO driverReview) {
        this.driverReview = driverReview;
    }
}
