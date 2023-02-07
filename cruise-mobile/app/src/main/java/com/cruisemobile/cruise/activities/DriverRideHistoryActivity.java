package com.cruisemobile.cruise.activities;

import android.content.Context;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.adapters.DriverRidesHistoryAdapter;
import com.cruisemobile.cruise.adapters.PaginationScrollListener;
import com.cruisemobile.cruise.models.AllHistoryItemsDTO;
import com.cruisemobile.cruise.models.RideForUserDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class DriverRideHistoryActivity extends AppCompatActivity implements SensorEventListener {
    private static final double SHAKE_THRESHOLD = 2.0;
    private static final int PAGE_START = 0;
    private static final int PAGE_SIZE = 10;
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private ExecutorService executorService;
    private ArrayList<RideForUserDTO> userRidesFromHistory;
    private RecyclerView rideHistoryRecyclerView;
    private ProgressBar progressBar;
    private int total_pages_ride;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private DriverRidesHistoryAdapter driverRidesHistoryAdapter;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long lastUpdate;
    private String sort = "startTime-asc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_ride_history);
        topAppBar = findViewById(R.id.topAppBarDriverHistory);
        drawerLayout = findViewById(R.id.drawerLayoutDriverHistory);
        navigationView = findViewById(R.id.navigationViewDriverHistory);
        progressBar = findViewById(R.id.progressbar_driver_history);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        executorService = Executors.newSingleThreadExecutor();
        userRidesFromHistory = new ArrayList<>();

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        })
        ;

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.driverMainActivity:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverRideHistoryActivity.this, DriverMainActivity.class));
                        break;
                    case R.id.driverAccount:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverRideHistoryActivity.this, DriverAccountActivity.class));

                        break;
                    case R.id.driverInbox:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverRideHistoryActivity.this, DriverInboxActivity.class));

                        break;
                }
                return true;
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        loadFirstPage();

        driverRidesHistoryAdapter = new DriverRidesHistoryAdapter(this, userRidesFromHistory, sharedPreferences.getLong("id", -1));
        rideHistoryRecyclerView = findViewById(R.id.driver_rides_history_list);
        rideHistoryRecyclerView.setAdapter(driverRidesHistoryAdapter);
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

    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    private void loadFirstPage() {
        executorService.execute(() -> {
            loadUserHistoryItems();
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                if (currentPage < total_pages_ride) {
                    driverRidesHistoryAdapter.addLoadingFooter();
                } else {
                    isLastPage = true;
                }
            });
        });
    }

    private void loadNextPage() {
        executorService.execute(() -> {
            loadUserHistoryItems();
            runOnUiThread(() -> {
                driverRidesHistoryAdapter.removeLoadingFooter();
                isLoading = false;
                if (currentPage < total_pages_ride) {
                    driverRidesHistoryAdapter.addLoadingFooter();
                } else {
                    isLastPage = true;
                }
            });
        });
    }

    private void loadUserHistoryItems() {
        Long userId = sharedPreferences.getLong("id", -1);
        Call<AllHistoryItemsDTO> call = ServiceUtils.driverEndpoints.getAllUserHistoryItems(userId, PAGE_SIZE, currentPage, sort);
        AllHistoryItemsDTO allHistoryItems;
        try {
            allHistoryItems = call.execute().body();
            total_pages_ride = allHistoryItems.getRidePageCount();
            int sizeBefore = userRidesFromHistory.size();
            userRidesFromHistory.addAll(allHistoryItems.getHistoryItems());
            int sizeAfter = userRidesFromHistory.size();
            runOnUiThread(() -> rideHistoryRecyclerView.getAdapter().notifyItemRangeInserted(sizeBefore, sizeAfter - sizeBefore));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float accelationSquareRoot = (x * x + y * y + z * z)
                    / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
            long actualTime = event.timestamp;
            if (accelationSquareRoot >= 4) //
            {
                if (actualTime - lastUpdate < 200) {
                    return;
                }
                lastUpdate = actualTime;

                if (accelationSquareRoot > SHAKE_THRESHOLD) {
                    Toast.makeText(this, "Sorting by date", Toast.LENGTH_SHORT).show();

                    currentPage = PAGE_START;
                    userRidesFromHistory.clear();
                    rideHistoryRecyclerView.getAdapter().notifyDataSetChanged();
                    if (Objects.equals(sort, "startTime-asc")) {
                        sort = "startTime-desc";
                    } else
                        sort = "startTime-asc";
                    loadFirstPage();
                }

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);

        }
    }


}