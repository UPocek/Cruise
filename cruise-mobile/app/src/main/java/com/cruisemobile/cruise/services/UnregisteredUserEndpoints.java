package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.models.OfferDTO;
import com.cruisemobile.cruise.models.RideInfoDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UnregisteredUserEndpoints {

    String urlExtension = "unregisteredUser";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(urlExtension)
    Call<OfferDTO> requestRideEstimation(@Body RideInfoDTO ride);
}
