package com.cruisemobile.cruise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cruisemobile.cruise.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPaymentFragment extends Fragment {


    public AddPaymentFragment() {
        // Required empty public constructor
    }


    public static AddPaymentFragment newInstance() {
        return new AddPaymentFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_payment, container, false);
    }
}