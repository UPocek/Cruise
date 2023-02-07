package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.ResetPasswordDTO;
import com.cruisemobile.cruise.services.ServiceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordChangeActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String code = getIntent().getStringExtra("code");
        String email = getIntent().getStringExtra("email");
        TextView pass = findViewById(R.id.reset_password_password_1);
        TextView pass2 = findViewById(R.id.reset_password_password_repeat_1);
        Button button = findViewById(R.id.resetPasswordBtn);
        button.setOnClickListener(view -> changePassword(pass.getText().toString(), pass2.getText().toString(), code, email));
    }

    private void changePassword(String pass, String pass2, String code, String email)
    {
        if(pass.equals(pass2))
        {
            ResetPasswordDTO dto = new ResetPasswordDTO(pass, code);
            Call<Void> call = ServiceUtils.userEndpoints.resetPassword(email, dto);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    startActivity(new Intent(getApplicationContext(), UserLoginActivity.class));
                    finish();
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {

                    Toast.makeText(getApplicationContext(), "Something went wrong, please try again later!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
    }
}
