package com.cruise.Cruise.panic.DTO;

import com.cruise.Cruise.ride.DTO.RideForTransferDTO;

public class PanicDTO {
    private Long id;
    private UserForPanicDTO user;
    private RideForTransferDTO ride;
    private String time;
    private String reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserForPanicDTO getUser() {
        return user;
    }

    public void setUser(UserForPanicDTO user) {
        this.user = user;
    }

    public RideForTransferDTO getRide() {
        return ride;
    }

    public void setRide(RideForTransferDTO ride) {
        this.ride = ride;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
