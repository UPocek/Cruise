package com.cruisemobile.cruise.fragments.passenger;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.activities.ConfirmRideDetailsActivity;
import com.cruisemobile.cruise.activities.NewRideActivity;

public class PassengerQuickPickFragment extends Fragment implements SensorEventListener {
    private static final int PROXIMITY_THRESHOLD = 0;
    private SensorManager sensorManager;

    public static PassengerQuickPickFragment newInstance() {
        return new PassengerQuickPickFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_passenger_quick_pick, container, false);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        EditText pickUp = view.findViewById(R.id.pickup_field);
        EditText destination = view.findViewById(R.id.destination_field);

        view.findViewById(R.id.find_a_cruise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = pickUp.getText().toString();
                String to = destination.getText().toString();
                if (areInputsValid(from, to)) {
                    Intent intent = new Intent(getActivity(), ConfirmRideDetailsActivity.class);
                    intent.putExtra("from", from);
                    intent.putExtra("to", to);
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private boolean areInputsValid(String from, String to) {
        return !from.equals("") && !to.equals("");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float[] values = sensorEvent.values;
            float x = values[0];
            if (x == PROXIMITY_THRESHOLD) {
                startActivity(new Intent(getActivity(), NewRideActivity.class));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
