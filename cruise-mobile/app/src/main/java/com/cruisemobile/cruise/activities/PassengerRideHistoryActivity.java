package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.adapters.PaginationScrollListener;
import com.cruisemobile.cruise.adapters.PassengerRidesHistoryAdapter;
import com.cruisemobile.cruise.models.AllHistoryItemsDTO;
import com.cruisemobile.cruise.models.FavouriteRideDTO;
import com.cruisemobile.cruise.models.RideForUserDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;


public class PassengerRideHistoryActivity extends AppCompatActivity implements SensorEventListener {
    private static final int PAGE_START = 0;
    private static final int PAGE_SIZE = 10;
    private static final int SHAKE_THRESHOLD = 800;
    private SharedPreferences sharedPreferences;
    private ArrayList<RideForUserDTO> userRidesFromHistory;
    private PassengerRidesHistoryAdapter passengerRidesHistoryAdapter;
    private RecyclerView rideHistoryRecyclerView;
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ExecutorService executorService;
    private ProgressBar progressBar;
    private int total_pages_ride;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private SensorManager sensorManager;
    private long lastUpdate;
    private float last_x;
    private float last_y;
    private float last_z;
    private boolean sortOrderReverse = false;
    private ArrayList<FavouriteRideDTO> userFavouriteRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_ride_history);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        topAppBar = findViewById(R.id.topAppBarPassengerHistory);
        drawerLayout = findViewById(R.id.drawerLayoutPassengerHistory);
        navigationView = findViewById(R.id.navigationViewPassengerHistory);
        progressBar = findViewById(R.id.progressbar_history);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        executorService = Executors.newSingleThreadExecutor();
        userRidesFromHistory = new ArrayList<>();
        userFavouriteRides = new ArrayList<>();

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.passengerMainActivity:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassengerRideHistoryActivity.this, PassangerMainActivity.class));
                        break;
                    case R.id.passengerAccount:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassengerRideHistoryActivity.this, PassengerAccountActivity.class));

                        break;
                    case R.id.passengerInbox:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassengerRideHistoryActivity.this, PassengerInboxActivity.class));

                        break;
                }
                return true;
            }
        });

        passengerRidesHistoryAdapter = new PassengerRidesHistoryAdapter(this, userRidesFromHistory, sharedPreferences.getLong("id", -1), userFavouriteRides);
        rideHistoryRecyclerView = findViewById(R.id.passenger_rides_history_list);
        rideHistoryRecyclerView.setAdapter(passengerRidesHistoryAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rideHistoryRecyclerView.setLayoutManager(linearLayoutManager);
        rideHistoryRecyclerView.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

        });

        loadFirstPage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void loadFirstPage() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                loadUserFavouriteRides();
                loadUserHistoryItems();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        passengerRidesHistoryAdapter.setRidesList(userRidesFromHistory, sortOrderReverse);
                        progressBar.setVisibility(View.GONE);
                        if (currentPage < total_pages_ride) {
                            passengerRidesHistoryAdapter.addLoadingFooter();
                        } else {
                            isLastPage = true;
                        }
                    }
                });
            }
        });
    }

    private void loadNextPage() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                loadUserHistoryItems();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        passengerRidesHistoryAdapter.setRidesList(userRidesFromHistory, sortOrderReverse);
                        passengerRidesHistoryAdapter.removeLoadingFooter();
                        isLoading = false;
                        if (currentPage < total_pages_ride) {
                            passengerRidesHistoryAdapter.addLoadingFooter();
                        } else {
                            isLastPage = true;
                        }
                    }
                });
            }
        });
    }

    private void loadUserHistoryItems() {
        Long userId = sharedPreferences.getLong("id", -1);
        Call<AllHistoryItemsDTO> call = ServiceUtils.userEndpoints.getAllUserHistoryItems(userId, PAGE_SIZE, currentPage);
        AllHistoryItemsDTO allHistoryItems;
        try {
            allHistoryItems = call.execute().body();
            total_pages_ride = allHistoryItems.getRidePageCount();
            userRidesFromHistory.addAll(allHistoryItems.getHistoryItems());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUserFavouriteRides() {
        Call<ArrayList<FavouriteRideDTO>> call = ServiceUtils.rideEndpoints.getAllPassengerFavouriteRides();
        try {
            userFavouriteRides = call.execute().body();
            passengerRidesHistoryAdapter.addNewFavourites(userFavouriteRides);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            // only allow one update every 100ms.
            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float[] values = sensorEvent.values;
                float x = values[0];
                float y = values[1];
                float z = values[2];

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    sortOrderReverse = !sortOrderReverse;
                    passengerRidesHistoryAdapter.sortOnShakeChatItems(sortOrderReverse);
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}