package com.cruisemobile.cruise.fragments.passenger;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.activities.MessagesActivity;
import com.cruisemobile.cruise.fragments.MapFragment;
import com.cruisemobile.cruise.models.NoteDTO;
import com.cruisemobile.cruise.models.NoteWithDateDTO;
import com.cruisemobile.cruise.models.PanicDTO;
import com.cruisemobile.cruise.models.ReasonDTO;
import com.cruisemobile.cruise.models.RideDTO;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.models.VehicleDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PassengerCurrentRideFragment extends Fragment {
    Timer timer;
    ImageView messageImageBtn;
    ImageView callImageBtn;
    RideDTO ride;
    LatLng departure;
    LatLng destination;
    private SharedPreferences sharedPreferences;
    private double elapsedTime;
    private int minutes;
    private int seconds;
    private MapFragment mMapFragment;
    private String driverFullName;
    private String driverTelephoneNumber;

    public PassengerCurrentRideFragment() {
    }

    public PassengerCurrentRideFragment(RideDTO ride) {
        this.ride = ride;
    }

    public static PassengerCurrentRideFragment newInstance(RideDTO ride) {

        return new PassengerCurrentRideFragment(ride);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("ride", ride.toString());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMapFragment = MapFragment.newInstance();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.passenger_current_ride_map_placeholder, mMapFragment).commit();
        driverFullName = "";
    }

    @Override
    public void onResume() {
        super.onResume();

        requestDirection();
    }

    private void requestDirection() {
        try {
            Log.e("departure", ride.getLocations()[0].getDeparture().getAddress());
            mMapFragment.requestDirection(departure, destination);
        } catch (Exception e) {
            Log.e("MAPA", "nije ucitana");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_current_ride, container, false);
        // Inflate the layout for this fragment
        messageImageBtn = view.findViewById(R.id.current_ride_passenger_message);
        callImageBtn = view.findViewById(R.id.current_ride_passenger_call);

        fillView(ride, view);

        messageImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessagesActivity.class);
                intent.putExtra("rideId", ride.getId());
                intent.putExtra("type", "RIDE");
                intent.putExtra("otherId", ride.getDriver().getId());
                intent.putExtra("otherFullName", driverFullName);
                startActivity(intent);
            }
        });

        callImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phone_intent = new Intent(Intent.ACTION_DIAL);
                phone_intent.setData(Uri.parse("tel:" + driverTelephoneNumber));
                startActivity(phone_intent);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }


    @SuppressLint("SetTextI18n")
    private void fillView(RideDTO ride, View view) {
        TextView price = view.findViewById(R.id.passenger_current_ride_price);
        TextView estimatedTime = view.findViewById(R.id.passenger_current_ride_estimated_time);
        TextView driverName = view.findViewById(R.id.passenger_current_ride_driver_name);
        TextView driverSurname = view.findViewById(R.id.passenger_current_ride_driver_surname);
        TextView carModel = view.findViewById(R.id.passenger_current_ride_car_model);

        Button panic = view.findViewById(R.id.passenger_current_ride_panic_btn);
        panic.setOnClickListener(view1 -> panic(view, ride.getId()));

        Button report = view.findViewById(R.id.passenger_current_ride_report_btn);
        report.setOnClickListener(view1 -> report(view, sharedPreferences.getLong("id", 0L)));

        price.setText(ride.getTotalCost() + "RSD");
        estimatedTime.setText(ride.getEndTime().split("T")[1].split("\\.")[0]);

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Call<UserDTO> call = ServiceUtils.driverEndpoints.getActiveDriverDetails(ride.getDriver().getEmail());
                try {
                    UserDTO driver = call.execute().body();
                    if (driver != null) {
                        driverFullName = driver.getName() + " " + driver.getSurname();
                        driverTelephoneNumber = driver.getTelephoneNumber();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                driverName.setText(driver.getName());
                                driverSurname.setText(driver.getSurname());
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Call<VehicleDTO> call2 = ServiceUtils.driverEndpoints.getDriverVehicle(ride.getDriver().getId());
        call2.enqueue(new Callback<VehicleDTO>() {
            @Override
            public void onResponse(Call<VehicleDTO> call, Response<VehicleDTO> response) {
                carModel.setText(response.body().getModel());
            }

            @Override
            public void onFailure(Call<VehicleDTO> call, Throwable t) {

            }
        });

        departure = new LatLng(ride.getLocations()[0].getDeparture().getLatitude(), ride.getLocations()[0].getDeparture().getLongitude());
        destination = new LatLng(ride.getLocations()[0].getDestination().getLatitude(), ride.getLocations()[0].getDestination().getLongitude());

        if (ride.getStatus().equals("ACTIVE")) {
            startTimer(view);
        }

    }

    private void startTimer(View view) {
        TextView elapsedTimeText = view.findViewById(R.id.passenger_current_ride_elapsed_time);
        elapsedTime = 0;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    minutes = (int) Math.floor(elapsedTime / 60);
                    seconds = (int) (elapsedTime % 60);
                    elapsedTimeText.setText(minutes + "m " + seconds + "s");
                    elapsedTime += 1;
                });

            }
        }, 0, 1000);
    }

    private void report(View view, Long id) {
        EditText message = view.findViewById(R.id.passenger_current_ride_message);
        String messageText = String.valueOf(message.getText());
        if (messageText.equals(""))
            Toast.makeText(getContext(), "You have to enter reason for report", Toast.LENGTH_SHORT).show();
        else {
            Call<NoteWithDateDTO> noteCall = ServiceUtils.userEndpoints.createNote(id, new NoteDTO(messageText));
            noteCall.enqueue(new Callback<NoteWithDateDTO>() {
                @Override
                public void onResponse(Call<NoteWithDateDTO> call, Response<NoteWithDateDTO> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Note forwarded to administration!", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getContext(), "Note exception", Toast.LENGTH_SHORT).show();
                        Log.e("REPORT IN", "Note exception" + response.code());
                    }

                }

                @Override
                public void onFailure(Call<NoteWithDateDTO> call, Throwable t) {
                    Toast.makeText(getContext(), "Note error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void panic(View view, Long id) {
        EditText message = view.findViewById(R.id.passenger_current_ride_message);
        String messageText = String.valueOf(message.getText());
        String jwt = sharedPreferences.getString("jwt", "");
        if (messageText.equals(""))
            Toast.makeText(getContext(), "You have to enter reason for panic", Toast.LENGTH_SHORT).show();
        else {
            Call<PanicDTO> panicCall = ServiceUtils.rideEndpoints.panic(id, new ReasonDTO(messageText));
            panicCall.enqueue(new Callback<PanicDTO>() {
                @Override
                public void onResponse(Call<PanicDTO> call, Response<PanicDTO> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Panic forwarded to administarion!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Panic exception", Toast.LENGTH_SHORT).show();
                        Log.e("PANIC", "Panic exception" + response.code());
                    }
                }

                @Override
                public void onFailure(Call<PanicDTO> call, Throwable t) {
                    Toast.makeText(getContext(), "Panic error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}