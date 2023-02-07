package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.MapFragment;
import com.cruisemobile.cruise.models.ReviewBasicDTO;
import com.cruisemobile.cruise.models.ReviewDTO;
import com.cruisemobile.cruise.models.ReviewPairDTO;
import com.cruisemobile.cruise.models.RideForTransferDTO;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.models.UserForRideDTO;
import com.cruisemobile.cruise.models.VehicleDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.Helper;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerHistoryRideDetailsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Long rideId;
    private Long userId;
    private String userEmail;
    private RideForTransferDTO ride;
    private TextView rideDateTime;
    private TextView rideLocations;
    private TextView splitFairCount;
    private ListView splitFairList;
    private TextView rideDuration;
    private TextView rideDistance;
    private TextView ridePrice;
    private TextView driverName;
    private TextView driverSurname;
    private RatingBar driverRating;
    private TextView vehicleMake;
    private TextView vehicleModel;
    private RatingBar vehicleRating;
    private TextInputEditText commentInput;
    private Button submitCommentBtn;
    private Button rebookBtn;
    private MapFragment map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_history_ride_details);

        rideDateTime = findViewById(R.id.passenger_ride_history_details_time);
        rideLocations = findViewById(R.id.passenger_ride_history_details_location);
        splitFairCount = findViewById(R.id.passeenger_ride_history_details_splitfare);
        splitFairList = findViewById(R.id.passenger_ride_history_cruise_splitfare);
        rideDuration = findViewById(R.id.passenger_ride_history_details_duration);
        rideDistance = findViewById(R.id.passenger_ride_history_details_distance);
        ridePrice = findViewById(R.id.passenger_ride_history_details_price);
        driverName = findViewById(R.id.passenger_current_ride_driver_name);
        driverSurname = findViewById(R.id.passenger_current_ride_driver_surname);
        driverRating = findViewById(R.id.driver_rating);
        vehicleMake = findViewById(R.id.passenger_current_ride_car_make);
        vehicleModel = findViewById(R.id.passenger_current_ride_car_model);
        vehicleRating = findViewById(R.id.car_rating);
        commentInput = findViewById(R.id.comment_field);
        rebookBtn = findViewById(R.id.rebook_from_history_btn);
        submitCommentBtn = findViewById(R.id.submit_comment_btn);

        Intent intent = getIntent();
        rideId = intent.getLongExtra("ride", -1);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userId = sharedPreferences.getLong("id", -1);
        userEmail = sharedPreferences.getString("email", "");
        loadRideData();

        FloatingActionButton backButton = findViewById(R.id.passenger_ride_history_back_fab);
        backButton.setOnClickListener(v -> onBackPressed());

        submitCommentBtn.setOnClickListener(v -> submitReviews());
        rebookBtn.setOnClickListener(v -> rebookRide());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        map = MapFragment.newInstance();
        transaction.replace(R.id.ride_details_map_to_show, map).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, PassengerRideHistoryActivity.class));
        finish();
    }

    private void loadRideData() {
        Call<RideForTransferDTO> call = ServiceUtils.rideEndpoints.getRideDetails(rideId);
        call.enqueue(new Callback<RideForTransferDTO>() {
            @Override
            public void onResponse(Call<RideForTransferDTO> call, Response<RideForTransferDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ride = response.body();
                    fillRideDetails(ride);
                } else {
                    Log.d("RIDE DETAILS", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RideForTransferDTO> call, Throwable t) {
                Log.e("RIDE HISTORY DETAILS", "Ride details could not be loaded " + t.getMessage());
            }
        });
    }

    private void fillRideDetails(RideForTransferDTO rideDetails) {
        rideDateTime.setText(String.format("%s - %s", Helper.formatISODateToOurDate(rideDetails.getStartTime()), Helper.formatISODateToOurDate(rideDetails.getEndTime())));
        rideLocations.setText(String.format("%s - %s", rideDetails.getLocations().get(0).getDeparture().getAddress(), rideDetails.getLocations().get(0).getDestination().getAddress()));
        splitFairCount.setText(String.format("%s friend(s)", rideDetails.getPassengers().size() - 1));
        rideDuration.setText(String.format("%s minutes", calculateRideDuration(rideDetails.getStartTime(), rideDetails.getEndTime())));
        rideDistance.setText(String.format("%s meters", rideDetails.getDistance()));
        ridePrice.setText(String.format("%s rsd", rideDetails.getTotalCost()));

        map.requestDirection(new LatLng(rideDetails.getLocations().get(0).getDeparture().getLatitude(), rideDetails.getLocations().get(0).getDeparture().getLongitude()), new LatLng(rideDetails.getLocations().get(0).getDestination().getLatitude(), rideDetails.getLocations().get(0).getDestination().getLongitude()));

        List<String> friendsEmails = new ArrayList<>();
        for (UserForRideDTO friendFromRide : rideDetails.getPassengers()) {
            if (!friendFromRide.getEmail().equals(userEmail)) {
                friendsEmails.add(friendFromRide.getEmail());
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, friendsEmails);
        splitFairList.setAdapter(adapter);

        loadDriverAndVehicleDetails(rideDetails.getDriver().getId());
        loadRatings();
    }


    private void loadDriverAndVehicleDetails(Long driverId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Call<UserDTO> call = ServiceUtils.driverEndpoints.getDriverDetails(driverId);
                call.enqueue(new Callback<UserDTO>() {
                    @Override
                    public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillDriverDetails(response.body());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<UserDTO> call, Throwable t) {
                        Log.e("RIDE HISTORY DETAILS", "Driver details could not be loaded " + t.getMessage());
                    }
                });
            }
        });
    }

    private void fillDriverDetails(UserDTO driver) {
        driverName.setText(driver.getName());
        driverSurname.setText(driver.getSurname());
        loadVehicleDetails(driver.getId());
    }

    private void loadVehicleDetails(Long driverId) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Call<VehicleDTO> call = ServiceUtils.driverEndpoints.getDriverVehicle(driverId);
                call.enqueue(new Callback<VehicleDTO>() {
                    @Override
                    public void onResponse(Call<VehicleDTO> call, Response<VehicleDTO> response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillVehicleDetails(response.body());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<VehicleDTO> call, Throwable t) {
                        Log.e("RIDE HISTORY DETAILS", "Vehicle details could not be loaded " + t.getMessage());
                    }
                });
            }
        });
    }

    private void fillVehicleDetails(VehicleDTO vehicle) {
        vehicleMake.setText(String.format("%s", vehicle.getModel()));
        vehicleModel.setText(String.format("%s", vehicle.getLicenseNumber()));
    }

    private void loadRatings() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Call<List<ReviewPairDTO>> call = ServiceUtils.reviewEndpoints.getAllRideReviews(rideId);

                call.enqueue(new Callback<List<ReviewPairDTO>>() {
                    @Override
                    public void onResponse(Call<List<ReviewPairDTO>> call, Response<List<ReviewPairDTO>> response) {
                        List<ReviewPairDTO> allRideReviews = response.body();
                        for (ReviewPairDTO reviewPair : allRideReviews) {
                            if (Objects.equals(reviewPair.getDriverReview().getPassenger().getId(), userId)) {
                                fillRideRatings(reviewPair);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ReviewPairDTO>> call, Throwable t) {
                        Log.e("RIDE HISTORY DETAILS", "Ride ratings could not be loaded " + t.getMessage());
                    }
                });

            }
        });
    }

    private void fillRideRatings(ReviewPairDTO rideReview) {
        driverRating.setRating((float) rideReview.getDriverReview().getRating());
        driverRating.setIsIndicator(true);
        vehicleRating.setRating((float) rideReview.getVehicleReview().getRating());
        vehicleRating.setIsIndicator(true);
        submitCommentBtn.setEnabled(false);
    }

    private long calculateRideDuration(String startDateTime, String endDateTime) {
        LocalDateTime start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime end = LocalDateTime.parse(endDateTime, DateTimeFormatter.ISO_DATE_TIME);
        return Duration.between(start, end).toMinutes();
    }

    private void submitReviews() {
        if (driverRating.getRating() > 0 && commentInput.getText().toString().trim().length() > 5) {
            submitDriverReview(driverRating.getRating(), commentInput.getText().toString().trim());
        } else {
            Toast.makeText(this, "Comment with length 5+ and rating are required", Toast.LENGTH_SHORT).show();
        }
        if (vehicleRating.getRating() > 0 && commentInput.getText().toString().trim().length() > 5) {
            submitVehicleReview(vehicleRating.getRating(), commentInput.getText().toString().trim());
        } else {
            Toast.makeText(this, "Comment with length 5+ and rating are required", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitDriverReview(float rating, String comment) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ReviewBasicDTO driversReview = new ReviewBasicDTO((int) rating, comment);
                Call<ReviewDTO> call = ServiceUtils.reviewEndpoints.submitDriverReview(rideId, driversReview);
                call.enqueue(new Callback<ReviewDTO>() {
                    @Override
                    public void onResponse(Call<ReviewDTO> call, Response<ReviewDTO> response) {
                        if (response.isSuccessful()) {
                            driverRating.setIsIndicator(true);
                            if (vehicleRating.isIndicator()) {
                                submitCommentBtn.setEnabled(false);
                            }
                            Toast.makeText(PassengerHistoryRideDetailsActivity.this, "Review added successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            driverRating.setRating(0);
                            try {
                                Toast.makeText(PassengerHistoryRideDetailsActivity.this, "Review not added successfully " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewDTO> call, Throwable t) {
                        Log.e("RIDE HISTORY DETAILS", "Driver ratings could not be submitted " + t.getMessage());
                        Toast.makeText(PassengerHistoryRideDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commentInput.setText("");
                    }
                });
            }
        });
    }

    private void submitVehicleReview(float rating, String comment) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ReviewBasicDTO vehicleReview = new ReviewBasicDTO((int) rating, comment);
                Call<ReviewDTO> call = ServiceUtils.reviewEndpoints.submitVehicleReview(rideId, vehicleReview);
                call.enqueue(new Callback<ReviewDTO>() {
                    @Override
                    public void onResponse(Call<ReviewDTO> call, Response<ReviewDTO> response) {
                        if (response.isSuccessful()) {
                            vehicleRating.setIsIndicator(true);
                            Toast.makeText(PassengerHistoryRideDetailsActivity.this, "Review added successfully", Toast.LENGTH_SHORT).show();
                            if (driverRating.isIndicator()) {
                                submitCommentBtn.setEnabled(false);
                            }
                        } else {
                            vehicleRating.setRating(0);
                            try {
                                Toast.makeText(PassengerHistoryRideDetailsActivity.this, "Review not added successfully " + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReviewDTO> call, Throwable t) {
                        Log.e("RIDE HISTORY DETAILS", "Vehicle ratings could not be submitted " + t.getMessage());
                        Toast.makeText(PassengerHistoryRideDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        commentInput.setText("");
                    }
                });
            }
        });
    }

    private void rebookRide() {
        Intent intent = new Intent(this, CruiseConfirmedActivity.class);
        intent.putExtra("from", ride.getLocations().get(0).getDeparture().getAddress());
        intent.putExtra("to", ride.getLocations().get(0).getDestination().getAddress());
        intent.putExtra("vehicleType", ride.getVehicleType());
        intent.putExtra("startTime", LocalDateTime.now().toString());
        intent.putExtra("petTransport", ride.getPetTransport());
        intent.putExtra("babyTransport", ride.getBabyTransport());
        startActivity(intent);
    }

}