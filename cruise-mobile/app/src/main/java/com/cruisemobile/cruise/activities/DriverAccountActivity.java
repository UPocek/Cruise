package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.PersonalInformationFragmet;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.AccountDetailTransition;
import com.cruisemobile.cruise.tools.Helper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class DriverAccountActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE =
            "com.example.android.twoactivities.extra.MESSAGE";
    private SharedPreferences sharedPreferences;
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_account);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        topAppBar = findViewById(R.id.topAppBarDriverAccount);
        drawerLayout = findViewById(R.id.drawerLayoutDriverAccount);
        navigationView = findViewById(R.id.navigationViewDriverAccount);

        Button favouriteRidesBtn = findViewById(R.id.driverStatsButton);
        Button reportsBtn = findViewById(R.id.driverReportsBtn);
        Button logOutButton = findViewById(R.id.passenger_log_out_button);

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
                    case R.id.driverRideHistory:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverAccountActivity.this, DriverRideHistoryActivity.class));
                        break;
                    case R.id.driverMainActivity:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverAccountActivity.this, DriverMainActivity.class));

                        break;
                    case R.id.driverInbox:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverAccountActivity.this, DriverInboxActivity.class));
                        break;

                }
                return true;
            }
        });

        favouriteRidesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverAccountActivity.this, PassengerAccountDetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Reports");
                startActivity(intent);
            }
        });

        reportsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverAccountActivity.this, PassengerAccountDetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Reports");
                startActivity(intent);
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        deactivateDriver();
                        Helper.clearSharedPreferences(sharedPreferences);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finishAffinity();
                                startActivity(new Intent(DriverAccountActivity.this, UserLoginActivity.class));
                            }
                        });
                    }
                });
            }
        });
        AccountDetailTransition.to(PersonalInformationFragmet.newInstance(), this, false);
    }

    private void deactivateDriver() {
        Call<Boolean> call = ServiceUtils.driverEndpoints.changeDriverActivity(sharedPreferences.getLong("id", 0L), false);
        try {
            call.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}