package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.models.AllHistoryItemsDTO;
import com.cruisemobile.cruise.models.UserChangesDTO;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.models.VehicleDTO;
import com.cruisemobile.cruise.models.VehicleToDriveDTO;
import com.cruisemobile.cruise.models.WorkingTimeDurationDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DriverEndpoints {
    public static String urlExtension = "driver";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{id}")
    Call<UserDTO> getDriverDetails(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/email={email}")
    Call<UserDTO> getActiveDriverDetails(@Path("email") String email);

    @PUT(urlExtension + "/{id}/activate")
    Call<Boolean> changeDriverActivity(@Path("id") Long id, @Query("activityStatus") boolean activityStatus);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{id}/vehicle")
    Call<VehicleDTO> getDriverVehicle(@Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @PUT(urlExtension + "/requestChanges/{id}")
    Call<UserDTO> requestDriverUpdate(@Body UserChangesDTO user, @Path("id") Long id);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/all-active-vehicles")
    Call<List<VehicleToDriveDTO>> getAllVehicles();

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{id}/history-items")
    Call<AllHistoryItemsDTO> getAllUserHistoryItems(@Path("id") Long id, @Query("size") int pageSize,
                                                    @Query("page") int currentPage, @Query("sort") String sort);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/{id}/workingTime")
    Call<WorkingTimeDurationDTO> getDriversWorkingTime(@Path("id") Long id);

}
