package com.lmos.spotter.MainInterface.Fragments.Frags;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.MainInterface.Adapters.FindPlaceTabPagerAdapter;
import com.lmos.spotter.R;

/**
 * Created by Kryssel on 6/10/2017.
 */

public class FindedPlacesFragment extends Fragment {
    @Nullable

    public static Fragment createObject (String userCityLocation) {

        FindedPlacesFragment findedPlacesFragment = new FindedPlacesFragment();

        Bundle locationData = new Bundle();

        locationData.putString("userCityLocation", userCityLocation);

        findedPlacesFragment.setArguments(locationData);

        return findedPlacesFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_finded_places, container, false);
    }

    @Override
    public void onStart() {

        super.onStart();

        View view = getView();

        TextView txtLocationResult = (TextView) view.findViewById(R.id.findedPlaces);

        TabLayout placesResultsTab = (TabLayout)view.findViewById(R.id.tab_places_layout);

        placesResultsTab.addTab(placesResultsTab.newTab().setText("Hotels"));
        placesResultsTab.addTab(placesResultsTab.newTab().setText("Tourist Spots"));
        placesResultsTab.addTab(placesResultsTab.newTab().setText("Restaurants"));

        final ViewPager placePager = (ViewPager) view.findViewById(R.id.places_pager);

        placePager.setAdapter(new FindPlaceTabPagerAdapter(getActivity().getSupportFragmentManager(), 3));
        placePager.setOffscreenPageLimit(3);
        placePager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(placesResultsTab));

        placesResultsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                placePager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ViewGroup actionBarLayout = (ViewGroup) getActivity().getLayoutInflater().inflate(R.layout.searchbar, null);

        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);

        final TextView appBarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.txtHome);

        SearchView placesSearchBar = (SearchView)actionBar.getCustomView().findViewById(R.id.search_view);

        placesSearchBar.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                appBarTitle.setVisibility(View.GONE);
            }
        });

        placesSearchBar.setOnCloseListener(new SearchView.OnCloseListener() {

            @Override
            public boolean onClose() {
                appBarTitle.setVisibility(View.VISIBLE);
                return false;
            }
        });

        appBarTitle.setTextSize(16.0f);
        appBarTitle.setText("Places at Las pinas city");


    }


}
