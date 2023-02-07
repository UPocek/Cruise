package com.cruisemobile.cruise.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.activities.EditAccountActivity;
import com.cruisemobile.cruise.tools.Helper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PersonalInformationFragmet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalInformationFragmet extends Fragment {
    // One Preview Image
    ImageView IVPreviewImage;
    private SharedPreferences sharedPreferences;
    private ImageView userImage;


    public PersonalInformationFragmet() {
        // Required empty public constructor
    }

    public static PersonalInformationFragmet newInstance() {
        return new PersonalInformationFragmet();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal_information_fragmet, container, false);
        View editButton = view.findViewById(R.id.editAccount);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getActivity(), EditAccountActivity.class));
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        userImage = view.findViewById(R.id.account_image_field);
        String profilePicture = sharedPreferences.getString("picture", "");
        if (!profilePicture.equals("")) {
            Bitmap bitmapPicture = Helper.stringToBitMap(profilePicture);
            userImage.setImageBitmap(bitmapPicture);
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        setUserData();
    }


    private void setUserData() {
        TextView nameField = getActivity().findViewById(R.id.account_first_name_field);
        nameField.setText(sharedPreferences.getString("name", ""));

        TextView surnameField = getActivity().findViewById(R.id.account_last_name_field);
        surnameField.setText(sharedPreferences.getString("surname", ""));

        TextView numberField = getActivity().findViewById(R.id.account_number_field);
        numberField.setText(sharedPreferences.getString("number", ""));

        TextView addressField = getActivity().findViewById(R.id.account_address_field);
        addressField.setText(sharedPreferences.getString("address", ""));

        TextView emailField = getActivity().findViewById(R.id.account_email_field);
        emailField.setText(sharedPreferences.getString("email", ""));
    }
}