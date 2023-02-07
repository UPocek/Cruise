package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.google.android.material.card.MaterialCardView;

public class EditPassengerAccountActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE =
            "com.example.android.twoactivities.extra.MESSAGE";
    private MaterialCardView passengerNameCard;
    private MaterialCardView passengerSurNameCard;
    private MaterialCardView passengerPhoneCard;
    private MaterialCardView passengerEmailCard;
    private MaterialCardView passengerAddressCard;
    private MaterialCardView passengerPasswordCard;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_passenger_account);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        passengerNameCard = findViewById(R.id.firstNamePerson);
        passengerSurNameCard = findViewById(R.id.lastNamePerson);
        passengerPhoneCard = findViewById(R.id.phonePerson);
        passengerEmailCard = findViewById(R.id.emailPerson);
        passengerAddressCard = findViewById(R.id.addressPerson);
        passengerPasswordCard = findViewById(R.id.passwordPerson);
        View backBtn = findViewById(R.id.backBtnPassengerEditAccount);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        passengerNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPassengerAccountActivity.this, ChangePassengerAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Name");
                startActivity(intent);

            }
        });
        passengerSurNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPassengerAccountActivity.this, ChangePassengerAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Surname");
                startActivity(intent);
            }
        });
        passengerPhoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPassengerAccountActivity.this, ChangePassengerAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Phone");
                startActivity(intent);
            }
        });
        passengerEmailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPassengerAccountActivity.this, ChangePassengerAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Email");
                startActivity(intent);
            }
        });
        passengerAddressCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPassengerAccountActivity.this, ChangePassengerAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Address");
                startActivity(intent);
            }
        });
        passengerPasswordCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditPassengerAccountActivity.this, ChangePassengerAccountActivity.class);
                intent.putExtra(EXTRA_MESSAGE, "Password");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserData();
    }

    private void setUserData() {
        TextView nameField = findViewById(R.id.user_detail_name_field);
        nameField.setText(sharedPreferences.getString("name", ""));

        TextView surnameField = findViewById(R.id.user_detail_surname_field);
        surnameField.setText(sharedPreferences.getString("surname", ""));

        TextView numberField = findViewById(R.id.user_detail_number_field);
        numberField.setText(sharedPreferences.getString("number", ""));

        TextView addressField = findViewById(R.id.user_detail_address_field);
        addressField.setText(sharedPreferences.getString("address", ""));

        TextView emailField = findViewById(R.id.user_detail_email_field);
        emailField.setText(sharedPreferences.getString("email", ""));
    }
}