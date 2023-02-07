package com.cruisemobile.cruise.services;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceUtils {


//    public static final String SERVER_IP = "192.168.0.144";
    //    Tamara
//    private static final String BASE_URL = "http://192.168.0.144:8080/api/";
//    Bojan
//    private static final String BASE_URL = "http://192.168.8.101:8080/api/";
//    public static final String SERVER_IP = "192.168.8.101";
//    private static final String BASE_URL = "http://192.168.1.5:8080/api/";
//    public static final String SERVER_IP = "192.168.1.5";
//    private static final String BASE_URL = "http://192.168.1.5:8080/api/";
//    private static final String BASE_URL = "http://192.168.0.141:8080/api/";
//    private static final String BASE_URL = "http://192.168.1.13:8080/api/";
    //    Uros
    private static final String BASE_URL = "http://192.168.0.141:8080/api/";
//    Uros MAC
//    private static final String BASE_URL = "http://192.168.5.220:8080/api/";
    public static final String SERVER_IP = "192.168.0.141";
//    public static final String SERVER_IP = "192.168.8.101";
//    public static final String SERVER_IP = "192.168.1.13";
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
