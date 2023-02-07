package com.cruisemobile.cruise.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.adapters.SplitFareMailsAdapter;
import com.cruisemobile.cruise.models.RideForTransferDTO;
import com.cruisemobile.cruise.services.ServiceUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverRideHistoryInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverRideHistoryInfoFragment extends Fragment {

    private Long ride_id;
    private RideForTransferDTO ride;
    private MapFragment mMapFragment;


    public DriverRideHistoryInfoFragment() {
        // Required empty public constructor
    }


    public static DriverRideHistoryInfoFragment newInstance() {
        return new DriverRideHistoryInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_ride_history_info, container, false);

        Intent intent = getActivity().getIntent();
        ride_id = intent.getLongExtra("ride", 0);
        loadRideData(view);

        mMapFragment = MapFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.driver_ride_details_map, mMapFragment).commit();

        return view;
    }

    private void loadRideData(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Call<RideForTransferDTO> call = ServiceUtils.rideEndpoints.getRideDetails(ride_id);
                call.enqueue(new Callback<RideForTransferDTO>() {
                    @Override
                    public void onResponse(Call<RideForTransferDTO> call, Response<RideForTransferDTO> response) {
                        ride = response.body();
                        fillInfo(ride, view);
                        SplitFareMailsAdapter adapter = new SplitFareMailsAdapter(getActivity(), ride);
                        ListView emailList = view.findViewById(R.id.driver_ride_history_cruise_splitfare);
                        emailList.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<RideForTransferDTO> call, Throwable t) {
                        Log.e("RIDE HISTORY DETAILS", "Ride details could not be loaded " + t.getMessage());
                    }
                });

            }
        });
    }

    private void fillInfo(RideForTransferDTO ride, View view) {
        TextView time = view.findViewById(R.id.driver_ride_history_details_time);
        time.setText(ride.getStartTime().replace("T", " ").split("\\.")[0].concat(" - ").concat(ride.getEndTime().replace("T", " ").split("\\.")[0]));

        TextView location = view.findViewById(R.id.driver_ride_history_details_location);
        location.setText(ride.getLocations().get(0).getDeparture().getAddress().concat(" to ")
                .concat(ride.getLocations().get(0).getDestination().getAddress()));

        TextView splitfare = view.findViewById(R.id.driver_ride_history_details_number_of_passengers);
        splitfare.setText(String.format("%s", ride.getPassengers().size()));

        TextView duration = view.findViewById(R.id.driver_ride_history_details_duration);
        duration.setText(String.valueOf(ride.getEstimatedTimeInMinutes()));

        TextView distance = view.findViewById(R.id.driver_ride_history_details_distance);
        distance.setText(String.valueOf(ride.getDistance() / 1000).concat("km"));

        TextView price = view.findViewById(R.id.driver_ride_history_details_price);
        price.setText(String.valueOf(ride.getTotalCost()).concat("RSD"));

        mMapFragment.requestDirection(new LatLng(ride.getLocations().get(0).getDeparture().getLatitude(), ride.getLocations().get(0).getDeparture().getLongitude()), new LatLng(ride.getLocations().get(0).getDestination().getLatitude(), ride.getLocations().get(0).getDestination().getLongitude()));
    }
}