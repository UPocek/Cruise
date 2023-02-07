package com.cruise.Cruise.passenger.DTO;

import javax.validation.constraints.NotNull;
import java.util.List;

public class EmailsDTO {
    @NotNull(message = "is required!")
    private List<String> emails;
    @NotNull(message = "is required!")
    private Long invitingPassenger;

    private Long rideId;

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getInvitingPassenger() {
        return invitingPassenger;
    }

    public void setInvitingPassenger(Long invitingPassenger) {
        this.invitingPassenger = invitingPassenger;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
