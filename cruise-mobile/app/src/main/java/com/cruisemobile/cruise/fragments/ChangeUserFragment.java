package com.cruisemobile.cruise.fragments;

import static android.text.InputType.TYPE_CLASS_PHONE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cruisemobile.cruise.R;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeUserFragment extends Fragment {
    private String hintLabel;

    public ChangeUserFragment(String label) {
        this.hintLabel = label;
        // Required empty public constructor
    }


    public static ChangeUserFragment newInstance(String label) {
        return new ChangeUserFragment(label);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_user, container, false);
        TextInputEditText inputField = view.findViewById(R.id.hintLabel);
        inputField.setHint(this.hintLabel);
        if (hintLabel.equalsIgnoreCase("Phone"))
            inputField.setInputType(TYPE_CLASS_PHONE);
        return view;
    }
}