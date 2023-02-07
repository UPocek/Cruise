package com.cruise.Cruise.unregisteredUser.DTO;

public class EstimationDataDTO {

    private DistanceEstimationDTO distance;
    private TimeEstimationDTO duration;
    private TimeEstimationDTO duration_in_traffic;
    private String status;

    public DistanceEstimationDTO getDistance() {
        return distance;
    }

    public void setDistance(DistanceEstimationDTO distance) {
        this.distance = distance;
    }

    public TimeEstimationDTO getDuration() {
        return duration;
    }

    public void setDuration(TimeEstimationDTO duration) {
        this.duration = duration;
    }

    public TimeEstimationDTO getDuration_in_traffic() {
        return duration_in_traffic;
    }

    public void setDuration_in_traffic(TimeEstimationDTO duration_in_traffic) {
        this.duration_in_traffic = duration_in_traffic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
