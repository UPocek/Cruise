package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.InboxFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class DriverInboxActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private InboxFragment inboxFragment;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_inbox);
        topAppBar = findViewById(R.id.topAppBarDriverInbox);
        drawerLayout = findViewById(R.id.drawerLayoutDriverInbox);
        navigationView = findViewById(R.id.navigationViewDriverInbox);

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
                        startActivity(new Intent(DriverInboxActivity.this, DriverRideHistoryActivity.class));
                        break;
                    case R.id.driverAccount:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverInboxActivity.this, DriverAccountActivity.class));
                        break;
                    case R.id.driverMainActivity:
                        drawerLayout.closeDrawer(navigationView);
                        startActivity(new Intent(DriverInboxActivity.this, DriverMainActivity.class));

                        break;
                }
                return true;
            }
        });

        inboxFragment = InboxFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.driverInbox, inboxFragment);
        transaction.commit();
    }

}