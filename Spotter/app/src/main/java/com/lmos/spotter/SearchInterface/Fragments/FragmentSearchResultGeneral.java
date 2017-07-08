package com.lmos.spotter.SearchInterface.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.SearchInterface.Adapters.GeneralResultsAdapter;
import com.lmos.spotter.Utilities.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by linker on 02/06/2017.
 */

public class FragmentSearchResultGeneral extends Fragment {

    private static RecyclerView recyclerView;
    private static GeneralResultsAdapter mAdapter;
    private static List<Place> places;
    RecyclerView.LayoutManager layoutManager;

    public static FragmentSearchResultGeneral newInstance(String focusedTab, List<Place> places){

        FragmentSearchResultGeneral fsg = new FragmentSearchResultGeneral();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("places", new ArrayList<Place>(places));
        bundle.putString("type", focusedTab);
        fsg.setArguments(bundle);

        return fsg;
    }

    public static List<Place> setPlaceByType(String type){
        List<Place> setPlace = new ArrayList<>();
        for(Place p : places){
            if(p.getPlaceType().equalsIgnoreCase(type))
                setPlace.add(p);

            Log.d("type", p.getPlaceType());
        }

        Collections.sort(setPlace, new Utilities.SortPlaces());

        return setPlace;
    }

    public static void changeAdapter(String type){
        if(mAdapter != null)
            mAdapter = null;

        mAdapter = new GeneralResultsAdapter(setPlaceByType(type));
        mAdapter.notifyItemChanged(0);
        recyclerView.swapAdapter(mAdapter, false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View thisView = inflater.inflate(R.layout.search_result_general, container, false);
        Log.d("LOG", "Creating fragment view");
        places = getArguments().getParcelableArrayList("places");

        recyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        mAdapter = new GeneralResultsAdapter(getContext(), setPlaceByType(getArguments().getString("type")));

        // Set RecyclerView onClickListener
        mAdapter.setOnItemClickListener(new GeneralResultsAdapter.OnClickListener() {
            @Override
            public void OnItemClick(Place place) {
                ((SearchResultsActivity) getContext()).switchFragment("", "replace", FragmentSearchResult.newInstance(place));
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
        recyclerView.requestDisallowInterceptTouchEvent(true);

        return thisView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        places = null;
    }
}
