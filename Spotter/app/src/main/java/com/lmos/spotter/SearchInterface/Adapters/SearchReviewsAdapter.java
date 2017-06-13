package com.lmos.spotter.SearchInterface.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.R;

/**
 * Created by emman on 6/10/2017.
 */

public class SearchReviewsAdapter extends RecyclerView.Adapter<SearchReviewsAdapter.SearchReviewsHolder> {

    String[] msg = {
            "\"This is exciting. I do not want to be in this hotel anymore. But do you even care? I guess not because nothing is real, nothing is true\"",
            "Kanan ka sa bandang kaliwa",
            "Deretso lang boss",
            "Dun sa banda dun",
            "Honto ni watashi no baka",
            "Punta ka lang sa kanto tapos deretso"
    };

    @Override
    public SearchReviewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_reviews_list_item, parent, false);
        return new SearchReviewsHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(SearchReviewsHolder holder, int position) {

        holder.search_review_msg.setText(msg[position]);

    }

    @Override
    public int getItemCount() {
        return msg.length;
    }

    public class SearchReviewsHolder extends RecyclerView.ViewHolder{

        TextView search_review_msg;

        public SearchReviewsHolder(View itemView) {
            super(itemView);
            search_review_msg = (TextView) itemView.findViewById(R.id.search_review_msg);
        }
    }

}
