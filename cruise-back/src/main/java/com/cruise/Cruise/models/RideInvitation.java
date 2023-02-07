package com.cruise.Cruise.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ride_invitation")
public class RideInvitation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Passenger invitingPassenger;
    @OneToOne
    private Passenger passenger;
    @OneToOne
    private Ride ride;
    private LocalDateTime createTime;
    private long lifespanInSeconds;

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public long getLifespanInSeconds() {
        return lifespanInSeconds;
    }

    public void setLifespanInSeconds(long lifespanInSeconds) {
        this.lifespanInSeconds = lifespanInSeconds;
    }

    public Passenger getInvitingPassenger() {
        return invitingPassenger;
    }

    public void setInvitingPassenger(Passenger invitingPassenger) {
        this.invitingPassenger = invitingPassenger;
    }
}
