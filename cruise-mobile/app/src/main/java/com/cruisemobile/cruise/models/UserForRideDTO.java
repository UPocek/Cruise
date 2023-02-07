package com.cruisemobile.cruise.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserForRideDTO {
    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("email")
    @Expose
    private String email;

    public UserForRideDTO() {
    }

    public UserForRideDTO(Long id, String email) {
        this.id = id;
        this.email = email;
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
}
