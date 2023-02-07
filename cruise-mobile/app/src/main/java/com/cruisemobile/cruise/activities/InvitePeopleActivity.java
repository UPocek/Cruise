package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cruisemobile.cruise.R;

public class InvitePeopleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_people);

        findViewById(R.id.next_after_invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InvitePeopleActivity.this, CruiseConfirmedActivity.class));
                finish();
            }
        });

        findViewById(R.id.invite_go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InvitePeopleActivity.this, PassangerMainActivity.class));
                finish();
            }
        });
    }
}