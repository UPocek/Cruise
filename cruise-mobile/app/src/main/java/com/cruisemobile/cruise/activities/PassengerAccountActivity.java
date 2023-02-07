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
import com.cruisemobile.cruise.tools.AccountDetailTransition;
import com.cruisemobile.cruise.tools.Helper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class PassengerAccountActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE =
            "com.example.android.twoactivities.extra.MESSAGE";
    private SharedPreferences sharedPreferences;
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_account);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        topAppBar = findViewById(R.id.topAppBarPassengerAccount);
        drawerLayout = findViewById(R.id.drawerLayoutPassengerAccount);
        navigationView = findViewById(R.id.navigationViewPassengerAccount);

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
                    case R.id.passengerRideHistory:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassengerAccountActivity.this, PassengerRideHistoryActivity.class));
                        break;
                    case R.id.passengerMainActivity:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassengerAccountActivity.this, PassangerMainActivity.class));

                        break;
                    case R.id.passengerInbox:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassengerAccountActivity.this, PassengerInboxActivity.class));
                        break;

                }
                return true;
            }
        });


        favouriteRidesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassengerAccountActivity.this, PassengerAccountDetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "FavouriteRides");
                startActivity(intent);
            }
        });

        reportsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassengerAccountActivity.this, PassengerAccountDetailActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Reports");
                startActivity(intent);
            }
        });
        AccountDetailTransition.to(PersonalInformationFragmet.newInstance(), this, false);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.clearSharedPreferences(sharedPreferences);
                Intent intent = new Intent(PassengerAccountActivity.this, UserLoginActivity.class);
                finishAffinity();
                startActivity(intent);
            }
        });
    }
}