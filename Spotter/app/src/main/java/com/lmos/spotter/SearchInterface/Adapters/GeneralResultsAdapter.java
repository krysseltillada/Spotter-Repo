package com.lmos.spotter.SearchInterface.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static Context context;
    private List<Place> places;
    public GeneralResultsAdapter(Context context,List<Place> places){
        this.places = new ArrayList<Place>(places);
        GeneralResultsAdapter.context = context;
    }

    public GeneralResultsAdapter(List<Place> places){
        this.places = new ArrayList<Place>(places);

    }

    @Override
    public GeneralResultsAdapter.GeneralResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_general_list_item, parent, false);
        return new GeneralResultsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(GeneralResultsAdapter.GeneralResultsViewHolder holder, int position) {
        Log.d("LOG", "adapter position: " + position);
        holder.name.setText(places.get(position).getPlaceName());
        holder.desc.setText(places.get(position).getPlaceDescription());
        holder.priceRange.setText(places.get(position).getPlacePriceRange());
        holder.rating.setText(places.get(position).getPlaceRating());

        try{

            String placeImageLink =
                    new JSONObject(
                            new JSONObject(places.get(position).getPlaceImageLink())
                            .getString("placeImages")
                    ).getString("frontImage");

            Picasso.with(context)
                    .load(placeImageLink)
                    .placeholder(R.drawable.loadingplace)
                    .into(holder.placeImage);

        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public void setOnItemClickListener(OnClickListener onClickListener){
       GeneralResultsAdapter.onClickListener = onClickListener;
    }

    public interface OnClickListener{

        void OnItemClick(Place place);
        void OnItemLongClick(View view, Place place);

    }

    public class GeneralResultsViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener{

        TextView name, desc, rating, priceRange;
        ImageView placeImage;

        public GeneralResultsViewHolder(View itemView) {
            super(itemView);
                name = (TextView) itemView.findViewById(R.id.general_list_name);
                desc = (TextView) itemView.findViewById(R.id.general_list_description);
                rating = (TextView) itemView.findViewById(R.id.general_list_rating);
                priceRange = (TextView) itemView.findViewById(R.id.general_list_price_range);
                placeImage = (ImageView) itemView.findViewById(R.id.general_list_image);
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

}
