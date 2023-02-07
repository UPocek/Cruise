package com.cruise.Cruise.ride.DTO;

public class RideEstimationDTO {

    private int estimatedTimeInMinutes;
    private int estimatedCost;
    private int distance;

    public RideEstimationDTO() {
    }

    public RideEstimationDTO(int estimatedTimeInMinutes, int estimatedCost, int distance) {
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.estimatedCost = estimatedCost;
        this.distance = distance;
    }

    public int getEstimatedTimeInMinutes() {
        return estimatedTimeInMinutes;
    }

    public void setEstimatedTimeInMinutes(int estimatedTimeInMinutes) {
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
    }

    public int getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(int estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
