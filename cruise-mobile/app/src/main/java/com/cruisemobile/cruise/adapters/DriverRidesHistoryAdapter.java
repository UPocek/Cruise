package com.cruisemobile.cruise.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.activities.DriverHistoryRideDetailsActivity;
import com.cruisemobile.cruise.activities.MessagesActivity;
import com.cruisemobile.cruise.models.RideForUserDTO;

import java.util.ArrayList;
import java.util.Objects;


public class DriverRidesHistoryAdapter extends RecyclerView.Adapter<DriverRidesHistoryAdapter.DriverRideHistoryHolder> {

    private ArrayList<RideForUserDTO> userRides;
    private LayoutInflater mInflater;
    private Long userId;
    private boolean isLoadingAdded = false;

    public DriverRidesHistoryAdapter(Context context, ArrayList<RideForUserDTO> userRides, Long userId) {
        this.userRides = userRides;
        this.userId = userId;
        mInflater = LayoutInflater.from(context);
    }


    @NonNull
    @Override
    public DriverRidesHistoryAdapter.DriverRideHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = mInflater.inflate(R.layout.ride_history_item,
                parent, false);
        return new DriverRidesHistoryAdapter.DriverRideHistoryHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull DriverRidesHistoryAdapter.DriverRideHistoryHolder holder, int position) {
        RideForUserDTO mCurrent = userRides.get(position);
        DriverRidesHistoryAdapter.DriverRideHistoryHolder rideViewHolder = holder;
        rideViewHolder.rideLocations.setText(String.format("%s: %s - %s", mCurrent.getId().toString(), mCurrent.getLocations().get(0).getDeparture().getAddress(), mCurrent.getLocations().get(0).getDestination().getAddress()));
        rideViewHolder.rideInfo.setText(String.format("%s - %s rsd / %s passenger(s) / %s vehicle", mCurrent.getStatus(), mCurrent.getTotalCost(), mCurrent.getPassengers().size(), mCurrent.getVehicleType()));
        rideViewHolder.ride = mCurrent;
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

    class DriverRideHistoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView rideLocations;
        private TextView rideInfo;
        private ImageButton inboxButton;
        private ImageButton favouriteButton;
        private Long otherId;
        private String otherFullName;
        private RideForUserDTO ride;
        private Long favouriteId;

        public DriverRideHistoryHolder(@NonNull View itemView) {
            super(itemView);
            rideLocations = itemView.findViewById(R.id.history_item_location_txt);
            rideInfo = itemView.findViewById(R.id.history_item_info_txt);
            inboxButton = itemView.findViewById(R.id.history_inbox);

            inboxButton.setOnClickListener(v -> goToChat(v));

            favouriteButton = itemView.findViewById(R.id.history_mark_favourite);
            favouriteButton.setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), DriverHistoryRideDetailsActivity.class);
            intent.putExtra("ride", this.ride.getId());
            v.getContext().startActivity(intent);
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
    }

}


