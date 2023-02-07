package com.cruisemobile.cruise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.activities.NewRideActivity;
import com.cruisemobile.cruise.adapters.OfferAdapter;
import com.cruisemobile.cruise.models.LocationDTO;
import com.cruisemobile.cruise.models.OfferDTO;
import com.cruisemobile.cruise.tools.Helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewRideFragment5 extends Fragment {

    private RecyclerView mRecyclerView;
    private OfferAdapter offerAdapter;
    private List<OfferDTO> offersForRide;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_ride5, container, false);

        offersForRide = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.all_offers);
        offerAdapter = new OfferAdapter(getContext(), offersForRide);
        mRecyclerView.setAdapter(offerAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getOffersForRide();

        return view;
    }

    private void getOffersForRide() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                NewRideActivity activity = (NewRideActivity) getActivity();
                try {
                    LocationDTO fromLocation = Helper.getLocationFromAddress(activity.from);
                    LocationDTO toLocation = Helper.getLocationFromAddress(activity.to);
                    OfferDTO offer = Helper.getEstimationForRequest(fromLocation, toLocation, activity.vehicleType, activity.babyTransport, activity.petTransport);
                    final int itemsAtStart = offersForRide.size();
                    offersForRide.add(offer);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            offerAdapter.notifyItemInserted(itemsAtStart);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}