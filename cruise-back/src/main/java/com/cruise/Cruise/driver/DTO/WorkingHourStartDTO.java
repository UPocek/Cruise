package com.cruise.Cruise.driver.DTO;

import javax.validation.constraints.NotNull;

public class WorkingHourStartDTO {
    @NotNull(message = "is required!")
    private String start;

    public WorkingHourStartDTO() {
    }

    public WorkingHourStartDTO(String start) {
        this.start = start;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }
}
