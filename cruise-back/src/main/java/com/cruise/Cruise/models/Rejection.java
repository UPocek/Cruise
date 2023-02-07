package com.cruise.Cruise.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rejection")
public class Rejection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Ride ride;
    private String reason;
    @ManyToOne
    private User user;
    private LocalDateTime time;

    public Rejection() {
    }

    public Rejection(Long id, Ride ride, String reason, User user, LocalDateTime time) {
        this.id = id;
        this.ride = ride;
        this.reason = reason;
        this.user = user;
        this.time = time;
    }

    public Rejection(Ride ride, String reason, User user, LocalDateTime time) {
        this.ride = ride;
        this.reason = reason;
        this.user = user;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
