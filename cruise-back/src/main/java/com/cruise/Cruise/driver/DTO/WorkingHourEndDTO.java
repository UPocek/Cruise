package com.cruise.Cruise.driver.DTO;

import javax.validation.constraints.NotNull;

public class WorkingHourEndDTO {
    @NotNull(message = "is required!")
    private String end;

    public WorkingHourEndDTO() {
    }

    public WorkingHourEndDTO(String end) {
        this.end = end;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
