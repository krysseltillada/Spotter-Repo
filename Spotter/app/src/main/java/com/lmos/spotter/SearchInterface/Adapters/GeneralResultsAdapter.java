package com.lmos.spotter.SearchInterface.Adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.Place;
import com.lmos.spotter.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emman on 6/9/2017.
 * This class will load informations -
 * data about list of places in a certain city -
 * into RecyclerView.
 */

public class GeneralResultsAdapter extends RecyclerView.Adapter<GeneralResultsAdapter.GeneralResultsViewHolder>{

    private static OnClickListener onClickListener;

    public interface OnClickListener{

        void OnItemClick(Place place);
        void OnItemLongClick(View view, Place place);

    }

    List<Place> places;

    public GeneralResultsAdapter(List<Place> places){

        this.places = new ArrayList<Place>(places);
        if(this.places == null)
            Log.d("GeneralResult", "Null");

    }

    @Override
    public GeneralResultsAdapter.GeneralResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_general_list_item, parent, false);
        return new GeneralResultsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(GeneralResultsAdapter.GeneralResultsViewHolder holder, int position) {
        holder.name.setText(places.get(position).getPlaceName());
        holder.place.setText(places.get(position).getPlaceAddress());
        //holder.rating.setText(places);
    }


    @Override
    public int getItemCount() {
        return places.size();
    }

    public class GeneralResultsViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener{

        TextView name, place, rating;

        public GeneralResultsViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.general_list_name);
            place = (TextView) itemView.findViewById(R.id.general_list_address);
            rating = (TextView) itemView.findViewById(R.id.general_rating_digit);
            itemView.findViewById(R.id.search_general_viewContainer).setOnClickListener(this);
            itemView.findViewById(R.id.search_general_viewContainer).setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickListener.OnItemClick(places.get(getAdapterPosition()));
        }

        @Override
        public boolean onLongClick(View v) {
            onClickListener.OnItemLongClick(rating, places.get(getAdapterPosition()));
            return true;
        }
    }

    public void setOnItemClickListener(OnClickListener onClickListener){
       GeneralResultsAdapter.onClickListener = onClickListener;
    }

}
