package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.databinding.ActivityUserRegisterBinding;
import com.cruisemobile.cruise.fragments.RegistrationFragment1;
import com.cruisemobile.cruise.fragments.RegistrationFragment2;
import com.cruisemobile.cruise.fragments.RegistrationFragment3;
import com.cruisemobile.cruise.fragments.RegistrationFragment4;
import com.cruisemobile.cruise.fragments.RegistrationFragment5;
import com.cruisemobile.cruise.models.UserDTO;
import com.cruisemobile.cruise.models.UserRegistrationDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.LinkedList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegisterActivity extends AppCompatActivity {

    public UserRegistrationDTO userForRegistration;
    private ActivityUserRegisterBinding binding;
    private LinkedList<Fragment> progressStack;
    private int currentFragmentDisplayedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentFragmentDisplayedIndex = 0;
        progressStack = new LinkedList<>();
        progressStack.add(new RegistrationFragment1());
        progressStack.add(new RegistrationFragment2());
        progressStack.add(new RegistrationFragment3());
        progressStack.add(new RegistrationFragment4());
        progressStack.add(new RegistrationFragment5());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.registration_form_container, RegistrationFragment1.class, null)
                    .commit();
        }

        findViewById(R.id.next_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragmentDisplayedIndex < progressStack.size() - 1 && inputsValid(currentFragmentDisplayedIndex)) {
                    currentFragmentDisplayedIndex++;
                    findViewById(R.id.back_button_registration).setVisibility(View.VISIBLE);
                    Fragment goToFragment = progressStack.get(currentFragmentDisplayedIndex);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.registration_form_container, goToFragment, null)
                            .commit();
                } else if (inputsValid(currentFragmentDisplayedIndex)) {
                    registerUser();
                }
            }
        });

        findViewById(R.id.back_button_registration).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragmentDisplayedIndex != 0) {
                    currentFragmentDisplayedIndex--;
                    Fragment goToFragment = progressStack.get(currentFragmentDisplayedIndex);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.registration_form_container, goToFragment, null)
                            .commit();
                }
                if (currentFragmentDisplayedIndex == 0) {
                    findViewById(R.id.back_button_registration).setVisibility(View.INVISIBLE);
                }
            }
        });

        userForRegistration = new UserRegistrationDTO();

    }

    private boolean inputsValid(int currentFragmentIndex) {
        switch (currentFragmentIndex) {

            case 0:
                String emailInput = ((TextInputEditText) findViewById(R.id.registration_email_field)).getText().toString();
                if (emailInput.equals("")) {
                    return false;
                }
                userForRegistration.setEmail(emailInput);
                break;
            case 1:
                String nameInput = ((TextInputEditText) findViewById(R.id.registration_name_field)).getText().toString();
                String surnameInput = ((TextInputEditText) findViewById(R.id.registration_surname_field)).getText().toString();
                if (nameInput.equals("") || surnameInput.equals("")) {
                    return false;
                }
                userForRegistration.setName(nameInput);
                userForRegistration.setSurname(surnameInput);
                break;
            case 2:
                String phoneInput = ((TextInputEditText) findViewById(R.id.registration_phone_field)).getText().toString();
                if (phoneInput.equals("")) {
                    return false;
                }
                userForRegistration.setTelephoneNumber(phoneInput);
                break;
            case 3:
                String addressInput = ((TextInputEditText) findViewById(R.id.registration_address_field)).getText().toString();
                if (addressInput.equals("")) {
                    return false;
                }
                userForRegistration.setAddress(addressInput);
                break;
            case 4:
                String passwordInput = ((TextInputEditText) findViewById(R.id.registration_password_field)).getText().toString();
                String repasswordInput = ((TextInputEditText) findViewById(R.id.registration_repassword_field)).getText().toString();
                if (passwordInput.equals("") || repasswordInput.equals("") || (!passwordInput.equals(repasswordInput))) {
                    return false;
                }
                userForRegistration.setPassword(passwordInput);
                break;
        }
        return true;
    }

    private void registerUser() {
        userForRegistration.setProfilePicture("");
        Call<UserDTO> call = ServiceUtils.passengerEndpoints.register(userForRegistration);
        call.enqueue(new Callback<UserDTO>() {
            @Override
            public void onResponse(Call<UserDTO> call, Response<UserDTO> response) {
                if (response.code() == 200) {
                    Log.d("PASSED", "Meesage recieved");
                    Intent intent = new Intent(UserRegisterActivity.this, RegistrationFinishActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UserRegisterActivity.this, "Some of credentials are not valid. Change and try again. " + response.message(), Toast.LENGTH_LONG).show();
                    Log.d("MEHH", "Meesage recieved: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<UserDTO> call, Throwable t) {
                Toast.makeText(UserRegisterActivity.this, "Some of credentials are not valid. Change and try again. " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("FAILED", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }
}