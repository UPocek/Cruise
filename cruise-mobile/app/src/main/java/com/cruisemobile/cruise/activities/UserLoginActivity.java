package com.cruisemobile.cruise.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.CredentialsDTO;
import com.cruisemobile.cruise.models.LoginDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserLoginActivity extends AppCompatActivity {

    public static String jwtToken;
    private CredentialsDTO credentialsDTO;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        credentialsDTO = new CredentialsDTO();
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(v -> {
            logInUser();
        });
        Button googleLoginBtn = findViewById(R.id.googleLoginBtn);
        googleLoginBtn.setOnClickListener(v -> {
        });

        TextView registerLink = findViewById(R.id.register_cruise);
        TextView forgotPassword = findViewById(R.id.forgot_password);
        registerLink.setOnClickListener(v -> startActivity(new Intent(UserLoginActivity.this, UserRegisterActivity.class)));
        forgotPassword.setOnClickListener(v -> startActivity(new Intent(UserLoginActivity.this, ForgetPasswordEmailActivity.class)));
    }

    private void logInUser() {
        String emailInput = ((TextInputEditText) findViewById(R.id.loginEmail)).getText().toString();
        if (emailInput.equals("")) {
            return;
        }
        credentialsDTO.setEmail(emailInput);

        String passwordInput = ((TextInputEditText) findViewById(R.id.loginPassword)).getText().toString();
        if (passwordInput.equals("")) {
            return;
        }
        credentialsDTO.setPassword(passwordInput);

        Call<LoginDTO> call = ServiceUtils.userEndpoints.login(credentialsDTO);
        call.enqueue(new Callback<LoginDTO>() {
            @Override
            public void onResponse(Call<LoginDTO> call, Response<LoginDTO> response) {
                if (response.code() == 200) {
                    Log.d("PASSED", "radi");
                    redirect(response.body().getAccessToken());
                } else {
                    Log.d("MEHH", "Meesage recieved: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginDTO> call, Throwable t) {
                Log.d("FAILED", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    private void redirect(String jwt) {
        String[] chunks = jwt.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        Log.e("jwt", payload);
        String email = payload.split(",")[0].split(":")[1].replace("\"", "");
        String role = payload.split(",")[1].split(":")[1].replace("\"", "");
        String id = payload.split(",")[2].split(":")[1].replace("\"", "");
        Log.e("email", email);
        Log.e("role", role);
        Log.e("id", id);
        setPreferences(jwt, email, role, Long.parseLong(id));
        if (role.equals("ROLE_DRIVER")) {
            startActivity(new Intent(UserLoginActivity.this, DriverMainActivity.class));
        } else if (role.equals("ROLE_PASSENGER")) {
            startActivity(new Intent(UserLoginActivity.this, PassangerMainActivity.class));
        }
        finish();

    }

    private void setPreferences(String jwt, String email, String role, Long id) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor sp_editor = sharedPreferences.edit();
        sp_editor.putString("jwt", jwt);
        jwtToken = jwt;
        sp_editor.putString("email", email);
        sp_editor.putString("role", role);
        sp_editor.putLong("id", id);
        sp_editor.apply();
    }
}