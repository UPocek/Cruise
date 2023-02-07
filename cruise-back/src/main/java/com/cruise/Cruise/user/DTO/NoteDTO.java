package com.cruise.Cruise.user.DTO;

public class NoteDTO {

    private Long id;
    private String date;
    private String message;

    public NoteDTO() {
    }

    public NoteDTO(Long id, String date, String message) {
        this.id = id;
        this.date = date;
        this.message = message;
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
