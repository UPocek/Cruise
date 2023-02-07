package com.cruisemobile.cruise.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.DriverRatingFragment;
import com.cruisemobile.cruise.fragments.DriverRideHistoryInfoFragment;
import com.cruisemobile.cruise.tools.DriverHistoryTransition;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DriverHistoryRideDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_history_ride_details);

        FloatingActionButton backButton = findViewById(R.id.driver_ride_history_back_fab);
        backButton.setOnClickListener(v -> onBackPressed());
        DriverHistoryTransition.to(DriverRideHistoryInfoFragment.newInstance(), this, false);

        Button detailsButton = findViewById(R.id.details_btn);
        Button ratingButton = findViewById(R.id.rating_btn);
        ratingButton.setOnClickListener(v -> {
            DriverHistoryTransition.to(DriverRatingFragment.newInstance(), DriverHistoryRideDetailsActivity.this, false);
            v.setVisibility(View.INVISIBLE);
            detailsButton.setVisibility(View.VISIBLE);
        });
        detailsButton.setOnClickListener(v -> {
            DriverHistoryTransition.to(DriverRideHistoryInfoFragment.newInstance(), DriverHistoryRideDetailsActivity.this, false);
            v.setVisibility(View.INVISIBLE);
            ratingButton.setVisibility(View.VISIBLE);
        });

    }

}