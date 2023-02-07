package com.cruise.Cruise.driver.DTO;

import com.cruise.Cruise.models.WorkingTime;

import java.time.LocalDateTime;

public class WorkingHoursDTO {
    private Long id;
    private String start;
    private String end;

    public WorkingHoursDTO() {
    }

    public WorkingHoursDTO(Long id, LocalDateTime start, LocalDateTime end) {
        this.id = id;
        this.start = String.valueOf(start);
        this.end = String.valueOf(end);
    }

    public WorkingHoursDTO(WorkingTime workingTime) {
        this.id = workingTime.getId();
        this.start = String.valueOf(workingTime.getStartTime());
        this.end = String.valueOf(workingTime.getEndTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = String.valueOf(start);
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = String.valueOf(end);
    }
}
