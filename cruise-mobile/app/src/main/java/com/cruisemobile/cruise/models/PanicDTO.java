package com.cruisemobile.cruise.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PanicDTO {
    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("user")
    @Expose
    private UserForPanicDTO user;

    @SerializedName("ride")
    @Expose
    private RideDTO ride;

    @SerializedName("time")
    @Expose
    private String time;

    @SerializedName("reason")
    @Expose
    private String reason;

    public PanicDTO() {
    }

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

    public RideDTO getRide() {
        return ride;
    }

    public void setRide(RideDTO ride) {
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
