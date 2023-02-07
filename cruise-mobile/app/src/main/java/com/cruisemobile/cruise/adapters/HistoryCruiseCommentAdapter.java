package com.cruisemobile.cruise.adapters;

import android.app.Activity;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cruisemobile.cruise.R;
import com.cruisemobile.cruise.models.ReviewPairDTO;

import java.util.ArrayList;
import java.util.List;

public class HistoryCruiseCommentAdapter extends BaseAdapter {

    Activity activity;
    private List<ReviewPairDTO> reviews;
    private List<Pair<String, String>> comments;

    public HistoryCruiseCommentAdapter(Activity activity, List<ReviewPairDTO> reviews) {
        this.activity = activity;
        this.reviews = reviews;
        comments = new ArrayList<>();
        for (ReviewPairDTO review : reviews) {
            if (review.getDriverReview() != null) {
                comments.add(new Pair<>("DRIVER: ", review.getDriverReview().getComment()));
            }
            if (review.getVehicleReview() != null) {
                comments.add(new Pair<>("VEHICLE: ", review.getVehicleReview().getComment()));
            }
        }

    }

    @Override
    public int getCount() {
        return this.comments.size();
    }

    @Override
    public Object getItem(int position) {
        return this.comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        Pair<String, String> comment = this.comments.get(position);

        if (convertView == null)
            vi = activity.getLayoutInflater().inflate(R.layout.cruise_comment_layout, null);

        TextView commentText = vi.findViewById(R.id.history_cruise_comment);
        commentText.setText(comment.first.concat(comment.second));

        return vi;
    }
}