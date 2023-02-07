package com.cruise.Cruise.driver.DTO;

public class DriverBasicInfoDTO {

    private Long id;
    private String email;

    public DriverBasicInfoDTO() {
    }

    public DriverBasicInfoDTO(Long id, String email) {
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
