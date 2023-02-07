package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.passenger.PassengerCurrentRideCheckFragment;
import com.cruisemobile.cruise.fragments.passenger.PassengerQuickPickFragment;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.Helper;
import com.cruisemobile.cruise.tools.PassengerFragmentTransition;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class PassangerMainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private MaterialToolbar topAppBar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passanger_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        topAppBar = findViewById(R.id.topAppBarPassengerMain);
        drawerLayout = findViewById(R.id.drawerLayoutPassengerMain);
        navigationView = findViewById(R.id.navigationViewPassengerMain);
        bottomNavigationView = findViewById(R.id.bottom_navigationPassengerMain);
        executorService = Executors.newSingleThreadExecutor();
        PassengerFragmentTransition.to(PassengerQuickPickFragment.newInstance(), this, false);

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
                    case R.id.passengerRideHistory:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassangerMainActivity.this, PassengerRideHistoryActivity.class));
                        break;
                    case R.id.passengerAccount:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassangerMainActivity.this, PassengerAccountActivity.class));
                        break;
                    case R.id.passengerInbox:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(PassangerMainActivity.this, PassengerInboxActivity.class));
                        break;
                }
                return true;
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new PassengerBottomNavigationListener());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sharedPreferences.contains("name")) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        UserDTO user = getUserData();
                        Helper.setUserDataInSharedPreferences(user, sharedPreferences);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }


    }

    private UserDTO getUserData() throws IOException {
        Call<UserDTO> call = ServiceUtils.passengerEndpoints.getActivePassengerDetails(sharedPreferences.getString("email", ""));
        return call.execute().body();
    }

    private void selectItemFromBottomNavigationBar(MenuItem item) {
        if (item.getTitle().equals("Quick pick"))
            PassengerFragmentTransition.to(PassengerQuickPickFragment.newInstance(), this, false);
        else if (item.getTitle().equals("Current ride"))
            PassengerFragmentTransition.to(PassengerCurrentRideCheckFragment.newInstance(), this, false);
        else if (item.getTitle().equals("New ride"))
            startActivity(new Intent(PassangerMainActivity.this, NewRideActivity.class));
    }

    public class PassengerBottomNavigationListener implements BottomNavigationView.OnItemSelectedListener {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            selectItemFromBottomNavigationBar(item);
            return true;
        }
    }
}