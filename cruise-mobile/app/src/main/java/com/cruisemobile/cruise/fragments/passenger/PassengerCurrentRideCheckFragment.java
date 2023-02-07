package com.cruisemobile.cruise.fragments.passenger;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.fragments.MapFragment;
import com.cruisemobile.cruise.models.RideDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.cruisemobile.cruise.tools.PassengerFragmentTransition;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerCurrentRideCheckFragment extends Fragment {


    private SharedPreferences sharedPreferences;
    private MapFragment mMapFragment;


    public static PassengerCurrentRideCheckFragment newInstance() {
        return new PassengerCurrentRideCheckFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        findRide();
    }

    private void findRide() {
        Call<RideDTO> call = ServiceUtils.rideEndpoints.getActiveRideForPassenger(sharedPreferences.getLong("id", 0L));
        call.enqueue(new Callback<RideDTO>() {
            @Override
            public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
                if (response.code() == 200) {
                    Log.e("PASSED", "ima ride");
                    PassengerFragmentTransition.to(PassengerCurrentRideFragment.newInstance(response.body()), getActivity(), false);

                } else {
                    Log.d("MEHH", "Meesage recieved: " + response.code() + " " + response.message());
                    Call<RideDTO> call2 = ServiceUtils.rideEndpoints.getAcceptedRideForPassenger(sharedPreferences.getLong("id", 0L));
                    call2.enqueue(new Callback<RideDTO>() {
                        @Override
                        public void onResponse(Call<RideDTO> call, Response<RideDTO> response) {
                            if (response.code() == 200) {
                                Log.e("PASSED", "ima ride");
                                PassengerFragmentTransition.to(PassengerCurrentRideFragment.newInstance(response.body()), getActivity(), false);
                            } else {
                                Log.d("MEHH", "Meesage recieved: " + response.code() + " " + response.message());
                                TextView textView = getView().findViewById(R.id.ride_check_text);
                                textView.setText(R.string.you_do_not_have_any_cruise_go_order_one_and_come_back);
                            }
                        }

                        @Override
                        public void onFailure(Call<RideDTO> call, Throwable t) {
                            Log.d("FAILED 2", t.getMessage() != null ? t.getMessage() : "error");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<RideDTO> call, Throwable t) {
                Log.d("FAILED 1", t.getMessage() != null ? t.getMessage() : "error");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_passenger_current_ride_check, container, false);
    }
}