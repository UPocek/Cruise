package com.cruisemobile.cruise.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationDTO {

    @SerializedName("address")
    @Expose
    @Nullable
    private String address;

    @SerializedName("latitude")
    @Expose
    @Nullable
    private double latitude;

    @SerializedName("longitude")
    @Expose
    @Nullable
    private double longitude;

    public LocationDTO() {
    }

    public LocationDTO(@Nullable String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
