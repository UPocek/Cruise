package com.cruise.Cruise.user.DTO;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class ResetPasswordDTO {
    @NotNull(message = "is required!")
    @Length(min = 2, max = 20, message = "must have from 2 to 20 characters")
    private String new_password;
    @NotNull(message = "is required!")
    private String code;

    public ResetPasswordDTO() {
    }

    public ResetPasswordDTO(String new_password, String code) {
        this.new_password = new_password;
        this.code = code;
    }

    public String getNew_password() {
        return new_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
