package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.models.RideForTransferDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RideRequestEndpoints {

    public static String urlExtension = "ride-request";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(urlExtension)
    Call<Void> rideTransfer(@Body RideForTransferDTO ride);

}
