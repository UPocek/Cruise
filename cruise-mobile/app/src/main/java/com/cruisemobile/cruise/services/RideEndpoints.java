package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.models.FavouriteRideBasicDTO;
import com.cruisemobile.cruise.models.FavouriteRideDTO;
import com.cruisemobile.cruise.models.PanicDTO;
import com.cruisemobile.cruise.models.ReasonDTO;
import com.cruisemobile.cruise.models.RideDTO;
import com.cruisemobile.cruise.models.RideForFutureDTO;
import com.cruisemobile.cruise.models.RideForTransferDTO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RideEndpoints {
    public static String urlExtension = "ride";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{id}")
    Call<RideForTransferDTO> getRideDetails(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(urlExtension)
    Call<RideForTransferDTO> rideForFutureRequest(@Body RideForFutureDTO ride);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/passenger/{id}/active")
    Call<RideDTO> getActiveRideForPassenger(@Path("id") Long id);


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/passenger/{id}/accepted")
    Call<RideDTO> getAcceptedRideForPassenger(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/driver/{id}/active")
    Call<RideForTransferDTO> getActiveRideForDriver(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/driver/{id}/accepted")
    Call<RideForTransferDTO> getAcceptedRideForDriver(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
    })
    @PUT(urlExtension + "/{id}/panic")
    Call<PanicDTO> panic(@Path("id") Long id, @Body ReasonDTO reasonDTO);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
    })
    @PUT(urlExtension + "/{id}/cancel")
    Call<RideForTransferDTO> cancel(@Path("id") Long id, @Body ReasonDTO reasonDTO);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
    })
    @PUT(urlExtension + "/{id}/start")
    Call<RideForTransferDTO> startRide(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json",
    })
    @PUT(urlExtension + "/{id}/end")
    Call<RideForTransferDTO> endRide(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/favourites")
    Call<ArrayList<FavouriteRideDTO>> getAllPassengerFavouriteRides();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(urlExtension + "/favourites")
    Call<FavouriteRideDTO> addFavouriteRide(@Body FavouriteRideBasicDTO favouriteRideBasic);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @DELETE(urlExtension + "/favourites/{id}")
    Call<String> deleteFavouriteRide(@Path("id") Long id);
}
