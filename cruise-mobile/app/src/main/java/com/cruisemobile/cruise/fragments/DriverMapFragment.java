package com.cruisemobile.cruise.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.WorkingTimeDurationDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.ncorti.slidetoact.SlideToActView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverMapFragment extends Fragment {
    private SharedPreferences sharedPreferences;
    private MapFragment mMapFragment;
    private ExecutorService executorService;
    private Long workingTime;
    private int workingHours;
    private int workingMinutes;
    private Timer timer;


    public DriverMapFragment() {
        // Required empty public constructor
    }


    public static DriverMapFragment newInstance() {
        return new DriverMapFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_map, container, false);
        SlideToActView slideToActView = view.findViewById(R.id.driver_active_slider);
        slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
                if (slideToActView.getText().equals("Active")) {
                    executorService.execute(() -> {
                        try {
                            deactivateDriver();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                slideToActView.setText("Inactive");
                                slideToActView.setOuterColor(R.color.dark_gray);
                                slideToActView.setIconColor(R.color.dark_gray);
                                slideToActView.resetSlider();
                            }
                        });
                    });
                } else {
                    executorService.execute(() -> {
                        try {
                            activateDriver();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                slideToActView.setText("Active");
                                slideToActView.setOuterColor(R.color.amber);
                                slideToActView.setIconColor(R.color.amber);
                                slideToActView.resetSlider();
                            }
                        });
                    });

                }
            }
        });

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.driver_main_map, mMapFragment).commit();

        TextView nameField = view.findViewById(R.id.driver_name_surname_field);
        String welcomeMessage = "Welcome back " + sharedPreferences.getString("name", "") + " " + sharedPreferences.getString("surname", "");
        nameField.setText(welcomeMessage);
        startWorkingHour(view);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
        }
    }

    private void activateDriver() throws IOException {
        Call<Boolean> call = ServiceUtils.driverEndpoints.changeDriverActivity(sharedPreferences.getLong("id", 0L), true);
        call.execute();
    }

    private void deactivateDriver() throws IOException {
        Call<Boolean> call = ServiceUtils.driverEndpoints.changeDriverActivity(sharedPreferences.getLong("id", 0L), false);
        call.execute();
        timer.cancel();
    }

    private void startWorkingHour(View view) {
        executorService.execute(() -> {
            Call<WorkingTimeDurationDTO> call = ServiceUtils.driverEndpoints.getDriversWorkingTime(sharedPreferences.getLong("id", 0L));
            call.enqueue(new Callback<WorkingTimeDurationDTO>() {
                @Override
                public void onResponse(Call<WorkingTimeDurationDTO> call, Response<WorkingTimeDurationDTO> response) {
                    updateTimer(view, response.body().getDuration());
                }

                @Override
                public void onFailure(Call<WorkingTimeDurationDTO> call, Throwable t) {

                }
            });
        });
    }

    private void updateTimer(View view, Long duration) {
        this.workingTime = duration;
        convertWorkingTimeFromMilis();
        TextView workingTime = view.findViewById(R.id.driver_timer);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                workingMinutes++;
                if (workingMinutes == 60) {
                    workingHours++;
                    workingMinutes = 0;
                }
                if (workingHours == 8) {
                    try {
                        deactivateDriver();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                try {
                    getActivity().runOnUiThread(() -> {
                        workingTime.setText(workingHours + "h " + workingMinutes + "min");
                    });
                } catch (IllegalStateException e) {
                    this.cancel();
                }

            }
        }, 0, 60000);
    }

    private void convertWorkingTimeFromMilis() {
        int seconds = (int) Math.floor(this.workingTime / 1000);
        int minutes = (int) Math.floor(seconds / 60);
        int hours = (int) Math.floor(minutes / 60);

        minutes = minutes % 60;

        this.workingHours = hours;
        this.workingMinutes = minutes;
    }

}
