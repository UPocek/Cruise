package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.models.ReportDTO;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportsEndpoints {
    String urlExtension = "ride";

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET(urlExtension + "/report/{id}")
    Call<ReportDTO> getUserReportByType(@Path("id") Long id, @Query("from") String from, @Query("to") String to, @Query("role") String role, @Query("type") String type);

}
