package com.lmos.spotter.SearchInterface.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.lmos.spotter.MapsLayoutFragment;
import com.lmos.spotter.NavigationActivity;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.SearchInterface.Adapters.SearchReviewsAdapter;
import com.lmos.spotter.UserReview;
import com.lmos.spotter.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linker on 06/06/2017.
 */

public class FragmentSearchResult extends Fragment
    implements Utilities.OnLocationFoundListener{

    public static Place place;
    TextView description, review_count, place_address,
            place_rate_label, place_recommended, place_bookmark, no_review;
    RatingBar place_rate;
    Button navigate, showReview;
    SearchReviewsAdapter mAdapter;
    Utilities.LocationHandler locationHandler;
    private RecyclerView reviews;
    private RecyclerView deals;
    ProgressBar reviewPb;

    public static FragmentSearchResult newInstance(Place place){

        FragmentSearchResult fragmentSearchResult = new FragmentSearchResult();

        FragmentSearchResultGeneral.placeID = place.getPlaceID();

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

        RequestQueue requestReview = Volley.newRequestQueue(getContext());

        StringRequest requestString = new StringRequest(Request.Method.POST,
                "http://admin-spotter.000webhostapp.com/app_scripts/loadReview.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        new loadReview().execute(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("placeID", place.getPlaceID());
                    put("limit", "5");
                }};
            }
        };

        requestReview.add(requestString);

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        place = getArguments().getParcelable("data");
        View thisView = inflater.inflate(R.layout.search_result_hrt, container, false);

        ((SearchResultsActivity) getContext() ).setHeaderText(place.getPlaceName());

        reviews = (RecyclerView) thisView.findViewById(R.id.reviews_recycler);
        reviewPb = (ProgressBar) thisView.findViewById(R.id.reviewPb);
        deals = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        description = (TextView) thisView.findViewById(R.id.place_about);
        review_count = (TextView) thisView.findViewById(R.id.review_count);
        place_rate_label = (TextView) thisView.findViewById(R.id.place_rating_label);
        place_rate = (RatingBar) thisView.findViewById(R.id.place_rate);
        place_address = (TextView)thisView.findViewById(R.id.place_address);
        place_recommended = (TextView) thisView.findViewById(R.id.place_recommended_label);
        place_bookmark = (TextView) thisView.findViewById(R.id.place_bookmark_label);
        no_review = (TextView) thisView.findViewById(R.id.no_review_msg);
        review_count = (TextView) thisView.findViewById(R.id.review_count);
        navigate = (Button) thisView.findViewById(R.id.btnNavigate);
        showReview = (Button) thisView.findViewById(R.id.showAllReview);
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
        String rec = place.getRecommended() + " people recommended this.";
        String bkmk = place.getRecommended() + " people bookmarked this.";
        String revCount = "Users Review (" + place.getUserReviews() + ")";
        place_recommended.setText(rec);
        place_bookmark.setText(bkmk);
        review_count.setText(revCount);

        reviews.setVisibility(View.GONE);
        reviewPb.setVisibility(View.VISIBLE);

        if(place.getPlaceImageLink() != null)
            ((SearchResultsActivity) getContext()).setHeaderImg(place.getPlaceImageLink());

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, 0)
                .add(
                        R.id.map_content_holder,
                        MapsLayoutFragment.newInstance(
                                new LatLng(
                                    Double.parseDouble(place.getPlaceLat()),
                                    Double.parseDouble(place.getPlaceLng())
                                ),
                                place
                        ),
                        "Map"
                )
                .commit();

        return thisView;
    }

    public static Place getPlace () {
        return place;
    }

    class loadReview extends AsyncTask<String, Void, String>{

        List<UserReview> userReviewList;

        @Override
        protected String doInBackground(String... params) {

            String result;

            try{

                userReviewList = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(params[0]);

                JSONArray arrData = jsonObject.getJSONArray("userReviewData");

                for(int i = 0; i != arrData.length(); i++){

                    UserReview userReview = new UserReview();

                    JSONObject jsonElement = new JSONObject(arrData.getString(i));

                    userReview.userName = jsonElement.getString("Username");
                    userReview.profileImage = Utilities.BlurImg.stringToBitmap(jsonElement.getString("Image"));

                    userReview.setDate(jsonElement.getString("DatePosted"));
                    userReview.setReview(jsonElement.getString("Review"));
                    userReview.setRating((float) jsonElement.getDouble("Rating"));
                    userReview.setRecommend(jsonElement.getBoolean("isRecommended"));

                    userReviewList.add(userReview);
                }

                result = "Done.";

            }
            catch (JSONException e) {
                e.printStackTrace();
                result = e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if(s.equals("Done.")){

                    mAdapter = new SearchReviewsAdapter(getContext(), userReviewList);
                    reviews.setAdapter(mAdapter);
                    reviews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
                    reviewPb.setVisibility(View.GONE);
                    reviews.setVisibility(View.VISIBLE);
                    showReview.setVisibility(View.VISIBLE);

            }
            else{
                reviewPb.setVisibility(View.GONE);
                no_review.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onLocationFoundCity(String city) {

    }

    @Override
    public void onLocationFoundLatLng(double lat, double lng, float bearing) {

        Fragment mapFragment = getFragmentManager().findFragmentByTag("Map");

        if (mapFragment != null && mapFragment instanceof MapsLayoutFragment) {
            ((MapsLayoutFragment) mapFragment).setUserPosition(
                    new LatLng(lat, lng), "directions", null);

            locationHandler.stopLocationRequest();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(locationHandler.checkApiState()){
            locationHandler.stopLocationRequest();
            locationHandler.changeApiState("disconnect");}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationHandler.checkApiState()){
            locationHandler.stopLocationRequest();
            locationHandler.changeApiState("disconnect");}
    }
}
