package com.lmos.spotter.SearchInterface.Adapters;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmos.spotter.R;
import com.lmos.spotter.UserReview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emman on 6/10/2017.
 */

public class SearchReviewsAdapter extends RecyclerView.Adapter<SearchReviewsAdapter.SearchReviewsHolder> {

    List<UserReview> dataList;

    public SearchReviewsAdapter (List<UserReview> data) {
        dataList = data;
    }

    @Override
    public SearchReviewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_reviews_list_item, parent, false);
        return new SearchReviewsHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(SearchReviewsHolder holder, int position) {

        holder.userImage.setImageDrawable(new BitmapDrawable())


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class SearchReviewsHolder extends RecyclerView.ViewHolder{

        ImageView userImage, isRecommended;
        TextView userName, review, rating, date;

        public SearchReviewsHolder(View itemView) {
            super(itemView);

            userImage = (ImageView)itemView.findViewById(R.id.userImage);
            isRecommended = (ImageView)itemView.findViewById(R.id.isRecommended);

            userName = (TextView)itemView.findViewById(R.id.userName);
            review = (TextView)itemView.findViewById(R.id.review);
            rating = (TextView)itemView.findViewById(R.id.general_rating_digit);
            date = (TextView)itemView.findViewById(R.id.date);

        }
    }

}
