package com.cruise.Cruise.user.DTO;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class NoteBasicDTO {
    @NotNull(message = "is required!")
    @Length(max = 1600, message = "cannot be longer than 1600 characters!")
    private String message;

    public NoteBasicDTO() {
    }

    public NoteBasicDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
