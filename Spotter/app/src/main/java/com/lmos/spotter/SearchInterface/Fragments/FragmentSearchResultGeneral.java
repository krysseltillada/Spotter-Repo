package com.lmos.spotter.SearchInterface.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.R;

/**
 * Created by linker on 02/06/2017.
 */

public class FragmentSearchResultGeneral extends Fragment {

    TabLayout tabLayout;
    RecyclerView recyclerView;
    GeneralResultsAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.search_result_general, container, false);

        tabLayout = (TabLayout) thisView.findViewById(R.id.search_tab_layout);
        recyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        mAdapter = new GeneralResultsAdapter();
        layoutManager = new LinearLayoutManager(getContext());

        // Set recyclerview params
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        /**
         * This method will allow the RecyclerView to scroll
          * smoothly inside NestedScrollView
         **/
        recyclerView.setNestedScrollingEnabled(false);

        return thisView;
    }

    public class GeneralResultsAdapter extends RecyclerView.Adapter<GeneralResultsAdapter.GeneralResultsViewHolder>{


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
                {"Underground River", "Punta ka lang sa kanto tapos deretso", "5"},

        };


        @Override
        public GeneralResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View layoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_general_list_item, parent, false);
            return new GeneralResultsViewHolder(layoutView);

        }

        @Override
        public void onBindViewHolder(GeneralResultsViewHolder holder, int position) {

            holder.name.setText(items[position][0]);
            holder.place.setText(items[position][1]);
            holder.rating.setText(items[position][2]);

        }

        @Override
        public int getItemCount() {
            return items.length;
        }

        public class GeneralResultsViewHolder extends RecyclerView.ViewHolder{

            TextView name, place, rating;

            public GeneralResultsViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.general_list_name);
                place = (TextView) itemView.findViewById(R.id.general_list_address);
                rating = (TextView) itemView.findViewById(R.id.general_rating_digit);
            }
        }

    }

}
