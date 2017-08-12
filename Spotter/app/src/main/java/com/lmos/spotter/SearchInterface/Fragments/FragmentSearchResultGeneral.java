package com.lmos.spotter.SearchInterface.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.lmos.spotter.NavigationActivity;
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

    public static String placeID;
    TextView no_result;
    RecyclerView.LayoutManager layoutManager;
    private List<Place> places;
    private RecyclerView recyclerView;
    private GeneralResultsAdapter mAdapter;

    public static FragmentSearchResultGeneral newInstance(String focusedTab, List<Place> places){

        FragmentSearchResultGeneral fsg = new FragmentSearchResultGeneral();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("places", new ArrayList<Place>(places));
        bundle.putString("type", focusedTab);
        fsg.setArguments(bundle);

        return fsg;
    }

    public List<Place> setPlaceByType(String type){
        List<Place> setPlace = new ArrayList<>();

        for(Place p : places){
            if(p.getPlaceType().equalsIgnoreCase(type))
                setPlace.add(p);

            Log.d("type", p.getPlaceType());
        }
        Log.d("SetPlace", String.valueOf(setPlace.size()));

        if(places.get(0).getDistance() != null)
            Collections.sort(setPlace, new Utilities.SortPlaces());

        return setPlace;
    }

    public void changeAdapter(String type){
        if(mAdapter != null)
            mAdapter = null;

        if(!setPlaceByType(type).isEmpty()){
            mAdapter = new GeneralResultsAdapter(getContext(), setPlaceByType(type));
            mAdapter.notifyDataSetChanged();
            // Set RecyclerView onClickListener
            mAdapter.setOnItemClickListener(new GeneralResultsAdapter.OnClickListener() {
                @Override
                public void OnItemClick(View v, Place place) {

                    switch (v.getId()){

                        case R.id.general_list_view_bookmark:
                            ((SearchResultsActivity) getContext()).queryFavorites("add", "", place);
                            break;
                        case R.id.general_list_explore:
                            /**Intent navigate = new Intent(getContext(), NavigationActivity.class);
                            navigate.putExtra(
                                    "destination",
                                    new LatLng(
                                            Double.parseDouble(place.getPlaceLat()),
                                            Double.parseDouble(place.getPlaceLng())
                                    )
                            );**/
                            try{
                                String url = "https://waze.com/ul?ll=" + place.getPlaceLat() + "&" + place.getPlaceLng();
                                Intent navigate = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(navigate);
                            }
                            catch (ActivityNotFoundException e){
                                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                                startActivity(intent);
                            }
                            break;
                        default:
                            ((SearchResultsActivity) getContext()).setTemp_place(place);
                            ((SearchResultsActivity) getContext()).switchFragment("", "replace", FragmentSearchResult.newInstance(place));
                            break;
                    }

                }

            });
            recyclerView.setAdapter(mAdapter);
            no_result.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else{
            no_result.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View thisView = inflater.inflate(R.layout.search_result_general, container, false);
        Log.d("LOG", "Creating fragment view");
        places = getArguments().getParcelableArrayList("places");

        thisView.findViewById(R.id.recycleViewProgressBar).setVisibility(View.GONE);

        no_result = (TextView) thisView.findViewById(R.id.no_result);

        recyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        //mAdapter = new GeneralResultsAdapter(getContext(), setPlaceByType(getArguments().getString("type")));
        changeAdapter(getArguments().getString("type"));


        // Set recyclerview params
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setAdapter(mAdapter);
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
