package com.lmos.spotter.SearchInterface.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lmos.spotter.R;

/**
 * Created by emman on 6/9/2017.
 */

public class GeneralResultsAdapter extends RecyclerView.Adapter<GeneralResultsAdapter.GeneralResultsViewHolder>{

    private static OnClickListener onClickListener;

    public interface OnClickListener{

        void OnItemClick(int pos, View view);
        void OnItemLongClick(int pos, View view);

    }

    public GeneralResultsAdapter(){}

    String[][] items = {

            {"HYATT Hotel", "Kanan ka sa bandang kaliwa", "2.5"},
            {"Resorts World Manila", "Deretso lang boss", "1.5"},
            {"Solaire Resorts and Casino", "Dun sa banda dun", "3.5"},
            {"OKADA", "Honto ni watashi no baka", "4.5"},
            {"Siayan Travelers Inn", "Punta ka lang sa kanto tapos deretso", "5"},
            {"Vikings", "Kanan ka sa bandang kaliwa", "2.5"},
            {"Aling Puring", "Deretso lang boss", "1.5"},
            {"Mang Inadobo", "Dun sa banda dun", "3.5"},
            {"LUGAW AT KAPE", "Honto ni watashi no baka", "4.5"},
            {"Caffeine", "Punta ka lang sa kanto tapos deretso", "5"},
            {"Pico de Loro", "Kanan ka sa bandang kaliwa", "2.5"},
            {"Mt. Butak", "Deretso lang boss", "1.5"},
            {"Pagsanghan falls", "Dun sa banda dun", "3.5"},
            {"Calaruega", "Honto ni watashi no baka", "4.5"},
            {"Underground River", "Punta ka lang sa kanto tapos deretso", "5"}

    };


    @Override
    public GeneralResultsAdapter.GeneralResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_general_list_item, parent, false);
        return new GeneralResultsViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(GeneralResultsAdapter.GeneralResultsViewHolder holder, int position) {
        holder.name.setText(items[position][0]);
        holder.place.setText(items[position][1]);
        holder.rating.setText(items[position][2]);
    }


    @Override
    public int getItemCount() {
        return items.length;
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
        }

        @Override
        public void onClick(View v) {
            onClickListener.OnItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            onClickListener.OnItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    public void setOnItemClickListener(OnClickListener onClickListener){
       GeneralResultsAdapter.onClickListener = onClickListener;
    }

}
