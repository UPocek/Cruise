package com.cruisemobile.cruise.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NoteDTO {
    @SerializedName("message")
    @Expose
    private String message;

    public NoteDTO(String message) {
        this.message = message;
    }

    public NoteDTO() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
