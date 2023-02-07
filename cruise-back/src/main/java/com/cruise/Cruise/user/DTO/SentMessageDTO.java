package com.cruise.Cruise.user.DTO;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SentMessageDTO {

    @NotBlank(message = "is required!")
    @Length(max = 1600, message = "cannot be longer than 1600 characters!")
    private String message;
    @NotNull(message = "is required!")
    private String type;
    @NotNull(message = "is required!")
    private Long rideId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }
}
