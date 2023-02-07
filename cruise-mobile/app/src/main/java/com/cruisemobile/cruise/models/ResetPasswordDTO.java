package com.cruisemobile.cruise.models;
public class ResetPasswordDTO {
    private String new_password;
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
