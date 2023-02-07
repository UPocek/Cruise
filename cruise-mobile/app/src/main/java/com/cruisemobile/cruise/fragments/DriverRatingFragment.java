package com.cruisemobile.cruise.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RatingBar;

import androidx.fragment.app.Fragment;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.adapters.HistoryCruiseCommentAdapter;
import com.cruisemobile.cruise.models.ReviewPairDTO;
import com.cruisemobile.cruise.services.ServiceUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DriverRatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverRatingFragment extends Fragment {

    Long ride_id;
    List<ReviewPairDTO> reviews;

    public DriverRatingFragment() {
        // Required empty public constructor
    }


    public static DriverRatingFragment newInstance() {
        return new DriverRatingFragment();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void loadRideData(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            Call<List<ReviewPairDTO>> call = ServiceUtils.reviewEndpoints.getAllRideReviews(ride_id);
            call.enqueue(new Callback<List<ReviewPairDTO>>() {
                @Override
                public void onResponse(Call<List<ReviewPairDTO>> call, Response<List<ReviewPairDTO>> response) {
                    reviews = response.body();
                    fillInfo(view);
                    HistoryCruiseCommentAdapter adapter1 = new HistoryCruiseCommentAdapter(getActivity(), reviews);
                    ListView commentsList = view.findViewById(R.id.driver_ride_history_cruise_comments);
                    commentsList.setAdapter(adapter1);
                }

                @Override
                public void onFailure(Call<List<ReviewPairDTO>> call, Throwable t) {
                    Log.e("RIDE HISTORY DETAILS", "Ride details could not be loaded " + t.getMessage());
                }
            });

        });
    }

    private void fillInfo(View view) {
        RatingBar driverRatingBar = view.findViewById(R.id.driver_ride_history_cruise_driver_ratingbar);
        RatingBar vehicleRatingBar = view.findViewById(R.id.driver_ride_history_cruise_vehicle_ratingbar);
        int driverRatingsNum = 0;
        double driverRatings = 0;
        int vehicleRatingsNum = 0;
        double vehicleRatings = 0;
        for (ReviewPairDTO review : reviews) {
            if (review.getDriverReview() != null) {
                driverRatings += review.getDriverReview().getRating();
                driverRatingsNum++;
            }
            if (review.getVehicleReview() != null) {
                vehicleRatings += review.getVehicleReview().getRating();
                vehicleRatingsNum++;
            }
        }
        driverRatingBar.setRating((float) driverRatings / driverRatingsNum);
        vehicleRatingBar.setRating((float) vehicleRatings / vehicleRatingsNum);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_rating, container, false);
        Intent intent = getActivity().getIntent();
        ride_id = intent.getLongExtra("ride", 0);
        loadRideData(view);

        return view;
    }
}