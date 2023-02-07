package com.cruise.Cruise.driver.DTO;

public class WorkingTimeDurationDTO {
    private Long duration;

    public WorkingTimeDurationDTO() {
    }

    public WorkingTimeDurationDTO(Long duration) {
        this.duration = duration;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
