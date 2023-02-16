package com.cruisemobile.cruise.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.cruisemobile.cruise.R;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {

    private final ActivityResultLauncher<String> permission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
        if (result) {
            int SPLASH_TIME_OUT = 2800;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, UserLoginActivity.class));
                    finish();
                }
            }, SPLASH_TIME_OUT);
        } else {
            finish();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        checkInternetConncetion();
        checkLocation();
    }

    private void checkInternetConncetion() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if (networkCapabilities == null) {
            Toast.makeText(getApplicationContext(), "You are not connected to the internet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isLocationEnabled()) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.oops)
                    .setMessage(R.string.location_disabled_message)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", ((dialogInterface, i) -> finish()))
                    .create().show();
        } else {
            permission.launch(Manifest.permission.ACCESS_FINE_LOCATION);

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        permission.launch(Manifest.permission.ACCESS_FINE_LOCATION);

    }
}