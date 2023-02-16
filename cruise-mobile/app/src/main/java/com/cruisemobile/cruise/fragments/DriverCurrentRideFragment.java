package com.cruisemobile.cruise.fragments;

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
import com.cruisemobile.cruise.models.PanicDTO;
import com.cruisemobile.cruise.models.ReasonDTO;
import com.cruisemobile.cruise.models.RideForTransferDTO;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.DriverFragmentTransition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverCurrentRideFragment extends Fragment {
    public RideForTransferDTO ride;
    ImageView messageImageBtn;
    ImageView callImageBtn;
    TextView passengerName;
    TextView passengerSurname;
    Button panicBtn;
    Button cancelBtn;
    Button startOrEndRideBtn;
    TextView elapsedTimeText;
    EditText message;
    String messageText;
    private SharedPreferences sharedPreferences;
    private Timer timer;
    private MapFragment mMap;
    private double elapsedTime;
    private int minutes;
    private int seconds;
    private String passengerFullName;
    private String passengerTelephoneNumber;


    public DriverCurrentRideFragment() {
        // Required empty public constructor
    }


    public static DriverCurrentRideFragment newInstance() {
        return new DriverCurrentRideFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        passengerFullName = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_current_ride, container, false);

        messageImageBtn = view.findViewById(R.id.current_ride_driver_message);
        callImageBtn = view.findViewById(R.id.current_ride_driver_call);
        panicBtn = view.findViewById(R.id.driver_current_ride_panic_btn);
        cancelBtn = view.findViewById(R.id.driver_current_ride_cancel_btn);
        startOrEndRideBtn = view.findViewById(R.id.driver_current_ride_start_end_btn);
        panicBtn.setEnabled(false);
        startOrEndRideBtn.setEnabled(false);
        cancelBtn.setEnabled(false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap = MapFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.driver_current_ride_map, mMap).commit();

        findRide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    private void findRide() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                Call<RideForTransferDTO> callGetActiveRide = ServiceUtils.rideEndpoints.getActiveRideForDriver(sharedPreferences.getLong("id", -1L));
                try {
                    RideForTransferDTO rideResponseActive = callGetActiveRide.execute().body();
                    if (rideResponseActive != null) {
                        Log.e("ACTIVE", "ima ride");
                        ride = rideResponseActive;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fillView(ride);
                            }
                        });
                        return;
                    }
                } catch (Exception e) {
                    Log.e("ACTIVE", "nema ride");
                }
                try {
                    Call<RideForTransferDTO> callGetAcceptedRide = ServiceUtils.rideEndpoints.getAcceptedRideForDriver(sharedPreferences.getLong("id", -1L));
                    RideForTransferDTO rideResponseAccepted = callGetAcceptedRide.execute().body();
                    Log.e("STATUS", "treci");
                    if (rideResponseAccepted != null) {
                        ride = rideResponseAccepted;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("ACCEPTED", "ima ride");
                                fillView(ride);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e("NEMA", "nema ride");
                    DriverFragmentTransition.to(DriverNoCurrentRideFragment.newInstance(), getActivity(), false);
                }
            }
        });
    }

    private void startTimer() {
        elapsedTimeText = getView().findViewById(R.id.driver_current_ride_elapsed_time);
        elapsedTime = 0;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                minutes = (int) Math.floor(elapsedTime / 60);
                seconds = (int) (elapsedTime % 60);
                elapsedTime += 1;
                try {
                    requireActivity().runOnUiThread(() -> {
                        elapsedTimeText.setText(minutes + "m " + seconds + "s");
                    });
                } catch (IllegalStateException e) {
                    this.cancel();
                }

            }
        }, 0, 1000);
    }

    @SuppressLint("SetTextI18n")
    private void fillView(RideForTransferDTO ride) {
        passengerName = getView().findViewById(R.id.driver_current_ride_passenger_name);
        passengerSurname = getView().findViewById(R.id.driver_current_ride_passenger_surname);

        panicBtn.setEnabled(true);
        startOrEndRideBtn.setEnabled(true);
        cancelBtn.setEnabled(true);

        panicBtn.setOnClickListener(view1 -> panic(getView(), ride.getId()));
        startOrEndRideBtn.setOnClickListener(view1 -> startOrEndRide(ride.getId()));
        cancelBtn.setOnClickListener(view1 -> cancelRide(getView(), ride.getId()));

        Call<UserDTO> call = ServiceUtils.passengerEndpoints.getActivePassengerDetails(ride.getPassengers().get(0).getEmail());
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    UserDTO passenger = call.execute().body();
                    if (passenger != null) {
                        passengerFullName = passenger.getName() + " " + passenger.getSurname();
                        passengerTelephoneNumber = passenger.getTelephoneNumber();
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                passengerName.setText(passenger.getName());
                                passengerSurname.setText(passenger.getSurname());
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        messageImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MessagesActivity.class);
                intent.putExtra("rideId", ride.getId());
                intent.putExtra("otherId", ride.getPassengers().get(0).getId());
                intent.putExtra("otherFullName", passengerFullName);
                intent.putExtra("type", "RIDE");
                startActivity(intent);
            }
        });

        callImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phone_intent = new Intent(Intent.ACTION_DIAL);
                phone_intent.setData(Uri.parse("tel:" + passengerTelephoneNumber));
                startActivity(phone_intent);
            }
        });

        LatLng departure = new LatLng(ride.getLocations().get(0).getDeparture().getLatitude(), ride.getLocations().get(0).getDeparture().getLongitude());
        LatLng destination = new LatLng(ride.getLocations().get(0).getDestination().getLatitude(), ride.getLocations().get(0).getDestination().getLongitude());

        mMap.requestDirection(departure, destination);
        if (ride.getStatus().equals("ACTIVE")) {
            startTimer();
            startOrEndRideBtn.setText("End");
        }
    }

    private void panic(View view, Long id) {
        message = view.findViewById(R.id.driver_current_ride_message);
        messageText = String.valueOf(message.getText());
        if (messageText.equals(""))
            Toast.makeText(getContext(), "You have to enter reason for panic", Toast.LENGTH_SHORT).show();
        else {
            Call<PanicDTO> panicCall = ServiceUtils.rideEndpoints.panic(id, new ReasonDTO(messageText));
            panicCall.enqueue(new Callback<PanicDTO>() {
                @Override
                public void onResponse(Call<PanicDTO> call, Response<PanicDTO> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Panic forwarded to administarion!", Toast.LENGTH_SHORT).show();
                        message.setText("");
                        DriverFragmentTransition.to(DriverMapFragment.newInstance(), getActivity(), false);
                    } else {
                        Toast.makeText(getContext(), "Message for panic is not valid " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<PanicDTO> call, Throwable t) {
                    Toast.makeText(getContext(), "Panic error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void startOrEndRide(Long id) {
        if (startOrEndRideBtn.getText().toString().equals("Start")) {
            Call<RideForTransferDTO> startRide = ServiceUtils.rideEndpoints.startRide(id);
            startRide.enqueue(new Callback<RideForTransferDTO>() {
                @Override
                public void onResponse(Call<RideForTransferDTO> call, Response<RideForTransferDTO> response) {
                    if (response.code() == 200) {
                        startOrEndRideBtn.setText("End");
                        cancelBtn.setEnabled(false);
                        startTimer();
                    } else {
                        Log.d("", "");
                    }
                }

                @Override
                public void onFailure(Call<RideForTransferDTO> call, Throwable t) {
                }
            });
            return;
        }
        Call<RideForTransferDTO> endRide = ServiceUtils.rideEndpoints.endRide(id);
        endRide.enqueue(new Callback<RideForTransferDTO>() {
            @Override
            public void onResponse(Call<RideForTransferDTO> call, Response<RideForTransferDTO> response) {
                if (response.code() == 200) {
                    DriverFragmentTransition.to(DriverMapFragment.newInstance(), getActivity(), false);
                } else {
                    Log.d("DRIVER RIDE", "Not able to end ride " + response.code());
                }
            }

            @Override
            public void onFailure(Call<RideForTransferDTO> call, Throwable t) {
            }
        });
    }

    private void cancelRide(View view, Long id) {
        message = view.findViewById(R.id.driver_current_ride_message);
        messageText = String.valueOf(message.getText());
        if (messageText.equals(""))
            Toast.makeText(getContext(), "You have to enter reason for cancellation!", Toast.LENGTH_SHORT).show();
        else {
            Call<RideForTransferDTO> cancelCall = ServiceUtils.rideEndpoints.cancel(id, new ReasonDTO(messageText));
            cancelCall.enqueue(new Callback<RideForTransferDTO>() {
                @Override
                public void onResponse(Call<RideForTransferDTO> call, Response<RideForTransferDTO> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "You canceled this ride!", Toast.LENGTH_SHORT).show();
                        DriverFragmentTransition.to(DriverMapFragment.newInstance(), getActivity(), false);
                    } else {
                        Toast.makeText(getContext(), "Message for cancel is not valid", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<RideForTransferDTO> call, Throwable t) {
                    Toast.makeText(getContext(), "Cancel error!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}