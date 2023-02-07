package com.cruise.Cruise.ride.DTO;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class ReasonDTO {

    @NotNull(message = "is required!")
    @Length(min = 5, max = 300, message = "must have from 5 to 300 characters")
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
