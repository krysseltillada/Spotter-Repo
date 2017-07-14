package com.lmos.spotter.SearchInterface.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.R;

import java.util.ArrayList;

/**
 * Created by emman on 6/10/2017.
 */

public class SearchReviewsAdapter extends RecyclerView.Adapter<SearchReviewsAdapter.SearchReviewsHolder> {

    @Override
    public SearchReviewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_reviews_list_item, parent, false);
        return new SearchReviewsHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(SearchReviewsHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class SearchReviewsHolder extends RecyclerView.ViewHolder{

        public SearchReviewsHolder(View itemView) {
            super(itemView);

        }
    }

}
