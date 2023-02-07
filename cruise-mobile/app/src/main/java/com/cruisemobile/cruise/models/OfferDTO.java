package com.cruisemobile.cruise.models;

public class OfferDTO {
    int distance;
    int estimatedTimeInMinutes;
    int estimatedCost;

    public OfferDTO() {
    }

    public OfferDTO(int distance, int estimatedTimeInMinutes, int estimatedCost) {
        this.distance = distance;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.estimatedCost = estimatedCost;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
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
}
