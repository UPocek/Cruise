package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.LocationDTO;
import com.cruisemobile.cruise.models.LocationPairDTO;
import com.cruisemobile.cruise.models.OfferDTO;
import com.cruisemobile.cruise.models.RideForFutureDTO;
import com.cruisemobile.cruise.models.RideForTransferDTO;
import com.cruisemobile.cruise.models.UserForRideDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.Helper;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.gusavila92.websocketclient.WebSocketClient;

public class CruiseConfirmedActivity extends AppCompatActivity {

    ImageView rideStateCheckmark;
    private WebSocketClient webSocketClient;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cruise_confirmed);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        gson = new Gson();

        findViewById(R.id.back_button_cruise_confirmed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CruiseConfirmedActivity.this, PassangerMainActivity.class));
                finish();
            }
        });

        findViewById(R.id.back_text_confirmed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CruiseConfirmedActivity.this, PassangerMainActivity.class));
                finish();
            }
        });

        Intent intent = getIntent();
        executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    RideForTransferDTO ride = getRideDetails(intent);
                    System.out.println(ride.getStartTime());
                    System.out.println(Duration.between(LocalDateTime.now(), LocalDateTime.parse(ride.getStartTime())).compareTo(Duration.ofMinutes(15)) > 0);
                    if (Duration.between(LocalDateTime.now(), LocalDateTime.parse(ride.getStartTime())).compareTo(Duration.ofMinutes(15)) > 0) {
                        RideForFutureDTO rideForFutureDTO = constructRideForFuture(ride);
                        RideForTransferDTO futureRideId = requestRideForFuture(rideForFutureDTO);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                informPassengerAboutFutureRideStatus(futureRideId);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                requestRideForNow(ride);
                            }
                        });
                    }

                } catch (IOException e) {
                    startActivity(new Intent(CruiseConfirmedActivity.this, PassangerMainActivity.class));
                    finish();
                }
            }
        });
    }

    private RideForTransferDTO getRideDetails(Intent intent) throws IOException {
        RideForTransferDTO ride = new RideForTransferDTO();
        ride.setId(-1L);
        List<UserForRideDTO> passengers = new ArrayList<>();
        passengers.add(new UserForRideDTO(sharedPreferences.getLong("id", -1L), sharedPreferences.getString("email", "")));
        ride.setPassengers(passengers);
        ride.setVehicleType(intent.getStringExtra("vehicleType").toUpperCase());
        ride.setBabyTransport(intent.getBooleanExtra("babyTransport", false));
        ride.setPetTransport(intent.getBooleanExtra("petTransport", false));
        String departureAddress = intent.getStringExtra("from");
        String destinationAddress = intent.getStringExtra("to");
        LocationDTO departure = Helper.getLocationFromAddress(departureAddress);
        LocationDTO destination = Helper.getLocationFromAddress(destinationAddress);
        OfferDTO offer = Helper.getEstimationForRequest(departure, destination, ride.getVehicleType(), ride.getBabyTransport(), ride.getPetTransport());
        List<LocationPairDTO> locations = new ArrayList<>();
        LocationPairDTO route = new LocationPairDTO(departure, destination);
        locations.add(route);
        ride.setLocations(locations);
        ride.setEstimatedTimeInMinutes(offer.getEstimatedTimeInMinutes());
        ride.setDistance(offer.getDistance());
        ride.setTotalCost(offer.getEstimatedCost());
        ride.setStartTime(intent.getStringExtra("startTime"));
        ride.setEndTime(intent.getStringExtra("startTime"));
        ride.setRejection(null);
        ride.setDriver(null);
        ride.setStatus("");

        return ride;
    }

    private RideForFutureDTO constructRideForFuture(RideForTransferDTO ride) {
        RideForFutureDTO rideForFutureDTO = new RideForFutureDTO();
        rideForFutureDTO.setPassengers(ride.getPassengers());
        rideForFutureDTO.setVehicleType(ride.getVehicleType());
        rideForFutureDTO.setBabyTransport(ride.getBabyTransport());
        rideForFutureDTO.setPetTransport(ride.getPetTransport());
        rideForFutureDTO.setLocations(ride.getLocations());
        rideForFutureDTO.setTimeEstimation(ride.getEstimatedTimeInMinutes());
        rideForFutureDTO.setDistance(ride.getDistance());
        rideForFutureDTO.setPrice(ride.getTotalCost());
        rideForFutureDTO.setStartTime(ride.getStartTime());

        return rideForFutureDTO;
    }

    private RideForTransferDTO requestRideForFuture(RideForFutureDTO ride) throws IOException {
        Call<RideForTransferDTO> call = ServiceUtils.rideEndpoints.rideForFutureRequest(ride);
        return call.execute().body();
    }

    private void informPassengerAboutFutureRideStatus(RideForTransferDTO rideForTransfer) {
        ImageView rideStateCheckmark = findViewById(R.id.ride_processing_checkmark);
        TextView rideStateText = findViewById(R.id.ride_processing_title);
        TextView rideStateTimeToDriverArrival = findViewById(R.id.ride_processing_time_to_cruise_arrival);
        if (rideForTransfer == null) {
            rideStateCheckmark.setImageResource(R.drawable.denied);
            rideStateText.setText(R.string.cruise_denied);
            rideStateTimeToDriverArrival.setText("Can't request ride...");
        } else {
            rideStateCheckmark.setImageResource(R.drawable.confirmed);
            rideStateText.setText(R.string.cruise_confirmed);
            rideStateTimeToDriverArrival.setText("Driver will be on your pickup location in scheduled minutes");
        }
    }

    private void requestRideForNow(RideForTransferDTO ride) {
        URI uri;
        ImageView rideStateCheckmark = findViewById(R.id.ride_processing_checkmark);
        TextView rideStateText = findViewById(R.id.ride_processing_title);
        TextView rideStateTimeToDriverArrival = findViewById(R.id.ride_processing_time_to_cruise_arrival);
        try {
            // Connect to local host
            uri = new URI("ws://" + ServiceUtils.SERVER_IP + ":8080/websocket");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
                Call<Void> call = ServiceUtils.rideRequestEndpoints.rideTransfer(ride);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.d("RIDE", "Ride not requested");
                        rideStateCheckmark.setImageResource(R.drawable.denied);
                        rideStateText.setText(R.string.cruise_denied);
                        rideStateTimeToDriverArrival.setText("Server error");
                    }
                });
//                webSocketClient.send(gson.toJson(ride));
            }

            @Override
            public void onTextReceived(String payload) {
                Log.i("WebSocket", "Message received");
                RideForTransferDTO ride = gson.fromJson(payload, RideForTransferDTO.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (ride.getStatus()) {
                            case "ACCEPTED":
                                rideStateCheckmark.setImageResource(R.drawable.confirmed);
                                rideStateText.setText(R.string.cruise_confirmed);
                                rideStateTimeToDriverArrival.setText("Driver will be on your pickup location in a few minutes");
                                break;
                            case "REJECTED":
                                rideStateCheckmark.setImageResource(R.drawable.denied);
                                rideStateText.setText(R.string.cruise_denied);
                                rideStateTimeToDriverArrival.setText("No drivers available at this point");
                                break;
                            case "FORBIDDEN":
                                rideStateCheckmark.setImageResource(R.drawable.denied);
                                rideStateText.setText(R.string.cruise_denied);
                                rideStateTimeToDriverArrival.setText("You already have ride in process");
                                break;
                        }
                    }
                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                Log.e("CRUISE CONFIRMED", e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
            }
        };

//        webSocketClient.setConnectTimeout(10000);
//        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(10000);
        webSocketClient.addHeader("id", "" + sharedPreferences.getLong("id", -1L));
        webSocketClient.addHeader("role", "" + sharedPreferences.getString("role", ""));
        webSocketClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
        executorService.shutdown();
        sharedPreferences = null;
        gson = null;
    }
}