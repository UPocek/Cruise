package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.services.ServiceUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordEmailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_email);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView email = findViewById(R.id.forget_password_email_2);
        Button button = findViewById(R.id.sendEmailForPassChangeBtn);
        button.setOnClickListener(view -> sendEmail(email.getText().toString()));
    }

    private void sendEmail(String email)
    {
        Call<Void> call = ServiceUtils.userEndpoints.sendResetPasswordMail(email);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Intent intent = new Intent(ForgetPasswordEmailActivity.this, ForgetPasswordActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ForgetPasswordEmailActivity.this, "Email can't be sent, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
