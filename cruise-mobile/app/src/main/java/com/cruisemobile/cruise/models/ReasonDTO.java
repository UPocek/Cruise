package com.cruisemobile.cruise.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReasonDTO {
    @SerializedName("reason")
    @Expose
    private String reason;

    public ReasonDTO() {
    }

    public ReasonDTO(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
