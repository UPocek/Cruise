package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cruisemobile.cruise.R;

public class CruiseDeclinedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cruise_declined);

        findViewById(R.id.back_button_declined).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CruiseDeclinedActivity.this, PassangerMainActivity.class));
                finish();
            }
        });

        findViewById(R.id.back_text_declined).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CruiseDeclinedActivity.this, PassangerMainActivity.class));
                finish();
            }
        });
    }
}