package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.cruisemobile.cruise.databinding.ActivityRegistrationFinishBinding;

public class RegistrationFinishActivity extends AppCompatActivity {

    private ActivityRegistrationFinishBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationFinishBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        binding.finishRegistrationButton.setOnClickListener(view -> {
            Intent intent = new Intent(RegistrationFinishActivity.this, UserLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}