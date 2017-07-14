package com.lmos.spotter.SearchInterface.Adapters;

import android.content.Context;
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
    Context context;

    public SearchReviewsAdapter (Context con, List<UserReview> data) {
        dataList = data;
        context = con;
    }


    @Override
    public SearchReviewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_reviews_list_item, parent, false);
        return new SearchReviewsHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(SearchReviewsHolder holder, int position) {

        UserReview userReview = dataList.get(position);

        holder.userImage.setImageDrawable(new BitmapDrawable(context.getResources(), userReview.profileImage));

        holder.userName.setText("by " + userReview.userName);
        holder.review.setText(userReview.getReview());
        holder.date.setText(userReview.getDate());
        holder.isRecommended.setVisibility((userReview.isRecommend()) ? View.VISIBLE : View.GONE);
        holder.rating.setText(String.valueOf(userReview.getRating()));

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
