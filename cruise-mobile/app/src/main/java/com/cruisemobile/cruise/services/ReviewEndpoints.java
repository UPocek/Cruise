package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.models.ReviewBasicDTO;
import com.cruisemobile.cruise.models.ReviewDTO;
import com.cruisemobile.cruise.models.ReviewPairDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewEndpoints {

    String urlExtension = "review";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{rideId}")
    Call<List<ReviewPairDTO>> getAllRideReviews(@Path("rideId") Long rideId);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(urlExtension + "/{rideId}/vehicle")
    Call<ReviewDTO> submitDriverReview(@Path("rideId") Long rideId, @Body ReviewBasicDTO review);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(urlExtension + "/{rideId}/driver")
    Call<ReviewDTO> submitVehicleReview(@Path("rideId") Long rideId, @Body ReviewBasicDTO review);

}
