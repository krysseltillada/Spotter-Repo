package com.lmos.spotter.SearchInterface.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Adapters.ReviewDialogFragment;
import com.lmos.spotter.SearchInterface.Adapters.SearchReviewsAdapter;
import com.lmos.spotter.UserReview;
import com.lmos.spotter.Utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*

    THIS MAKE A SCRIPT THAT GETS THE REVIEWS FOR THAT PLACE
    AND CALCULATES THE AVERAGE REVIEW ON THAT PLACE
    INSERT A REVIEW IN A REVIEWS TABLE


 */

public class ReviewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private NestedScrollView reviewScrollView;

    private ProgressBar reviewProgressBar;

    private RatingBar placeRatingBar;

    private TextView placeRatingText;
    private TextView placeUserReviews;
    private TextView placeBookmarks;
    private TextView placeRecommend;

    private TextView reviewListMsg;

    private CardView reviewInfoLayout;

    SharedPreferences userPreference;

    private RecyclerView reviewList;

    List<UserReview> userReviewList;
    List<UserReview> prevUserReviewList;

    private boolean isHasReviews;

    public static String placeID;


    private void initComp() {

        setContentView(R.layout.activity_review);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        reviewScrollView = (NestedScrollView) findViewById(R.id.activityReviewScrollView);

        reviewProgressBar = (ProgressBar)findViewById(R.id.reviewProgressBar);

        placeRatingBar = (RatingBar)findViewById(R.id.userRatingBar);
        placeRatingText = (TextView)findViewById(R.id.userRatingBarText);
        placeUserReviews = (TextView)findViewById(R.id.reviewTxtUserReview);
        placeBookmarks = (TextView)findViewById(R.id.reviewTxtBookmark);
        placeRecommend = (TextView)findViewById(R.id.reviewRecommend);
        reviewListMsg = (TextView)findViewById(R.id.txtReviewMessage);

        reviewScrollView.smoothScrollTo(0, 0);

        reviewList = (RecyclerView)findViewById(R.id.recycler_view);
        reviewInfoLayout = (CardView) findViewById(R.id.reviewInfoLayout);

        userPreference = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        userReviewList = new ArrayList<>();

        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Reviews");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        reviewList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        reviewList.setNestedScrollingEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.reviewFab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (reviewProgressBar.getVisibility() != View.VISIBLE) {

                    if (userPreference.getString("status", "").equals("Logged In")) {

                        ReviewDialogFragment reviewDialogFragment = new ReviewDialogFragment();

                        reviewDialogFragment.setOnReviewPost(new ReviewDialogFragment.OnReviewPost() {
                            @Override
                            public void reviewPost(String placeID) {
                                loadReviews(placeID);
                            }
                        });

                        reviewDialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                        reviewDialogFragment.show(getSupportFragmentManager(), "reviewDialogFragment");

                    } else {

                        new AlertDialog.Builder(ReviewActivity.this).setTitle("sign in")
                                                                    .setMessage("sign in to post a review")
                                                                    .setPositiveButton("sign in", new DialogInterface.OnClickListener() {

                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            Utilities.OpenActivity(getApplicationContext(),
                                                                                    LoginActivity.class,
                                                                                    null);
                                                                        }
                                                                    })
                                                                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            dialog.dismiss();

                                                                        }
                                                                    }).show();

                    }

                }

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComp();

        reviewList.setVisibility(View.GONE);
        reviewInfoLayout.setVisibility(View.GONE);

        placeID = getIntent().getStringExtra("placeID");
        loadReviews(getIntent().getStringExtra("placeID"));

    }

    public void loadReviews(final String placeID) {

        reviewScrollView.smoothScrollTo(0, 0);
        reviewListMsg.setVisibility(View.GONE);
        reviewList.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));

        RequestQueue requestReview = Volley.newRequestQueue(getApplicationContext());

        reviewProgressBar.setVisibility(View.VISIBLE);

        StringRequest requestString = new StringRequest(Request.Method.POST,
                "http://admin-spotter.000webhostapp.com/app_scripts/loadReview.php",
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        new ReviewGetData().execute(response);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {



            }
        }) {
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("placeID", placeID);
                }};
            }
        };

        requestReview.add(requestString);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.review_menu, menu);

        for (int menuCount = 0; menuCount != menu.size(); ++menuCount)
                menu.getItem(menuCount).setVisible(isHasReviews);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.sortByRating:

                reviewList.setVisibility(View.GONE);

                Collections.sort(userReviewList, new Comparator<UserReview>() {

                    @Override
                    public int compare(UserReview o1, UserReview o2) {
                        int returnVal = 0;

                        if(o1.getRating() < o2.getRating())
                            returnVal = 1;
                        else if(o1.getRating() > o2.getRating())
                            returnVal = -1;
                        else if(o1.getRating() == o2.getRating())
                            returnVal = 0;

                        return returnVal;
                    }
                });


                reviewScrollView.smoothScrollTo(0, 0);
                reviewList.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                reviewList.setVisibility(View.VISIBLE);
                reviewList.setAdapter(new SearchReviewsAdapter(getApplicationContext(), userReviewList));

                break;

            case R.id.sortByUser:

                reviewList.setVisibility(View.GONE);

                Collections.sort(userReviewList, new Comparator<UserReview>() {

                    @Override
                    public int compare(UserReview o1, UserReview o2) {
                        return o1.userName.compareTo(o2.userName);
                    }
                });

                reviewScrollView.smoothScrollTo(0, 0);
                reviewList.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                reviewList.setVisibility(View.VISIBLE);
                reviewList.setAdapter(new SearchReviewsAdapter(getApplicationContext(), userReviewList));

                break;

            case R.id.sortByDate:

                reviewScrollView.smoothScrollTo(0, 0);
                reviewList.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
                reviewList.setAdapter(new SearchReviewsAdapter(getApplicationContext(), userReviewList));

                break;

        }

        return super.onOptionsItemSelected(item);

    }

    class ReviewGetData extends AsyncTask<String, Void, List<UserReview>> {

        private float sumRating = 0;
        private int noOfUserReviews = 0;
        private int userRecommend = 0;
        private int userBookmark = 0;


        @Override
        protected List<UserReview> doInBackground(String... params) {

            try {

                List <UserReview> userReviewData = new ArrayList<>();

                    JSONObject jsonRawData = new JSONObject(params[0]);


                    JSONArray jsonArrData = jsonRawData.getJSONArray("userReviewData");

                    userBookmark = jsonRawData.getInt("bookmarkCount");
                    noOfUserReviews = jsonArrData.length();

                    for (int i = 0; i != jsonArrData.length(); ++i) {

                        JSONObject jsonElement = new JSONObject(jsonArrData.getString(i));

                        Log.d("debug", jsonArrData.getString(i));

                        UserReview userReview = new UserReview();

                        userReview.userName = jsonElement.getString("Username");
                        userReview.profileImage = Utilities.BlurImg.stringToBitmap(jsonElement.getString("Image"));

                        userReview.setDate(jsonElement.getString("DatePosted"));
                        userReview.setReview(jsonElement.getString("Review"));
                        userReview.setRating((float) jsonElement.getDouble("Rating"));
                        userReview.setRecommend(jsonElement.getBoolean("isRecommended"));


                        userReviewData.add(userReview);

                        sumRating += jsonElement.getDouble("Rating");

                        if (jsonElement.getString("isRecommended").equals("true"))
                            ++userRecommend;


                    }

                    sumRating /= noOfUserReviews;

                    return userReviewData;


            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<UserReview> userReviews) {
            super.onPostExecute(userReviews);

            if (userReviews != null) {

                userReviewList = userReviews;

                reviewInfoLayout.setVisibility(View.VISIBLE);

                Collections.reverse(userReviewList);

                prevUserReviewList = userReviewList;

                placeUserReviews.setText(noOfUserReviews + " user reviews");
                placeRecommend.setText(userRecommend + " recommend this");
                placeBookmarks.setText(userBookmark + " bookmarked this");
                placeRatingBar.setRating(sumRating);
                placeRatingText.setText(String.format( "%.1f", sumRating) + " Stars");

                reviewProgressBar.setVisibility(View.GONE);
                reviewList.setVisibility(View.VISIBLE);
                reviewListMsg.setVisibility(View.GONE);


                reviewList.setAdapter(new SearchReviewsAdapter(getApplicationContext(), userReviewList));

                for (UserReview userReview : userReviewList) {

                    Log.d("debug", "\n");

                    Log.d("debug", "username: " + userReview.userName);
                    Log.d("debug", "review: " + userReview.getReview());
                    Log.d("debug", "rating: " + userReview.getRating());
                    Log.d("debug", "isRecommended: " + userReview.isRecommend());
                    Log.d("debug", "Date posted: " + userReview.getDate());
                }

                isHasReviews = true;

            } else {
                reviewProgressBar.setVisibility(View.GONE);
                reviewListMsg.setVisibility(View.VISIBLE);

                isHasReviews = false;

            }

            invalidateOptionsMenu();

        }
    }


}
