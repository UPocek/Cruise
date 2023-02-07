package com.cruisemobile.cruise.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.MapFragment;
import com.cruisemobile.cruise.models.RejectionDTO;
import com.cruisemobile.cruise.models.RideForTransferDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverAcceptanceRide extends AppCompatActivity {
    RideForTransferDTO ride;
    Gson gson;
    LatLng departure;
    LatLng destination;
    private MapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_acceptance_ride);
        mMapFragment = MapFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.acceptance_map, mMapFragment).commit();
        gson = new Gson();
        ride = gson.fromJson(getIntent().getStringExtra("ride"), RideForTransferDTO.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        addCallbackForButtons();
        fillView();
        requestDirection();
    }

    private void requestDirection() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                departure = new LatLng(ride.getLocations().get(0).getDeparture().getLatitude(), ride.getLocations().get(0).getDeparture().getLongitude());
                destination = new LatLng(ride.getLocations().get(0).getDestination().getLatitude(), ride.getLocations().get(0).getDestination().getLongitude());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMapFragment.requestDirection(departure, destination);
                    }
                });
            }
        });


    }

    private void addCallbackForButtons() {
        EditText rejection = findViewById(R.id.acceptance_rejection_txt);
        Log.e("ride", getIntent().getStringExtra("ride"));
        Log.e("ride_id", ride.getId().toString());
        Button yes = findViewById(R.id.acceptance_yes_btn);
        yes.setOnClickListener(view -> {
            Call<Void> call = ServiceUtils.rideRequestEndpoints.rideTransfer(ride);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    Log.d("RIDE", "Ride accepted");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("RIDE", "Ride not accepted");
                }
            });
            onBackPressed();
        });

        Button no = findViewById(R.id.acceptance_no_btn);
        no.setOnClickListener(view -> {
            if (String.valueOf(rejection.getText()).equals("")) {
                Toast.makeText(getApplicationContext(), "You must enter reason before rejection", Toast.LENGTH_SHORT).show();
            } else {
                RejectionDTO rejectionDTO = new RejectionDTO();
                rejectionDTO.setReason(String.valueOf(rejection.getText()));
                rejectionDTO.setTimeOfRejection(String.valueOf(LocalDateTime.now()));
                ride.setRejection(rejectionDTO);
                Log.e("id", ride.getId().toString());
                Call<Void> call = ServiceUtils.rideRequestEndpoints.rideTransfer(ride);
                Log.e("RIDE", ride.toString());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d("RIDE", "Ride rejected");
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("RIDE", "Ride not rejected");
                    }
                });
                onBackPressed();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void fillView() {
        TextView locations = findViewById(R.id.acceptance_location_txt);
        locations.setText("from " + ride.getLocations().get(0).getDeparture().getAddress() + "\nto " + ride.getLocations().get(0).getDestination().getAddress());

        TextView estimatedTime = findViewById(R.id.acceptance_estimated_time);
        estimatedTime.setText(ride.getEstimatedTimeInMinutes() + " min");

        TextView distance = findViewById(R.id.acceptance_distance);
        distance.setText(ride.getDistance() / 1000 + " km");

        TextView price = findViewById(R.id.acceptance_price);
        price.setText(ride.getTotalCost() + " RSD");

        TextView passengers = findViewById(R.id.acceptance_passengers);
        passengers.setText(String.valueOf(ride.getPassengers().size()));


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DriverMainActivity.class));
        finish();
    }

}