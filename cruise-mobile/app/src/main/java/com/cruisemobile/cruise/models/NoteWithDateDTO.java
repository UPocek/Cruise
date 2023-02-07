package com.cruisemobile.cruise.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoteWithDateDTO {
    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("message")
    @Expose
    private String message;

    public NoteWithDateDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
