package com.cruisemobile.cruise.adapters;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.RideForTransferDTO;

public class SplitFareMailsAdapter extends BaseAdapter {

    Activity activity;
    RideForTransferDTO ride;

    public SplitFareMailsAdapter(Activity activity, RideForTransferDTO ride) {
        this.activity = activity;
        this.ride = ride;
    }

    @Override
    public int getCount() {
        return this.ride.getPassengers().size();
    }

    @Override
    public Object getItem(int position) {
        return this.ride.getPassengers().get(position).getEmail();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        String email = this.ride.getPassengers().get(position).getEmail();

        if (convertView == null)
            vi = activity.getLayoutInflater().inflate(R.layout.email_split_fare, null);

        TextView emailTxt = vi.findViewById(R.id.email_splitfare);
        emailTxt.setText(email);

        return vi;
    }
}
