package com.cruisemobile.cruise.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.databinding.ActivityNewRideBinding;
import com.cruisemobile.cruise.fragments.NewRideFragment1;
import com.cruisemobile.cruise.fragments.NewRideFragment4;
import com.cruisemobile.cruise.fragments.NewRideFragment5;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.util.LinkedList;

public class NewRideActivity extends AppCompatActivity {

    public String from;
    public String to;
    public String vehicleType;
    public boolean babyTransport;
    public boolean petTransport;
    public String startTime;
    private ActivityNewRideBinding binding;
    private LinkedList<Fragment> progressStack;
    private int currentFragmentDisplayedIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNewRideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        currentFragmentDisplayedIndex = 0;
        progressStack = new LinkedList<>();
        progressStack.add(new NewRideFragment1());
//        progressStack.add(new NewRideFragment2());
//        progressStack.add(new NewRideFragment3());
        progressStack.add(new NewRideFragment4());
        progressStack.add(new NewRideFragment5());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.book_a_ride_container, NewRideFragment1.class, null)
                    .commit();
        }

        View nextButtonBooking = findViewById(R.id.next_button_booking);
        nextButtonBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragmentDisplayedIndex < progressStack.size() - 1 && inputsValid(currentFragmentDisplayedIndex)) {
                    currentFragmentDisplayedIndex++;
                    findViewById(R.id.back_button_booking).setVisibility(View.VISIBLE);
                    Fragment goToFragment = progressStack.get(currentFragmentDisplayedIndex);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.book_a_ride_container, goToFragment, null)
                            .commit();
                } else if (currentFragmentDisplayedIndex == progressStack.size() - 1 && inputsValid(currentFragmentDisplayedIndex)) {
                    Intent intent = new Intent(NewRideActivity.this, CruiseConfirmedActivity.class);
                    intent.putExtra("from", from);
                    intent.putExtra("to", to);
                    intent.putExtra("vehicleType", vehicleType);
                    intent.putExtra("startTime", startTime);
                    intent.putExtra("petTransport", petTransport);
                    intent.putExtra("babyTransport", babyTransport);
                    startActivity(intent);
                    finish();
                }
            }
        });

        findViewById(R.id.back_button_booking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragmentDisplayedIndex != 0) {
                    currentFragmentDisplayedIndex--;
                    Fragment goToFragment = progressStack.get(currentFragmentDisplayedIndex);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.book_a_ride_container, goToFragment, null)
                            .commit();
                }
                if (currentFragmentDisplayedIndex == 0) {
                    findViewById(R.id.back_button_booking).setVisibility(View.INVISIBLE);
                }
            }
        });

        findViewById(R.id.cancel_booking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewRideActivity.this, PassangerMainActivity.class));
                finish();
            }
        });
    }

    public void processTimePickerResult(int hour, int minutes) {
        String hour_string = Integer.toString(hour);
        String minutes_string = Integer.toString(minutes);
        LocalDateTime rideTime = LocalDateTime.now().withHour(hour).withMinute(minutes);
        startTime = rideTime.toString();

        ((TextView) findViewById(R.id.time_picker_form_booking)).setText(String.format("Time: %s:%s", hour_string, minutes_string));
    }

    private boolean inputsValid(int currentFragmentIndex) {
        switch (currentFragmentIndex) {

            case 0:
                String fromInput = ((TextInputEditText) findViewById(R.id.pickup_field)).getText().toString();
                String toInput = ((TextInputEditText) findViewById(R.id.destination_field)).getText().toString();
                if (fromInput.equals("") || toInput.equals("")) {
                    return false;
                }
                from = fromInput;
                to = toInput;
                break;
            case 1:
                if(startTime == null){
                    startTime = LocalDateTime.now().toString();
                }
                petTransport = ((CheckBox) findViewById(R.id.pet_transfer)).isChecked();
                babyTransport = ((CheckBox) findViewById(R.id.baby_transfer)).isChecked();
                vehicleType = ((Spinner) findViewById(R.id.car_types_booking)).getSelectedItem().toString();
                break;
        }
        return true;
    }
}