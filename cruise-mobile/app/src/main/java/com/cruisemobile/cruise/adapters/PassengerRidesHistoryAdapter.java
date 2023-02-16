package com.cruisemobile.cruise.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.activities.MessagesActivity;
import com.cruisemobile.cruise.activities.PassengerHistoryRideDetailsActivity;
import com.cruisemobile.cruise.activities.PassengerRideHistoryActivity;
import com.cruisemobile.cruise.models.FavouriteRideBasicDTO;
import com.cruisemobile.cruise.models.FavouriteRideDTO;
import com.cruisemobile.cruise.models.LocationPairDTO;
import com.cruisemobile.cruise.models.RideForUserDTO;
import com.cruisemobile.cruise.models.UserForRideDTO;
import com.cruisemobile.cruise.services.ServiceUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerRidesHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LOADING = 0;
    private static final int RIDE = 1;
    private ArrayList<RideForUserDTO> userRides;
    private final LayoutInflater mInflater;
    private final Long userId;
    private final ArrayList<FavouriteRideDTO> userFavouriteRides;
    private boolean isLoadingAdded = false;

    public PassengerRidesHistoryAdapter(Context context, ArrayList<RideForUserDTO> userRides, Long userId, ArrayList<FavouriteRideDTO> userFavouriteRides) {
        this.userRides = userRides;
        this.userId = userId;
        this.userFavouriteRides = userFavouriteRides;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == userRides.size() - 1 && isLoadingAdded) {
            return LOADING;
        }
        return RIDE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case RIDE:
                View viewItem = mInflater.inflate(R.layout.ride_history_item,
                        parent, false);
                viewHolder = new PassengerRidesHistoryAdapter.RideHistoryHolder(viewItem);
                break;
            case LOADING:
                View viewLoading = mInflater.inflate(R.layout.item_progress,
                        parent, false);
                viewHolder = new PassengerRidesHistoryAdapter.LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RideForUserDTO mCurrent = userRides.get(position);
        switch (getItemViewType(position)) {
            case RIDE:
                PassengerRidesHistoryAdapter.RideHistoryHolder rideViewHolder = (RideHistoryHolder) holder;
                rideViewHolder.rideLocations.setText(String.format("%s: %s - %s", mCurrent.getId().toString(), mCurrent.getLocations().get(0).getDeparture().getAddress(), mCurrent.getLocations().get(0).getDestination().getAddress()));
                rideViewHolder.rideInfo.setText(String.format("%s - %s rsd / %s passenger(s) / %s vehicle", mCurrent.getStatus(), mCurrent.getTotalCost(), mCurrent.getPassengers().size(), mCurrent.getVehicleType()));
                rideViewHolder.ride = mCurrent;
                rideViewHolder.setUpFavouriteStatus(isRideInFavourites(mCurrent));
                break;

            case LOADING:
                PassengerRidesHistoryAdapter.LoadingViewHolder loadingViewHolder = (PassengerRidesHistoryAdapter.LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    private Long isRideInFavourites(RideForUserDTO ride) {
        for (FavouriteRideDTO favouriteRide : userFavouriteRides) {
            if (!Objects.equals(ride.getVehicleType(), favouriteRide.getVehicleType()))
                continue;
            if (ride.isBabyTransport() != favouriteRide.isBabyTransport()) continue;
            if (ride.isPetTransport() != favouriteRide.isPetTransport()) continue;

            int numberOfSamePassengers = 0;

            for (UserForRideDTO passenger : ride.getPassengers()) {
                for (UserForRideDTO fpassenger : favouriteRide.getPassengers()) {
                    if (Objects.equals(passenger.getEmail(), fpassenger.getEmail())) {
                        numberOfSamePassengers++;
                        break;
                    }
                }
            }

            if (numberOfSamePassengers != ride.getPassengers().size()) {
                continue;
            }

            int numberOfSameLocations = 0;

            for (LocationPairDTO locationPair : ride.getLocations()) {
                for (LocationPairDTO flocationPair : favouriteRide.getLocations()) {
                    if (Objects.equals(locationPair.getDeparture().getAddress(), flocationPair.getDeparture().getAddress()) && Objects.equals(locationPair.getDestination().getAddress(), flocationPair.getDestination().getAddress())) {
                        numberOfSameLocations++;
                        break;
                    }
                }
            }

            if (numberOfSameLocations != ride.getLocations().size()) {
                continue;
            }

            return favouriteRide.getId();
        }
        return -1L;
    }

    @Override
    public int getItemCount() {
        return this.userRides.size();
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
    }

    public void setRidesList(ArrayList<RideForUserDTO> filterRideList, boolean sortOrderReverse) {
        userRides = filterRideList;
        sortOnShakeChatItems(sortOrderReverse);
    }

    public void sortOnShakeChatItems(boolean sortOrderReverse) {
        if (sortOrderReverse) {
            this.userRides.sort(new PassengerHistoryComparator().reversed());
        } else {
            this.userRides.sort(new PassengerHistoryComparator());
        }
        notifyDataSetChanged();
    }

    public void addNewFavourites(ArrayList<FavouriteRideDTO> newUserFavouriteRides) {
        userFavouriteRides.addAll(newUserFavouriteRides);
    }

//    INNER CLASS

    class RideHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView rideLocations;
        private final TextView rideInfo;
        private final ImageButton inboxButton;
        private final ImageButton favouriteButton;
        private Long otherId;
        private String otherFullName;
        private RideForUserDTO ride;
        private Long favouriteId;

        public RideHistoryHolder(@NonNull View itemView) {
            super(itemView);
            rideLocations = itemView.findViewById(R.id.history_item_location_txt);
            rideInfo = itemView.findViewById(R.id.history_item_info_txt);
            inboxButton = itemView.findViewById(R.id.history_inbox);

            inboxButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToChat(v);
                }
            });

            favouriteButton = itemView.findViewById(R.id.history_mark_favourite);

            favouriteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleFavourite(v);
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), PassengerHistoryRideDetailsActivity.class);
            intent.putExtra("ride", this.ride.getId());
            v.getContext().startActivity(intent);
        }

        private void setUpFavouriteStatus(Long idOfFavouriteRide) {
            if (idOfFavouriteRide != -1) {
                favouriteButton.setImageResource(R.drawable.ic_heart_full);
                favouriteButton.setTag("full");
                this.favouriteId = idOfFavouriteRide;
            }
        }

        private void goToChat(View v) {
            this.otherId = Objects.equals(this.ride.getDriver().getId(), userId) ? this.ride.getPassengers().get(0).getId() : this.ride.getDriver().getId();
            this.otherFullName = Objects.equals(this.ride.getDriver().getId(), userId) ? this.ride.getPassengers().get(0).getEmail() : this.ride.getDriver().getEmail();

            Intent intent = new Intent(v.getContext(), MessagesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("rideId", this.ride.getId());
            intent.putExtra("type", "RIDE");
            intent.putExtra("otherId", otherId);
            intent.putExtra("otherFullName", otherFullName);
            v.getContext().startActivity(intent);
        }

        private void toggleFavourite(View v) {
            if (favouriteButton.getTag().toString().equals("empty")) {
                favouriteButton.setImageResource(R.drawable.ic_heart_full);
                favouriteButton.setTag("full");
                addRideToFavourites(v);
            } else {
                favouriteButton.setImageResource(R.drawable.ic_heart_empty);
                favouriteButton.setTag("empty");
                removeRideFromFavourite(v);
            }
        }

        private void addRideToFavourites(View v) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    String favouriteName = ride.getLocations().get(0).getDeparture().getAddress() + " - " + ride.getLocations().get(0).getDestination().getAddress();
                    FavouriteRideBasicDTO favouriteRideBasic = new FavouriteRideBasicDTO(favouriteName, ride.getLocations(), ride.getPassengers(), ride.getVehicleType(), ride.isBabyTransport(), ride.isPetTransport(), 1000.0);
                    Call<FavouriteRideDTO> call = ServiceUtils.rideEndpoints.addFavouriteRide(favouriteRideBasic);
                    call.enqueue(new Callback<FavouriteRideDTO>() {
                        @Override
                        public void onResponse(Call<FavouriteRideDTO> call, Response<FavouriteRideDTO> response) {
                            Activity activity = (Activity) v.getContext();
                            activity.finish();
                            v.getContext().startActivity(new Intent(v.getContext(), PassengerRideHistoryActivity.class));
                            favouriteId = response.body().getId();
                        }

                        @Override
                        public void onFailure(Call<FavouriteRideDTO> call, Throwable t) {
                            Log.e("HISTORY", "Ride could not be added to favourite " + t.getMessage());
                        }
                    });
                }
            });
        }

        private void removeRideFromFavourite(View v) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Call<String> call = ServiceUtils.rideEndpoints.deleteFavouriteRide(favouriteId);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Activity activity = (Activity) v.getContext();
                            activity.finish();
                            v.getContext().startActivity(new Intent(v.getContext(), PassengerRideHistoryActivity.class));
                            favouriteId = -1L;
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Log.e("HISTORY", "Removing from favourite declined: " + t.getMessage());
                        }
                    });
                }
            });
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private final ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.loadmore_progress);

        }
    }

    class PassengerHistoryComparator implements Comparator<RideForUserDTO> {
        @Override
        public int compare(RideForUserDTO o1, RideForUserDTO o2) {
            return o2.getId().compareTo(o1.getId());
        }
    }
}
