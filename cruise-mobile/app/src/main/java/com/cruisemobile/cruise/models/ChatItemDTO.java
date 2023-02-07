package com.cruisemobile.cruise.models;

public class ChatItemDTO {
    private String departureAddress;
    private String destinationAddress;
    private Long rideId;
    private String type;
    private String rideStatus;

    public ChatItemDTO() {
    }

    public ChatItemDTO(String departureAddress, String destinationAddress, Long rideId, String type, String rideStatus) {
        this.departureAddress = departureAddress;
        this.destinationAddress = destinationAddress;
        this.rideId = rideId;
        this.type = type;
        this.rideStatus = rideStatus;
    }

    public String getDepartureAddress() {
        return departureAddress;
    }

    public void setDepartureAddress(String departureAddress) {
        this.departureAddress = departureAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinatioinAddress) {
        this.destinationAddress = destinatioinAddress;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRideStatus() {
        return rideStatus;
    }

    public void setRideStatus(String rideStatus) {
        this.rideStatus = rideStatus;
    }
}
