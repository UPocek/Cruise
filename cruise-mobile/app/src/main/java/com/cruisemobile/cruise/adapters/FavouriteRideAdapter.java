package com.cruisemobile.cruise.adapters;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.activities.CruiseConfirmedActivity;
import com.cruisemobile.cruise.activities.PassengerAccountDetailActivity;
import com.cruisemobile.cruise.models.FavouriteRideDTO;
import com.cruisemobile.cruise.services.ServiceUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class FavouriteRideAdapter extends RecyclerView.Adapter<FavouriteRideAdapter.FavouriteRideHolder> {
    private ExecutorService executorService;
    private LayoutInflater mInflater;
    private Context context;
    private SharedPreferences sharedPreferences;
    private ArrayList<FavouriteRideDTO> favouriteRides;

    public FavouriteRideAdapter(Context context, ArrayList<FavouriteRideDTO> favouriteRides) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.executorService = Executors.newSingleThreadExecutor();
        this.favouriteRides = favouriteRides;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FavouriteRideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewItem = mInflater.inflate(R.layout.favourite_ride,
                parent, false);
        return new FavouriteRideHolder(viewItem, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteRideHolder holder, int position) {
        FavouriteRideDTO mCurrent = favouriteRides.get(position);
        FavouriteRideAdapter.FavouriteRideHolder favouriteRideViewHolder = holder;
        favouriteRideViewHolder.favouriteRideName.setText(mCurrent.getFavoriteName());
        favouriteRideViewHolder.favouriteRideDTO = mCurrent;
    }

    @Override
    public int getItemCount() {
        return favouriteRides.size();
    }

    public void setFavouriteRidesList(ArrayList<FavouriteRideDTO> favouriteRidesList) {
        favouriteRides = favouriteRidesList;
        notifyDataSetChanged();
    }

    private void deleteFavouriteRide(Long id) throws IOException {
        Call<String> call = ServiceUtils.rideEndpoints.deleteFavouriteRide(id);
        call.execute();
    }

    private void processDeletingFavouriteRide(FavouriteRideDTO favouriteRideDTO) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    deleteFavouriteRide(favouriteRideDTO.getId());
                    favouriteRides.remove(favouriteRideDTO);
                    ((PassengerAccountDetailActivity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    Log.e("FAVOURITE", "Favourite ride not deleted");
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void bookRide(FavouriteRideDTO favouriteRideDTO, String startTime) {
        Intent intent = new Intent(context, CruiseConfirmedActivity.class);
        intent.putExtra("from", favouriteRideDTO.getLocations().get(0).getDeparture().getAddress());
        intent.putExtra("to", favouriteRideDTO.getLocations().get(0).getDestination().getAddress());
        intent.putExtra("vehicleType", favouriteRideDTO.getVehicleType());
        intent.putExtra("startTime", startTime);
        intent.putExtra("petTransport", favouriteRideDTO.isPetTransport());
        intent.putExtra("babyTransport", favouriteRideDTO.isBabyTransport());
        context.startActivity(intent);
    }

    class FavouriteRideHolder extends RecyclerView.ViewHolder {

        public final Button bookNowBtn;
        public final Button bookFutureBtn;
        public final ImageView deleteFavouriteRideIcon;
        private final TextView favouriteRideName;
        private final FavouriteRideAdapter adapter;
        public FavouriteRideDTO favouriteRideDTO;
        private String startTime;

        public FavouriteRideHolder(@NonNull View itemView, FavouriteRideAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            this.favouriteRideName = itemView.findViewById(R.id.favourite_ride_name);
            this.bookNowBtn = itemView.findViewById(R.id.favourite_ride_book_now);
            this.bookFutureBtn = itemView.findViewById(R.id.favourite_ride_book_future);
            this.deleteFavouriteRideIcon = itemView.findViewById(R.id.favourite_ride_delete);

            TimePickerDialog timePickerDialog = new TimePickerDialog(adapter.context, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    startTime = String.valueOf(LocalDateTime.now().withHour(hourOfDay).withMinute(minute));
                    bookRide(favouriteRideDTO, startTime);
                }
            }, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), true);

            this.bookNowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.bookRide(favouriteRideDTO, LocalDateTime.now().toString());
                }
            });

            this.bookFutureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timePickerDialog.show();
                }
            });


            this.deleteFavouriteRideIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (favouriteRideDTO != null)
                        adapter.processDeletingFavouriteRide(favouriteRideDTO);
                }
            });
        }
    }
}
