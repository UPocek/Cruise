package com.cruisemobile.cruise.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.OfferDTO;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.OfferHolder> {

    private LayoutInflater mInflater;
    private List<OfferDTO> offers;

    public OfferAdapter(Context context, List<OfferDTO> offers) {
        mInflater = LayoutInflater.from(context);
        this.offers = offers;
    }

    @NonNull
    @Override
    public OfferAdapter.OfferHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.route_proposal_card,
                parent, false);
        return new OfferHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferAdapter.OfferHolder holder, int position) {
        OfferDTO mCurrent = offers.get(position);
        holder.offerName.setText(String.format("Offer %s", position + 1));
        holder.offerTime.setText(String.format("%s min ride", mCurrent.getEstimatedTimeInMinutes()));
        holder.offerPrice.setText(String.format("%s rsd", mCurrent.getEstimatedCost()));
    }

    @Override
    public int getItemCount() {
        return offers.size();
    }

    class OfferHolder extends RecyclerView.ViewHolder {

        OfferAdapter offerAdapter;
        private TextView offerName;
        private TextView offerTime;
        private TextView offerPrice;

        public OfferHolder(@NonNull View itemView, OfferAdapter adapter) {
            super(itemView);
            this.offerAdapter = adapter;
            this.offerName = itemView.findViewById(R.id.offer_name);
            this.offerTime = itemView.findViewById(R.id.offer_time);
            this.offerPrice = itemView.findViewById(R.id.offer_total_price);
        }
    }
}
