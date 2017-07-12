package com.lmos.spotter.SearchInterface.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.lmos.spotter.MapsLayoutFragment;
import com.lmos.spotter.NavigationActivity;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.Utilities.Utilities;

/**
 * Created by linker on 06/06/2017.
 */

public class FragmentSearchResult extends Fragment
    implements Utilities.OnLocationFoundListener{

    TextView description, review_count, place_address, place_rate_label;
    RatingBar place_rate;
    Button navigate;
    Place place;
    Utilities.LocationHandler locationHandler;
    private RecyclerView reviews;
    private RecyclerView deals;

    public static FragmentSearchResult newInstance(Place place){

        FragmentSearchResult fragmentSearchResult = new FragmentSearchResult();

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", place);

        fragmentSearchResult.setArguments(bundle);

        return fragmentSearchResult;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FragmentResult", "Building client");
        locationHandler = new Utilities.LocationHandler(getActivity(), this);
        locationHandler.buildGoogleClient();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("FragmentResult", "Connecting");
        locationHandler.changeApiState("connect");
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        place = getArguments().getParcelable("data");

        View thisView = inflater.inflate(R.layout.search_result_hrt, container, false);

        ((SearchResultsActivity) getContext() ).setHeaderText(place.getPlaceName());

        reviews = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        deals = (RecyclerView) thisView.findViewById(R.id.reviews_recycler);
        description = (TextView) thisView.findViewById(R.id.place_about);
        review_count = (TextView) thisView.findViewById(R.id.review_count);
        place_rate_label = (TextView) thisView.findViewById(R.id.place_rating_label);
        place_rate = (RatingBar) thisView.findViewById(R.id.place_rate);
        place_address = (TextView)thisView.findViewById(R.id.place_address);
        navigate = (Button) thisView.findViewById(R.id.btnNavigate);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigate = new Intent(getContext(), NavigationActivity.class);
                navigate.putExtra(
                        "destination",
                        new LatLng(
                                Double.parseDouble(place.getPlaceLat()),
                                Double.parseDouble(place.getPlaceLng())
                        )
                );
                startActivity(navigate);
            }
        });

        description.setText(place.getPlaceDescription());
        place_address.setText(place.getPlaceAddress());
        place_rate.setRating(Float.parseFloat(place.getPlaceRating()));
        String rating_place_holder = place.getPlaceRating() + " Stars";
        place_rate_label.setText(rating_place_holder);

        Log.d("JSON-imageLink", place.getPlaceImageLink());

        ((SearchResultsActivity) getContext()).setHeaderImg(place.getPlaceImageLink());

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, 0)
                .add(
                        R.id.map_content_holder,
                        MapsLayoutFragment.newInstance(
                                new LatLng(
                                    Double.parseDouble(place.getPlaceLat()),
                                    Double.parseDouble(place.getPlaceLng())
                                )
                        ),
                        "Map"
                )
                .commit();

        return thisView;
    }


    @Override
    public void onLocationFoundCity(String city) {

    }

    @Override
    public void onLocationFoundLatLng(double lat, double lng) {

        Fragment mapFragment = getFragmentManager().findFragmentByTag("Map");

        if(mapFragment != null && mapFragment instanceof MapsLayoutFragment)
            ((MapsLayoutFragment) mapFragment).setUserPosition(
                    new LatLng(lat, lng), "", null);

    }
}
