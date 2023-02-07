package com.cruise.Cruise.user.DTO;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class ChangePasswordDTO {
    @NotNull(message = "is required!")
    @Length(min = 2, max = 20, message = "must have from 2 to 20 characters")
    private String new_password;
    @NotNull(message = "is required!")
    @Length(min = 2, max = 20, message = "must have from 2 to 20 characters")
    private String old_password;

    public ChangePasswordDTO() {
    }

    public ChangePasswordDTO(String new_password, String old_password) {
        this.new_password = new_password;
        this.old_password = old_password;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getOld_password() {
        return old_password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }
}
