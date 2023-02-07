package com.cruisemobile.cruise.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.activities.MessagesActivity;
import com.cruisemobile.cruise.models.ChatItemDTO;
import com.cruisemobile.cruise.models.RideForTransferDTO;
import com.cruisemobile.cruise.services.ServiceUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;

public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int LOADING = 0;
    private static final int RIDE = 1;
    private static final int PANIC = 2;
    private static final String PANIC_MESSAGES = "PANIC";
    private static final String RIDE_MESSAGES = "RIDE";
    private Context context;
    private ArrayList<ChatItemDTO> allChatItems;
    private boolean isLoadingAdded = false;
    private LayoutInflater mInflater;
    private Long userId;

    public InboxAdapter(Context context,
                        ArrayList<ChatItemDTO> rides, Long thisUserId) {
        this.context = context;
        this.allChatItems = rides;
        mInflater = LayoutInflater.from(context);
        userId = thisUserId;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == allChatItems.size() - 1 && isLoadingAdded) {
            return LOADING;
        } else if (allChatItems.get(position).getType().equals(RIDE_MESSAGES)) {
            return RIDE;
        }
        return PANIC;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType) {
            case RIDE:
                View viewItem = mInflater.inflate(R.layout.person_message_card,
                        parent, false);
                viewHolder = new RideMessageHolder(viewItem);
                break;
            case PANIC:
                View viewPanic = mInflater.inflate(R.layout.panic_message_card,
                        parent, false);
                viewHolder = new PanicMessageHolder(viewPanic);
                break;
            case LOADING:
                View viewLoading = mInflater.inflate(R.layout.item_progress,
                        parent, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatItemDTO mCurrent = allChatItems.get(position);
        switch (getItemViewType(position)) {
            case RIDE:
                RideMessageHolder rideViewHolder = (RideMessageHolder) holder;
                rideViewHolder.messageTitle.setText(String.format("%s: %s - %s", mCurrent.getRideId().toString(), mCurrent.getDepartureAddress(), mCurrent.getDestinationAddress()));
                rideViewHolder.messageStatus.setText(mCurrent.getRideStatus());
                rideViewHolder.rideId = mCurrent.getRideId();
                break;

            case PANIC:
                PanicMessageHolder panicViewHolder = (PanicMessageHolder) holder;
                panicViewHolder.messageTitle.setText(String.format("%s: %s - %s", mCurrent.getRideId().toString(), mCurrent.getDepartureAddress(), mCurrent.getDestinationAddress()));
                panicViewHolder.rideId = mCurrent.getRideId();
                break;

            case LOADING:
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return allChatItems.size();
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
    }

    public void setChatList(ArrayList<ChatItemDTO> filterlist, boolean sortOrderReverse) {
        allChatItems = filterlist;
        sortOnShakeChatItems(sortOrderReverse);
    }

    public void sortOnShakeChatItems(boolean sortOrderReverse) {
        if (sortOrderReverse) {
            this.allChatItems.sort(new InboxComparator().reversed());
        } else {
            this.allChatItems.sort(new InboxComparator());
        }
        notifyDataSetChanged();
    }

    public ChatItemDTO getItem(int position) {
        return allChatItems.get(position);
    }


    class RideMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView messageTitle;
        public final TextView messageStatus;
        public Long rideId;

        public RideMessageHolder(@NonNull View itemView) {
            super(itemView);
            this.messageTitle = itemView.findViewById(R.id.person_from_message);
            this.messageStatus = itemView.findViewById(R.id.person_when_message);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sendRideInfo(v);
        }

        private void sendRideInfo(View v) {
            RideForTransferDTO rideDetails;
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Call<RideForTransferDTO> call = ServiceUtils.rideEndpoints.getRideDetails(rideId);
                    RideForTransferDTO rideDetails;
                    try {
                        rideDetails = call.execute().body();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Intent intent = new Intent(v.getContext(), MessagesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("rideId", rideId);
                    intent.putExtra("type", RIDE_MESSAGES);
                    intent.putExtra("otherId", getOtherPersonId(rideDetails));
                    intent.putExtra("otherFullName", getOtherPersonEmail(rideDetails));
                    v.getContext().startActivity(intent);
                }
            });
        }

        private Long getOtherPersonId(RideForTransferDTO ride) {
            if (Objects.equals(userId, ride.getDriver().getId())) {
                return ride.getPassengers().get(0).getId();
            }
            return ride.getDriver().getId();
        }

        private String getOtherPersonEmail(RideForTransferDTO ride) {
            if (Objects.equals(userId, ride.getDriver().getId())) {
                return ride.getPassengers().get(0).getEmail();
            }
            return ride.getDriver().getEmail();
        }
    }

    class PanicMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView messageTitle;
        public Long rideId;


        public PanicMessageHolder(@NonNull View itemView) {
            super(itemView);
            this.messageTitle = itemView.findViewById(R.id.panic_from_message);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), MessagesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("rideId", rideId);
            intent.putExtra("type", PANIC_MESSAGES);
            intent.putExtra("otherFullName", rideId);
            v.getContext().startActivity(intent);
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);

        }
    }

    class InboxComparator implements Comparator<ChatItemDTO> {

        @Override
        public int compare(ChatItemDTO o1, ChatItemDTO o2) {
            return o1.getRideId().compareTo(o2.getRideId());
        }
    }
}

