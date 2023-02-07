package com.cruisemobile.cruise.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationPairDTO {
    @SerializedName("departure")
    @Expose
    @Nullable
    private LocationDTO departure;

    @SerializedName("destination")
    @Expose
    @Nullable
    private LocationDTO destination;

    public LocationPairDTO() {
    }

    public LocationPairDTO(@Nullable LocationDTO departure, @Nullable LocationDTO destination) {
        this.departure = departure;
        this.destination = destination;
    }

    public LocationDTO getDeparture() {
        return departure;
    }

    public void setDeparture(LocationDTO departure) {
        this.departure = departure;
    }

    public LocationDTO getDestination() {
        return destination;
    }

    public void setDestination(LocationDTO destination) {
        this.destination = destination;
    }
}
