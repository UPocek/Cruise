package com.cruise.Cruise.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activation")
public class UserActivation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.PERSIST)
    private User user;
    private LocalDateTime createTime;
    private long lifespanInSeconds;

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
}
