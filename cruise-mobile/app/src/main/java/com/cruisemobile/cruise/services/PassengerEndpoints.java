package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.models.UserChangesDTO;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.models.UserRegistrationDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PassengerEndpoints {

    public static String urlExtension = "passenger";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST(urlExtension)
    Call<UserDTO> register(@Body UserRegistrationDTO userRegistration);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/email={email}")
    Call<UserDTO> getActivePassengerDetails(@Path("email") String email);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT(urlExtension + "/{id}")
    Call<UserDTO> updatePassenger(@Body UserChangesDTO user, @Path("id") Long id);
}
