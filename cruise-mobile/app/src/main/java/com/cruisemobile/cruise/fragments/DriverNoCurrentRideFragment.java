package com.cruisemobile.cruise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cruisemobile.cruise.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverNoCurrentRideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverNoCurrentRideFragment extends Fragment {

    public DriverNoCurrentRideFragment() {
        // Required empty public constructor
    }


    public static DriverNoCurrentRideFragment newInstance() {
        return new DriverNoCurrentRideFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_driver_no_current_ride, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}