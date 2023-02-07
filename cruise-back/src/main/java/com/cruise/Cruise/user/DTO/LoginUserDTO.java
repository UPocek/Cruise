package com.cruise.Cruise.user.DTO;

import javax.validation.constraints.NotNull;

public class LoginUserDTO {
    @NotNull(message = "is required!")
    private String accessToken;
    @NotNull(message = "is required!")
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
