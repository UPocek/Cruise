package com.cruise.Cruise.ride.DTO;

import com.cruise.Cruise.models.Rejection;

public class RejectionForRideDTO {
    private String reason;
    private String timeOfRejection;

    public RejectionForRideDTO() {
    }

    public RejectionForRideDTO(Rejection rejection) {
        this.reason = rejection.getReason();
        this.timeOfRejection = rejection.getTime().toString();
    }

    public RejectionForRideDTO(String reason, String timeOfRejection) {
        this.reason = reason;
        this.timeOfRejection = timeOfRejection;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTimeOfRejection() {
        return timeOfRejection;
    }

    public void setTimeOfRejection(String timeOfRejection) {
        this.timeOfRejection = timeOfRejection;
    }

    @Override
    public String toString() {
        return "RejectionForRideDTO{" + "reason='" + reason + '\'' + ", timeOfRejection='" + timeOfRejection + '\'' + '}';
    }
}
