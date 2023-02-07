package com.cruise.Cruise.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reset_password_request")
public class ResetPasswordRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    private User user;
    private LocalDateTime createTime;
    private long lifespanInSeconds;
    private String hash;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(User user, LocalDateTime createTime, long lifespanInSeconds, String hash) {
        this.user = user;
        this.createTime = createTime;
        this.lifespanInSeconds = lifespanInSeconds;
        this.hash = hash;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
