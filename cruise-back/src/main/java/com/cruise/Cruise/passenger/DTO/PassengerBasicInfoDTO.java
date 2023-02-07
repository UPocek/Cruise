package com.cruise.Cruise.passenger.DTO;

public class PassengerBasicInfoDTO {

    private Long id;
    private String email;

    public PassengerBasicInfoDTO() {
    }

    public PassengerBasicInfoDTO(Long id, String email) {
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
