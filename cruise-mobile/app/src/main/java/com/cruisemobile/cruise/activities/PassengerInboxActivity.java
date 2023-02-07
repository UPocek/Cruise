package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.InboxFragment;
import com.cruisemobile.cruise.models.RideForUserDTO;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class PassengerInboxActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private InboxFragment inboxFragment;
    private ArrayList<RideForUserDTO> userRides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_inbox);
        topAppBar = findViewById(R.id.topAppBarPassengerInbox);
        drawerLayout = findViewById(R.id.drawerLayoutPassengerInbox);
        navigationView = findViewById(R.id.navigationViewPassengerInbox);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userRides = new ArrayList<>();

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.open();
            }
        });

        inboxFragment = InboxFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.passengerInbox, inboxFragment);
        transaction.commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.passengerRideHistory:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassengerInboxActivity.this, PassengerRideHistoryActivity.class));
                        break;
                    case R.id.passengerAccount:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassengerInboxActivity.this, PassengerAccountActivity.class));

                        break;
                    case R.id.passengerMainActivity:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassengerInboxActivity.this, PassangerMainActivity.class));

                        break;
                }
                return true;
            }
        });
    }
}