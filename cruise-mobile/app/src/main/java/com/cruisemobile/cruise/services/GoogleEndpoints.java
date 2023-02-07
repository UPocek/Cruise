package com.cruisemobile.cruise.services;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface GoogleEndpoints {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
    })
    @GET("maps/api/geocode/json")
    Call<Map<String, Object>> getLatLngFromAddress(@Query("address") String address, @Query("key") String key);

}
