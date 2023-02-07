package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.models.AdminDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface AdminEndpoints {
    public static String urlExtension = "admin";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/username={username}")
    Call<AdminDTO> getAdmin(@Path("username") String username);

}
