package com.cruisemobile.cruise.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RejectionDTO {

    @SerializedName("timeOfRejection")
    @Expose
    @Nullable
    private String timeOfRejection;

    @SerializedName("reason")
    @Expose
    @Nullable
    private String reason;

    public RejectionDTO() {
    }

    public String getTimeOfRejection() {
        return timeOfRejection;
    }

    public void setTimeOfRejection(String timeOfRejection) {
        this.timeOfRejection = timeOfRejection;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
