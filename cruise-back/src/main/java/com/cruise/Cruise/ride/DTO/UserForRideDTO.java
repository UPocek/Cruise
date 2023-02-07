package com.cruise.Cruise.ride.DTO;


import com.cruise.Cruise.models.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class UserForRideDTO {
    @NotNull(message = "is required!")
    private Long id;
    @NotNull(message = "is required!")
    @Email(regexp = "^[\\w-\\.]+[+]?[\\w-\\.]*@([\\w-]+\\.)+[\\w-]{2,4}$", message = "format is not valid!")
    private String email;

    public UserForRideDTO() {
    }

    public UserForRideDTO(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public UserForRideDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserForRideDTO{" + "id=" + id + ", email='" + email + '\'' + '}';
    }
}
