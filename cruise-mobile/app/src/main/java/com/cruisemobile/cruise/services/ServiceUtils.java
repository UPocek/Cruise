package com.cruisemobile.cruise.services;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceUtils {

    private static final String BASE_URL = "http://192.168.1.7:8080/api/";
    public static final String SERVER_IP = "http://192.168.1.7";

    static TokenInterceptor interceptor = new TokenInterceptor();

    static OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(interceptor).build();

    public static Retrofit retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static AdminEndpoints adminEndpoints = retrofit.create(AdminEndpoints.class);
    public static PassengerEndpoints passengerEndpoints = retrofit.create(PassengerEndpoints.class);
    public static UserEndpoints userEndpoints = retrofit.create(UserEndpoints.class);
    public static DriverEndpoints driverEndpoints = retrofit.create(DriverEndpoints.class);
    public static RideRequestEndpoints rideRequestEndpoints = retrofit.create(RideRequestEndpoints.class);
    public static RideEndpoints rideEndpoints = retrofit.create(RideEndpoints.class);
    public static UnregisteredUserEndpoints unregisteredUserEndpoints = retrofit.create(UnregisteredUserEndpoints.class);
    public static ReviewEndpoints reviewEndpoints = retrofit.create(ReviewEndpoints.class);
    static OkHttpClient clientReports = new OkHttpClient.Builder()
            .addInterceptor(interceptor).readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS).build();
    public static Retrofit retrofitReports = new Retrofit.Builder()
            .client(clientReports)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static ReportsEndpoints reportsEndpoints = retrofitReports.create(ReportsEndpoints.class);
}
