package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.ChangePasswordFragment;
import com.cruisemobile.cruise.fragments.ChangeUserFragment;
import com.cruisemobile.cruise.models.ChangePasswordDTO;
import com.cruisemobile.cruise.models.UserChangesDTO;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.ChangePassengerTransition;
import com.cruisemobile.cruise.tools.Helper;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class ChangeAccountActivity extends AppCompatActivity {
    Button updateBtn;
    private SharedPreferences sharedPreferences;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passenger_account);
        executorService = Executors.newSingleThreadExecutor();
        updateBtn = findViewById(R.id.updateBtn);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent intent = getIntent();
        String message = intent.getStringExtra(PassengerAccountActivity.EXTRA_MESSAGE);
        if (message.equalsIgnoreCase("Password")) {
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
                    ChangePasswordDTO changePasswordDTO = validatePasswordChanges();
                    if (changePasswordDTO != null) {
                        processPasswordChanges(changePasswordDTO);
                    } else {
                        Toast.makeText(ChangeAccountActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    UserChangesDTO user = Helper.getUserInfoFromSharedPreferences(sharedPreferences);
                    changeUserInfoBasedOnNewInput(user, message);
                    makeUpdatesToUser(user);
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

    private void makeUpdatesToUser(UserChangesDTO user) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (sharedPreferences.getString("role", "").equalsIgnoreCase("ROLE_PASSENGER")) {
                        UserDTO updatedPassenger = applyPassengerChanges(user);
                        Helper.setUserDataInSharedPreferences(updatedPassenger, sharedPreferences);
                    } else {
                        requestChangesForDriver(user);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (sharedPreferences.getString("role", "").equalsIgnoreCase("ROLE_DRIVER")) {
                            Toast.makeText(ChangeAccountActivity.this, "Changes requested", Toast.LENGTH_LONG).show();
                        }
                        onBackPressed();
                    }
                });
            }
        });
    }

    private void processPasswordChanges(ChangePasswordDTO changePasswordDTO) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    requestPasswordChange(changePasswordDTO);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onBackPressed();
                    }
                });
            }
        });
    }

    private void changeUserInfoBasedOnNewInput(UserChangesDTO user, String message) {
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

    private UserDTO applyPassengerChanges(UserChangesDTO user) throws IOException {
        Call<UserDTO> call = ServiceUtils.passengerEndpoints.updatePassenger(user, sharedPreferences.getLong("id", -1L));
        return call.execute().body();
    }

    private void requestChangesForDriver(UserChangesDTO user) throws IOException {
        Call<UserDTO> call = ServiceUtils.driverEndpoints.requestDriverUpdate(user, sharedPreferences.getLong("id", -1L));
        call.execute();
    }

    private ChangePasswordDTO validatePasswordChanges() {
        String oldPassword = ((TextInputEditText) findViewById(R.id.old_password)).getText().toString();
        String newPassword = ((TextInputEditText) findViewById(R.id.new_password)).getText().toString();
        String newConfirmedPassword = ((TextInputEditText) findViewById(R.id.confirm_new_password)).getText().toString();
        if (!newPassword.equals(newConfirmedPassword)) {
            Toast.makeText(ChangeAccountActivity.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return null;
        }
        ChangePasswordDTO changePassword = new ChangePasswordDTO();
        changePassword.setNew_password(newPassword);
        changePassword.setOld_password(oldPassword);
        return changePassword;
    }

    private void requestPasswordChange(ChangePasswordDTO changePassword) throws IOException {
        Call<Void> call = ServiceUtils.userEndpoints.resetPassword(sharedPreferences.getLong("id", -1L), changePassword);
        call.execute();
    }
}