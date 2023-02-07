package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.AddPaymentFragment;
import com.cruisemobile.cruise.fragments.ChangePasswordFragment;
import com.cruisemobile.cruise.fragments.ChangeUserFragment;
import com.cruisemobile.cruise.models.ChangePasswordDTO;
import com.cruisemobile.cruise.models.UserChangesDTO;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.ChangePassengerTransition;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassengerAccountActivity extends AppCompatActivity {
    Button updateBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passenger_account);
        updateBtn = findViewById(R.id.updateBtn);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        String message = intent.getStringExtra(PassengerAccountActivity.EXTRA_MESSAGE);
        if (message.equalsIgnoreCase("Payment")) {
            ChangePassengerTransition.to(AddPaymentFragment.newInstance(), this, false);
            updateBtn.setText("Add payment");
        } else if (message.equalsIgnoreCase("Password")) {
            ChangePassengerTransition.to(ChangePasswordFragment.newInstance(), this, false);
            updateBtn.setText("Update");
        } else {
            ChangePassengerTransition.to(ChangeUserFragment.newInstance(message), this, false);
            updateBtn.setText("Update");
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message.equalsIgnoreCase("Password")) {
                    changeUserPassword();
                } else {
                    UserChangesDTO user = getCurrentUser();
                    makeChanges(user, message);
                    if (sharedPreferences.getString("role", "").equalsIgnoreCase("ROLE_PASSENGER")) {
                        processResponseForPassengerChanges(user);
                    } else {
                        requestChangesForDriver(user);
                    }
                }
            }
        });

        View backButton = findViewById(R.id.backBtnChange);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private UserChangesDTO getCurrentUser() {
        UserChangesDTO user = new UserChangesDTO();
        user.setName(sharedPreferences.getString("name", ""));
        user.setSurname(sharedPreferences.getString("surname", ""));
        user.setAddress(sharedPreferences.getString("address", ""));
        user.setTelephoneNumber(sharedPreferences.getString("number", ""));
        user.setEmail(sharedPreferences.getString("email", ""));
        user.setProfilePicture(sharedPreferences.getString("picture", ""));
        return user;
    }

    private void makeChanges(UserChangesDTO user, String message) {
        String input = ((TextInputEditText) findViewById(R.id.hintLabel)).getText().toString();
        switch (message) {
            case "Name":
                user.setName(input);
                break;
            case "Surname":
                user.setSurname(input);
                break;
            case "Phone":
                user.setTelephoneNumber(input);
                break;
            case "Email":
                user.setEmail(input);
                break;
            case "Address":
                user.setAddress(input);
        }
    }

    private void processResponseForPassengerChanges(UserChangesDTO user) {
        Call<UserDTO> call = ServiceUtils.passengerEndpoints.updatePassenger(user, sharedPreferences.getLong("id", -1L));
        SharedPreferences.Editor sp_editor = sharedPreferences.edit();
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.code() == 200) {
                    UserDTO user = response.body();
                    sp_editor.putString("name", user.getName());
                    sp_editor.putString("surname", user.getSurname());
                    sp_editor.putString("number", user.getTelephoneNumber());
                    sp_editor.putString("address", user.getAddress());
                    sp_editor.putString("picture", user.getProfilePicture());
                    sp_editor.commit();
                    Log.d("PASSED", "radi izmena");
                    Toast.makeText(ChangePassengerAccountActivity.this, "Changes made", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Log.d("MEHH", "Meesage recieved: " + response.code() + " msg: " + response.errorBody());
                    Toast.makeText(ChangePassengerAccountActivity.this, "Changes couldn't be applied", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Log.d("FAILED", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void requestChangesForDriver(UserChangesDTO user) {
    }

    private void changeUserPassword() {
        String oldPassword = ((TextInputEditText) findViewById(R.id.old_password)).getText().toString();
        String newPassword = ((TextInputEditText) findViewById(R.id.new_password)).getText().toString();
        String newConfirmedPassword = ((TextInputEditText) findViewById(R.id.confirm_new_password)).getText().toString();
        if (!newPassword.equals(newConfirmedPassword)) {
            Toast.makeText(ChangePassengerAccountActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }
        ChangePasswordDTO changePassword = new ChangePasswordDTO();
        changePassword.setNew_password(newPassword);
        changePassword.setOld_password(oldPassword);
        Call<Void> call = ServiceUtils.userEndpoints.resetPassword(sharedPreferences.getLong("id", -1L), changePassword);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 200) {
                    Toast.makeText(ChangePassengerAccountActivity.this, "Password changed!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    Log.d("MEHH", "Meesage recieved: " + response.code() + " msg: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("FAILED", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}