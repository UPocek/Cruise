package com.cruisemobile.cruise.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.databinding.ActivityConfirmRideDetailsBinding;
import com.cruisemobile.cruise.fragments.MapFragment;
import com.cruisemobile.cruise.models.FavouriteRideBasicDTO;
import com.cruisemobile.cruise.models.FavouriteRideDTO;
import com.cruisemobile.cruise.models.LocationDTO;
import com.cruisemobile.cruise.models.LocationPairDTO;
import com.cruisemobile.cruise.models.UserForRideDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.services.ThirdPartyUtils;
import com.cruisemobile.cruise.tools.Helper;
import com.cruisemobile.cruise.tools.MyTimePickerTwo;
import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmRideDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ActivityConfirmRideDetailsBinding binding;
    private String from;
    private String to;
    private String startTime;
    private LocationDTO fromLocation;
    private LocationDTO toLocation;
    private SharedPreferences sharedPreferences;
    private Long favouriteId;
    private ImageButton markFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConfirmRideDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        MapFragment map = MapFragment.newInstance();
        transaction.replace(R.id.confirm_ride_details_map, map).commit();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        to = intent.getStringExtra("to");
        startTime = LocalDateTime.now().toString();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                fromLocation = getLocationFromAddress(from);
                toLocation = getLocationFromAddress(to);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fromLocation != null && toLocation != null) {
                            map.requestDirection(new LatLng(fromLocation.getLatitude(), fromLocation.getLongitude()), new LatLng(toLocation.getLatitude(), toLocation.getLongitude()));
                        } else {
                            Toast.makeText(ConfirmRideDetailsActivity.this, "One or both locations are not valid", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ConfirmRideDetailsActivity.this, PassangerMainActivity.class));
                            finish();
                        }
                    }
                });
            }
        });

        Spinner carTypeSpinner = binding.confirmRideDetailsCarTypes;
        carTypeSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> spinner2Adapter = ArrayAdapter.createFromResource(this, R.array.car_types, android.R.layout.simple_spinner_item);
        spinner2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        carTypeSpinner.setAdapter(spinner2Adapter);

        binding.timePickerForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerDialog = new MyTimePickerTwo();
                timePickerDialog.show(getSupportFragmentManager(), "timePicker");
            }
        });

        findViewById(R.id.back_button_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmRideDetailsActivity.this, PassangerMainActivity.class));
                finish();
            }
        });

        markFavourite = binding.markFavourite;
        markFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (markFavourite.getTag().toString().equals("empty")) {
                    addRideToFavourites();
                } else {
                    removeRideFromFavourite();
                }
            }
        });

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmRideDetailsActivity.this, CruiseConfirmedActivity.class);
                intent.putExtra("from", from);
                intent.putExtra("to", to);
                intent.putExtra("vehicleType", ((Spinner) findViewById(R.id.confirm_ride_details_car_types)).getSelectedItem().toString());
                intent.putExtra("startTime", startTime);
                intent.putExtra("petTransport", ((CheckBox) findViewById(R.id.confirm_ride_details_pets)).isChecked());
                intent.putExtra("babyTransport", ((CheckBox) findViewById(R.id.confirm_ride_details_baby)).isChecked());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String spinnerValue = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void processTimePickerResult(int hour, int minutes) {
        String hour_string = Integer.toString(hour);
        String minutes_string = Integer.toString(minutes);
        LocalDateTime rideTime = LocalDateTime.now().withHour(hour).withMinute(minutes);
        startTime = rideTime.toString();

        binding.timePickerForm.setText(String.format("Time: %s:%s", hour_string, minutes_string));
    }

    private LocationDTO getLocationFromAddress(String address) {
        Call<Map<String, Object>> call = ThirdPartyUtils.googleEndpoints.getLatLngFromAddress(address, ThirdPartyUtils.mapsApiKey);
        LocationDTO locationDTO = null;
        try {
            Map<String, Object> response = call.execute().body();
            List<Object> results = (List<Object>) response.get("results");
            Map<String, Object> resultToUse = (Map<String, Object>) results.get(0);
            Map<String, Object> geometry = (Map<String, Object>) resultToUse.get("geometry");
            Map<String, Object> location = (Map<String, Object>) geometry.get("location");
            locationDTO = new LocationDTO(address, (Double) location.get("lat"), (Double) location.get("lng"));
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return locationDTO;
    }

    private void addRideToFavourites() {
        Log.e("FAV RIDE", "Called to add");
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                String favouriteName = from + " - " + to;
                List<LocationPairDTO> locations = new ArrayList<>();
                locations.add(new LocationPairDTO(fromLocation, toLocation));
                List<UserForRideDTO> passengers = new ArrayList<>();
                passengers.add(Helper.getLoggedInUserAsUserForRide(sharedPreferences));
                FavouriteRideBasicDTO favouriteRideBasic = new FavouriteRideBasicDTO(favouriteName, locations, passengers, ((Spinner) findViewById(R.id.confirm_ride_details_car_types)).getSelectedItem().toString(), ((CheckBox) findViewById(R.id.confirm_ride_details_baby)).isChecked(), ((CheckBox) findViewById(R.id.confirm_ride_details_pets)).isChecked(), 1000.0);
                Call<FavouriteRideDTO> call = ServiceUtils.rideEndpoints.addFavouriteRide(favouriteRideBasic);
                call.enqueue(new Callback<FavouriteRideDTO>() {
                    @Override
                    public void onResponse(Call<FavouriteRideDTO> call, Response<FavouriteRideDTO> response) {
                        if(response.isSuccessful()){
                            markFavourite.setImageResource(R.drawable.ic_heart_full);
                            markFavourite.setTag("full");
                            favouriteId = response.body().getId();
                            System.out.println(favouriteId);
                        }
                    }

                    @Override
                    public void onFailure(Call<FavouriteRideDTO> call, Throwable t) {
                        Log.e("HISTORY", "Ride could not be added to favourite " + t.getMessage());
                    }
                });
            }
        });
    }

    private void removeRideFromFavourite() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Call<String> call = ServiceUtils.rideEndpoints.deleteFavouriteRide(favouriteId);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.isSuccessful()){
                            markFavourite.setImageResource(R.drawable.ic_heart_empty);
                            markFavourite.setTag("empty");
                            favouriteId = -1L;
                        }else{
                            Log.e("PICK RIDE", "Removing from favourite declined: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e("PICK RIDE", "Removing from favourite declined: " + t.getMessage());
                    }
                });
            }
        });
    }
}