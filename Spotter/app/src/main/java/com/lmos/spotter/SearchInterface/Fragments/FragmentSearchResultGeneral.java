package com.lmos.spotter.SearchInterface.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.SearchInterface.Adapters.GeneralResultsAdapter;
import com.lmos.spotter.Utilities.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linker on 02/06/2017.
 */

public class FragmentSearchResultGeneral extends Fragment {

    TabLayout tabLayout;
    RecyclerView recyclerView;
    GeneralResultsAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<Place> places;

    public static FragmentSearchResultGeneral newInstance(List<Place> places){

        FragmentSearchResultGeneral fsg = new FragmentSearchResultGeneral();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("places", new ArrayList<Place>(places));

        fsg.setArguments(bundle);

        return fsg;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View thisView = inflater.inflate(R.layout.search_result_general, container, false);

        places = getArguments().getParcelableArrayList("places");

        if(places == null)
            Log.d("Results", "Null");

        recyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        mAdapter = new GeneralResultsAdapter(places);
        layoutManager = new LinearLayoutManager(getContext());


        // Set RecyclerView onClickListener
        mAdapter.setOnItemClickListener(new GeneralResultsAdapter.OnClickListener() {
            @Override
            public void OnItemClick(Place place) {

                Toast.makeText(getContext(), place.getPlaceID(), Toast.LENGTH_LONG).show();

            }

            @Override
            public void OnItemLongClick(View parent, final Place place) {
                Utilities.inflateOptionItem(getContext(), parent, R.menu.bookmark_option, new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(getContext(), "Aw I was clicked", Toast.LENGTH_SHORT).show();
                        ((SearchResultsActivity) getContext()).queryFavorites("add", "", place);
                        return false;
                    }
                });
            }

        });

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

}
