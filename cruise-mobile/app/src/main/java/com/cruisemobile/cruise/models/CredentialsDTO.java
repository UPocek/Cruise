package com.cruisemobile.cruise.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CredentialsDTO {
    @SerializedName("password")
    @Expose
    private String email;
    @SerializedName("email")
    @Expose
    private String password;

    public CredentialsDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
