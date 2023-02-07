package com.cruise.Cruise.ride.DTO;

public class AnswerEmailDTO {
    private String answer;
    private String senderPassengerEmail;
    private String receiverPassengerEmail;
    private long rideId;

    public long getRideId() {
        return rideId;
    }

    public void setRideId(long rideId) {
        this.rideId = rideId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSenderPassengerEmail() {
        return senderPassengerEmail;
    }

    public void setSenderPassengerEmail(String senderPassengerEmail) {
        this.senderPassengerEmail = senderPassengerEmail;
    }

    public String getReceiverPassengerEmail() {
        return receiverPassengerEmail;
    }

    public void setReceiverPassengerEmail(String receiverPassengerEmail) {
        this.receiverPassengerEmail = receiverPassengerEmail;
    }
}
