package com.cruise.Cruise.passenger.DTO;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PassengerDTO {
    @NotNull(message = "is required!")
    @Length(min = 2, max = 20, message = "must have from 2 to 20 characters")
    private String name;
    @NotNull(message = "is required!")
    @Length(min = 2, max = 20, message = "must have from 2 to 20 characters")
    private String surname;
    private String profilePicture;
    @NotNull(message = "is required!")
    @Pattern(regexp = "[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}", message = "format is not valid")
    private String telephoneNumber;
    @NotNull(message = "is required!")
    @Email(regexp = "^[\\w-\\.]+[+]?[\\w-\\.]*@([\\w-]+\\.)+[\\w-]{2,4}$", message = "format is not valid!")
    private String email;
    @NotNull(message = "is required!")
    @Length(min = 1, max = 100, message = "must have from 1 to 100 characters")
    private String address;
    @Length(min = 2, max = 20, message = "must have from 2 to 20 characters")
    private String password;

    public PassengerDTO() {
    }

    public PassengerDTO(String name, String surname, String profilePicture, String telephoneNumber, String email, String address, String password) {
        this.name = name;
        this.surname = surname;
        this.profilePicture = profilePicture;
        this.telephoneNumber = telephoneNumber;
        this.email = email;
        this.address = address;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
