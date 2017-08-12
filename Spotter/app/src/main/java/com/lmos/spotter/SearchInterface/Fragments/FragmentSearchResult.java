package com.lmos.spotter.SearchInterface.Fragments;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.Deals;
import com.lmos.spotter.MapsLayoutFragment;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.ReviewActivity;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.SearchInterface.Adapters.DealsAdapter;
import com.lmos.spotter.SearchInterface.Adapters.ReviewDialogFragment;
import com.lmos.spotter.SearchInterface.Adapters.SearchReviewsAdapter;
import com.lmos.spotter.SyncService;
import com.lmos.spotter.UserReview;
import com.lmos.spotter.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

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
    List<UserReview> userReviewList;
    List<Deals> dealsItem;
    ProgressBar reviewPb;
    DealsAdapter dealAdapter;
    int tempUserRev;
    private RecyclerView reviews;
    private RecyclerView deals;

    public static FragmentSearchResult newInstance(Place place){

        FragmentSearchResult fragmentSearchResult = new FragmentSearchResult();

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", place);

        fragmentSearchResult.setArguments(bundle);

        return fragmentSearchResult;
    }

    public static Place getPlace () {
        return place;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FragmentResult", "Building client");
        locationHandler = new Utilities.LocationHandler(getActivity(), this);
        locationHandler.buildGoogleClient();

        place = getArguments().getParcelable("data");
        assert place != null;
        tempUserRev = Integer.parseInt(place.getUserReviews());
        ReviewActivity.placeID = place.getPlaceID();

        dealsItem = new ArrayList<>();

        /** Get Place deals and store it in a List **/

        try {

            JSONObject dealObj = new JSONObject(place.getPlaceDeals());
            JSONArray imageLink = dealObj.getJSONArray("ImageLink");
            JSONArray dealName = dealObj.getJSONArray("DealName");
            JSONArray dealDesc = dealObj.getJSONArray("Description");

            Log.d("DEALS", "IMGSIZE: " + String.valueOf(imageLink.length()));
            Log.d("DEALS", "NAMESIZE: " + String.valueOf(dealName.length()));
            Log.d("DEALS", "DESCSIZE: " + String.valueOf(dealDesc.length()));

            for (int i = 0; i < imageLink.length(); i++){

                Deals deals = new Deals();
                deals.setDealImg(imageLink.get(i).toString());
                deals.setDealName(dealName.get(i).toString());
                deals.setDealDesc(dealDesc.get(i).toString());
                deals.setContact(place.getPlaceLinks());
                Log.d("DEALS", "ADDING");
                dealsItem.add(deals);
                Log.d("DEALS", String.valueOf(dealsItem.size()));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        finally {
            dealAdapter = new DealsAdapter(getContext(), dealsItem);
        }


    }

    public void updateBookmark(){
        place_bookmark.setText((Integer.parseInt(place.getBookmarks()) + 1) + " people bookmarked this.");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("FragmentResult", "Connecting");
        locationHandler.changeApiState("connect");

        if(Integer.parseInt(place.getUserReviews()) > 0){

            if(Utilities.checkNetworkState(getContext())){
                loadReviewData();
            }
            else{
                no_review.setText("Not connected to the internet.");
                reviewPb.setVisibility(View.GONE);
                showReview.setVisibility(View.GONE);
            }

        }

        else{
            reviewPb.setVisibility(View.GONE);
            no_review.setVisibility(View.VISIBLE);
            showReview.setText("Give a Review");
        }

        Log.d("UpdateView", "Starting " + place.getPlaceID());
        Intent updatePlaceView = new Intent(getContext(), SyncService.class);
        updatePlaceView.putExtra("action", "updateView");
        updatePlaceView.putExtra("placeID", place.getPlaceID());
        getActivity().startService(updatePlaceView);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (tempUserRev != Integer.parseInt(place.getUserReviews())){
            loadReviewData();
            String revCount = "Users Review (" + userReviewList.size() + ")";
            review_count.setText(revCount);
            place_rate.setRating(Float.parseFloat(place.getPlaceRating()));
            place_rate_label.setText(place_rate.getRating() + " Stars");
            place_recommended.setText(place.getRecommended() + " people recommmended this.");
        }
        else{
            if(userReviewList != null && userReviewList.size() > 0)
                showReview.setText("SHOW ALL");
        }
    }

    public void loadReviewData(){

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
        showReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (String.valueOf(showReview.getText())){

                    case "Give a Review":
                        SharedPreferences userPreference = getContext().getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);
                        if (userPreference.getString("status", "").equals("Logged In")) {

                            ReviewDialogFragment reviewDialogFragment = new ReviewDialogFragment();

                            reviewDialogFragment.setOnReviewPost(new ReviewDialogFragment.OnReviewPost() {

                                @Override
                                public void reviewPost(String... post_data) {


                                    UserReview addReview = new UserReview();

                                    addReview.userName = post_data[0];
                                    addReview.setReview(post_data[1]);
                                    addReview.setRating(Float.valueOf(post_data[2]));
                                    addReview.setRecommend(Boolean.valueOf(post_data[3]));
                                    addReview.setDate("Just now");

                                    if(userReviewList == null){
                                        userReviewList = new ArrayList<UserReview>();
                                        userReviewList.add(addReview);
                                        mAdapter = new SearchReviewsAdapter(getContext(), userReviewList);
                                        reviews.setAdapter(mAdapter);
                                        reviews.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
                                        reviewPb.setVisibility(View.GONE);
                                        reviews.setVisibility(View.VISIBLE);
                                        no_review.setVisibility(View.GONE);
                                        showReview.setText("Show All");
                                    }

                                    review_count.setText("Users Review(1)");
                                    place_recommended.setText(
                                            (addReview.isRecommend())?
                                                    "1 people  recommended this." :
                                                    "0 people recommended this."
                                    );
                                    place_rate.setRating(addReview.getRating());
                                    place_rate_label.setText(addReview.getRating() + " Stars");

                                }
                            });

                            reviewDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                            reviewDialogFragment.show(getFragmentManager(), "reviewDialogFragment");


                        } else {

                            new AlertDialog.Builder(getContext()).setTitle("Account Required")
                                    .setMessage("Hey wanderer! It looks like you are not yet signed in. Please log in your account to post a review.")
                                    .setPositiveButton("Sign in", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Utilities.OpenActivity(getContext(),
                                                    LoginActivity.class,
                                                    null);
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            dialog.dismiss();

                                        }
                                    }).show();

                        }
                        break;
                    default:
                        Bundle placeID = new Bundle();
                        placeID.putString("placeID", place.getPlaceID());
                        Utilities.OpenActivityWithBundle(getActivity(), ReviewActivity.class, null, placeID);
                        break;

                }

            }
        });
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**Intent navigate = new Intent(getContext(), NavigationActivity.class);
                navigate.putExtra(
                        "destination",
                        new LatLng(
                                Double.parseDouble(place.getPlaceLat()),
                                Double.parseDouble(place.getPlaceLng())
                        )
                );
                startActivity(navigate);**/
                try{
                    String url = "https://waze.com/ul?ll=" + place.getPlaceLat() + "," + place.getPlaceLng() + "&navigate=yes";
                    Intent navigate = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(navigate);
                }
                catch (ActivityNotFoundException e){
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                    startActivity(intent);
                }
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

        deals.setAdapter(dealAdapter);
        deals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

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



    class loadReview extends AsyncTask<String, Void, String>{

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

                Collections.reverse(userReviewList);

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

            }

        }
    }
}
