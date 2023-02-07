package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.PassengerFavouriteRidesFragment;
import com.cruisemobile.cruise.fragments.PersonalInformationFragmet;
import com.cruisemobile.cruise.fragments.ReportsFragment;
import com.cruisemobile.cruise.tools.AccountDetailTransition;

public class PassengerAccountDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        Intent intent = getIntent();
        String message = intent.getStringExtra(PassengerAccountActivity.EXTRA_MESSAGE);
        View backBtn = findViewById(R.id.backBtnDetail);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        switch (message) {
            case "PersonalInfo":
                AccountDetailTransition.to(PersonalInformationFragmet.newInstance(), this, false);
                break;
            case "FavouriteRides":
                AccountDetailTransition.to(PassengerFavouriteRidesFragment.newInstance(), this, false);
                break;
            case "Reports":
                AccountDetailTransition.to(ReportsFragment.newInstance(), this, false);
                break;
        }

    }
}