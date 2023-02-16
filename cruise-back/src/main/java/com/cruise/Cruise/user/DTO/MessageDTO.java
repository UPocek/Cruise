package com.cruise.Cruise.user.DTO;

import com.cruise.Cruise.models.Message;
import com.cruise.Cruise.models.Panic;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageDTO {

    private Long id;
    private String timeOfSending;
    private Long senderId;
    private Long receiverId;
    private String message;
    private String type;
    private Long rideId;

    public MessageDTO() {
    }

    public MessageDTO(Long id, LocalDateTime timeOfSending, Long senderId, Long receiverId, String message, String type, Long rideId) {
        this.id = id;
        this.timeOfSending = String.valueOf(timeOfSending);
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.type = type;
        this.rideId = rideId;
    }

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.timeOfSending = String.valueOf(message.getSentTime());
        try {
            this.senderId = message.getSender().getId();
        } catch (Exception e) {
            this.senderId = null;
        }
        try {
            this.receiverId = message.getReceiver().getId();
        } catch (Exception e) {
            this.receiverId = null;
        }
        this.message = message.getMessage();
        this.type = message.getType();
        this.rideId = message.getRide() != null ? message.getRide().getId() : -1;
    }

    public MessageDTO(Panic panic, Long requesterId) {
        this.id = panic.getId();
        this.timeOfSending = String.valueOf(panic.getTime());
        this.message = panic.getReason();
        this.rideId = panic.getCurrentRide().getId();
        this.receiverId = Objects.equals(panic.getUser().getId(), requesterId) ? requesterId : panic.getCurrentRide().getDriver().getId();
        this.senderId = Objects.equals(panic.getUser().getId(), requesterId) ? panic.getCurrentRide().getDriver().getId() : requesterId;
        this.type = "PANIC";
    }

    public MessageDTO(Long id, LocalDateTime timeOfSending, Long senderId, Long receiverId, String message, String type) {
        this.id = id;
        this.timeOfSending = String.valueOf(timeOfSending);
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTimeOfSending() {
        return timeOfSending;
    }

    public void setTimeOfSending(String timeOfSending) {
        this.timeOfSending = timeOfSending;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

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
