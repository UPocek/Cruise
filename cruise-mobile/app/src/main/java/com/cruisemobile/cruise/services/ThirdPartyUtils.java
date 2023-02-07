package com.cruisemobile.cruise.services;

import com.cruisemobile.cruise.BuildConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ThirdPartyUtils {

    public static final String mapsApiKey = BuildConfig.MAPS_API_KEY;

    static OkHttpClient client = new OkHttpClient.Builder().build();

    public static Retrofit retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl("https://maps.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static GoogleEndpoints googleEndpoints = retrofit.create(GoogleEndpoints.class);
}
