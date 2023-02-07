package com.cruisemobile.cruise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.adapters.FavouriteRideAdapter;
import com.cruisemobile.cruise.models.FavouriteRideDTO;
import com.cruisemobile.cruise.services.ServiceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class PassengerFavouriteRidesFragment extends Fragment {
    private RecyclerView favouriteRidesRecyclerView;
    private FavouriteRideAdapter favouriteRideAdapter;
    private ArrayList<FavouriteRideDTO> favouriteRides;
    private ExecutorService executorService;

    public PassengerFavouriteRidesFragment() {
        // Required empty public constructor
    }


    public static PassengerFavouriteRidesFragment newInstance() {
        return new PassengerFavouriteRidesFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executorService = Executors.newSingleThreadExecutor();
        favouriteRides = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_passenger_favourite_rides, container, false);
        favouriteRideAdapter = new FavouriteRideAdapter(getActivity(), favouriteRides);
        favouriteRidesRecyclerView = view.findViewById(R.id.passenger_fav_rides);
        favouriteRidesRecyclerView.setAdapter(favouriteRideAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        favouriteRidesRecyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    favouriteRides = getAllPassengerFavouriteRides();
                    if (favouriteRides != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                favouriteRideAdapter.setFavouriteRidesList(favouriteRides);
                            }
                        });

                    } else {
                        favouriteRides = new ArrayList<>();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

    }

    private ArrayList<FavouriteRideDTO> getAllPassengerFavouriteRides() throws IOException {
        Call<ArrayList<FavouriteRideDTO>> call = ServiceUtils.rideEndpoints.getAllPassengerFavouriteRides();
        return call.execute().body();
    }
}