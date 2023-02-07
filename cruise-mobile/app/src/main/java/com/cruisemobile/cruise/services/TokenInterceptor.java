package com.cruisemobile.cruise.services;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.MyApp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        Request newRequest;
        if (chain.request().header("skip") != null && chain.request().header("skip").equals("true")) {
            newRequest = chain.request();
        } else {
            newRequest = chain.request().newBuilder()
                    .header("Authorization", "Bearer " + sharedPreferences.getString("jwt", ""))
                    .build();
        }


        return chain.proceed(newRequest);
    }
}