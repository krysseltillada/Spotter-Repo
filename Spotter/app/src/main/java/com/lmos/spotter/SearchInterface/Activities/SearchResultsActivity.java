package com.lmos.spotter.SearchInterface.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.lmos.spotter.AppScript;
import com.lmos.spotter.DbHelper;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Fragments.FragmentSearchResult;
import com.lmos.spotter.SearchInterface.Fragments.FragmentSearchResultGeneral;
import com.lmos.spotter.Utilities.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by linker on 02/06/2017.
 *
 * This class will display the result/s of places
 * and/or specific hotels, restaurants and tourist spots.
 *
 */

public class SearchResultsActivity extends AppCompatActivity
    implements
        Utilities.OnDbResponseListener,
        Utilities.OnLocationFoundListener {

    /** Initialize views **/
    ViewFlipper viewFlipperManager;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView place_name, place_address;
    View actionBarView;
    DbHelper dbHelper;
    RelativeLayout loading_screen, desc_tab_holder;
    AppBarLayout appBarLayout;
    NestedScrollView nsview;
    ImageView loading_img;
    TextView loading_msg;
    TabLayout searchResultsTab;
    Menu toolbarMenu;
    CoordinatorLayout coordinatorLayout;
    /** End of initializing views **/

    boolean showBookmarkInAppBar = false;

    Utilities.LocationHandler locationHandler;

    Activity activity = this;
    String name;
    int tries = 0;
    private String fragmentType;

    private void startBackgroundHeaderFadeIn(){

        viewFlipperManager = (ViewFlipper) findViewById(R.id.viewFlipManager);


        viewFlipperManager.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        viewFlipperManager.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
        viewFlipperManager.setFlipInterval(3000);

        viewFlipperManager.startFlipping();

    }

    public void showReviewActivity (View view) {

        Bundle placeID = new Bundle();

        placeID.putString("placeID", FragmentSearchResultGeneral.placeID);

        Utilities.OpenActivityWithBundle(activity, ReviewActivity.class, null, placeID);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("debug", "onCreate SearchResults");

        setContentView(R.layout.activity_search_results);

        initComp();

        startBackgroundHeaderFadeIn();

        Utilities.setSearchBar(this, actionBarView);

        toggleLoadingScreen(View.VISIBLE, AnimationUtils.loadAnimation(this, R.anim.fade_in));
        contentSettings(
                View.GONE,
                View.VISIBLE,
                0,
                getResources().getColor(R.color.colorPrimary)
        );

        final Bundle fetch_intent = getIntent().getExtras();
        final String[] params = fetch_intent.getStringArray("data");

        if (params != null) {
            // First index of params represents the type.
            name = params[1];
            setHeaderText(params[1]);
            fragmentType = params[0];
            if(fragmentType.equals("Location")){
                boolean isPlayServicesAvailable = Utilities.checkPlayServices(this, new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onBackPressed();
                    }

                });

                if (isPlayServicesAvailable) {
                    locationHandler = new Utilities.LocationHandler(this, this);
                    locationHandler.buildGoogleClient();
                    Utilities.loadGifImageView(this, loading_img, R.drawable.loadingplaces);
                    loading_msg.setText(R.string.loading_msg_1);
                }

            }
            else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (fragmentType.equals("Home")) {
                            loading_msg.setText(R.string.loading_msg_2);
                            setHeaderText(params[1]);
                            Place place = fetch_intent.getParcelable("Place");
                            switchFragment("", "add", FragmentSearchResult.newInstance(place));
                            contentSettings(
                                    View.VISIBLE,
                                    View.GONE,
                                    200,
                                    getResources().getColor(R.color.blackTransparent)
                            );
                            toggleLoadingScreen(View.GONE, AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                        }
                        else {
                            runTask(params);
                        }
                    }
                });

            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(fragmentType.equals("Location"))
            locationHandler.changeApiState("connect");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(showBookmarkInAppBar)
            getMenuInflater().inflate(R.menu.bookmark_info, menu);

        toolbarMenu = menu;

        return super.onPrepareOptionsMenu(menu);
    }

    /** Activity methods **/

    public void setHeaderText(String name){
        place_name.setText(name);
    }

    public void setHeaderImg(String imageLink){

        try{

            JSONObject json_imgLnk = new JSONObject(imageLink);
            JSONObject json_placeImg = json_imgLnk.getJSONObject("placeImages");
            JSONArray json_prevImg = json_placeImg.getJSONArray("previewImages");

            ImageView[] prevImages = {
                    (ImageView) findViewById(R.id.placePreviewImage1),
                    (ImageView) findViewById(R.id.placePreviewImage2),
                    (ImageView) findViewById(R.id.placePreviewImage3)
            };

            for(int i = 0; i < json_prevImg.length(); i ++){

                Picasso.with(this)
                        .load(json_prevImg.get(i).toString())
                        .placeholder(R.drawable.loadingplace)
                        .into(prevImages[i]);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void switchFragment(final String type, String cmd, final Fragment fragment){

        final int view_id = R.id.search_content_holder;

        final FragmentManager fragmentManager = getSupportFragmentManager();

        if(cmd.equals("add")){

            fragmentManager.beginTransaction()
                    .add(view_id, fragment, type)
                    .commit();

        }
        else{

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    toggleLoadingScreen(View.VISIBLE, AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                    fragmentManager.beginTransaction()
                            .addToBackStack("General")
                            .replace(view_id, fragment, type)
                            .commit();

                    searchResultsTab.setVisibility(View.GONE);
                    toggleLoadingScreen(View.GONE, AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                    setShowBookmarkInAppBar(true);
                    nsview.smoothScrollTo(0,0);
                }
            });

        }

        Log.d("debug", String.valueOf(fragmentManager.getBackStackEntryCount()));

    }

    public void queryFavorites(String cmd, String args, Place place){

        dbHelper = new DbHelper(this, this);

        if(cmd.equals("add")) {
            dbHelper.addToFavorites(place);
        }
        else
            dbHelper.deleteBookmark(new String[] { args });

    }

    public void runTask(String... params){

        Utilities.loadGifImageView(this, loading_img, R.drawable.loadingplaces);
        loading_msg.setText(R.string.loading_msg_2);
        toggleLoadingScreen(View.VISIBLE, AnimationUtils.loadAnimation(this, R.anim.fade_in));
        contentSettings(
                View.GONE,
                View.VISIBLE,
                0,
                getResources().getColor(R.color.colorPrimary)
        );

        if(Utilities.checkNetworkState(this))
            new LoadSearchData().execute(params);
        else
            loading_msg.setText(R.string.loading_msg_5);

    }

    private void toggleLoadingScreen(final int loadingVisibility, Animation animation){

        final boolean activated = (loadingVisibility == View.GONE);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                appBarLayout.setActivated(activated);
                appBarLayout.setExpanded(activated, activated);
                appBarLayout.setVerticalScrollBarEnabled(activated);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appBarLayout.setActivated(activated);
                appBarLayout.setExpanded(activated, activated);
                appBarLayout.setVerticalScrollBarEnabled(activated);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing.
            }
        });
        loading_screen.setVisibility(loadingVisibility);
        loading_screen.startAnimation(animation);

    }

    private void contentSettings(int visibility, int tab_visibility, int value, int color){

        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();

        // visibility
        desc_tab_holder.setVisibility(visibility);
        nsview.setVisibility(visibility);
        actionBarView.setVisibility(visibility);
        searchResultsTab.setVisibility(tab_visibility);

        if(searchResultsTab.getVisibility() == View.GONE)
            value -= 50;

        // parameters
        collapsingToolbarLayout.setContentScrimColor(color);

        params.bottomMargin = value;
        toolbar.setLayoutParams(params);

    }

    private void initComp(){

        Log.d("debug", "initComp");

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.search_view_wrapper);
        searchResultsTab = (TabLayout)findViewById(R.id.search_tab_layout);
        loading_screen = (RelativeLayout) findViewById(R.id.loading_screen);
        desc_tab_holder = (RelativeLayout) findViewById(R.id.description_tab_holder);
        nsview = (NestedScrollView) findViewById(R.id.search_nsview);

        loading_img = (ImageView) findViewById(R.id.loading_img_holder);
        loading_msg = (TextView) findViewById(R.id.loading_msg);

        searchResultsTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("Tab", tab.getText().toString());

                //FragmentSearchResultGeneral.changeAdapter(tab.getText().toString());

                Fragment generalFragment = getSupportFragmentManager().findFragmentByTag("General");

                if(generalFragment != null && generalFragment instanceof FragmentSearchResultGeneral)
                    ((FragmentSearchResultGeneral) generalFragment).changeAdapter(tab.getText().toString());

                nsview.smoothScrollTo(0,0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        searchResultsTab.getTabAt(0).select();

        /** Set app bar layout, toolbar and collapsing toolbar for SearchResultHeader **/

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBarView = layoutInflater.inflate(R.layout.searchbar, null);

        toolbar = (Toolbar) findViewById(R.id.action_bar_toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        place_name = (TextView) findViewById(R.id.place_name);
        place_address = (TextView) findViewById(R.id.place_address);

        //Set collapse & expanded title color
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));

        //Set the title on collapsing toolbar
        collapsingToolbarLayout.setTitle("");

        /** End of setting header **/

        setSupportActionBar(toolbar);

        //Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    public void setShowBookmarkInAppBar(boolean val){
        showBookmarkInAppBar = val;
        invalidateOptionsMenu();
    }

    /** End of activity methods **/

    /** Abstract Methods **/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.add_to_bookmark:
             //   addToFavorites();
                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Log.d("debug", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            if (searchResultsTab.getVisibility() != View.VISIBLE)
                searchResultsTab.setVisibility(View.VISIBLE);

            appBarLayout.setExpanded(true);
            nsview.smoothScrollTo(0, 0);
            setHeaderText(name);
            searchResultsTab.getTabAt(0).select();

            getSupportFragmentManager().popBackStack();

        } else {
            finish();
        }

    }

    @Override
    public void onDbResponse(String response, final String undo_id) {
        Utilities.showSnackBar(
                findViewById(R.id.search_view_wrapper),
                response,
                Snackbar.LENGTH_LONG,
                "Undo",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queryFavorites("undo", undo_id, null);
                    }
                }
        );
    }

    @Override
    public void onLocationFoundCity(String city) {
        //new LoadSearchData().execute("General", city);
        runTask("General", city);
        locationHandler.changeApiState("disconnect");
    }

    @Override
    public void onLocationFoundLatLng(double lat, double lng, float bearing) {
        locationHandler.getLocality(lat, lng);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case Utilities.REQUEST_CODES.CHECK_SETTING_REQUEST_CODE:
                switch (resultCode){

                    case RESULT_OK:
                        return;
                    case RESULT_CANCELED:
                        if(loading_screen.getVisibility() == View.VISIBLE)
                            loading_msg.setText(R.string.loading_msg_3);

                        return;
                }
                break;
        }

    }

    /** End of Abstract Methods **/

    /** AsyncTask **/

    private class LoadSearchData extends AsyncTask<String, String, String>{

        String type;
        String[] data;

        @Override
        protected String doInBackground(String... params) {

            data = params;

            final Map<String, String> map_data = new HashMap<String, String>();

            type = params[0];

            switch (type){

                case "General":
                    map_data.put("field_ref", "Locality");
                    map_data.put("field_ref_val", params[1]);
                    break;
                case "Undefined":
                    map_data.put("field_ref", "Undefined");
                    map_data.put("field_ref_val", params[1]);
                    break;
                default:
                    map_data.put("field_ref", "Name");
                    map_data.put("field_ref_val", params[1]);
                    break;

            }
            Log.d("LOG", "connecting");
            AppScript appScript = new AppScript(activity){{
               setData("searchPlaces.php", map_data);
            }};

            String result = "";

            if(appScript.getResult() != null && appScript.getResult().equals("Data loaded.")){

                final List<Place> places = appScript.getPlacesList();

                if(!places.isEmpty()){

                    result = appScript.getResult();

                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int toggleTab = View.VISIBLE;
                                if(type.equals("General") || type.equals("Undefined"))
                                    switchFragment("General", "add", FragmentSearchResultGeneral.newInstance("Hotel", places));
                                else if(type.equals("Hotel") || type.equals("Restaurant") || type.equals("Tourist Spot")){
                                    switchFragment("", "add", FragmentSearchResult.newInstance(places.get(0)));
                                    setShowBookmarkInAppBar(true);
                                    toggleTab = View.GONE;
                                }

                                if(type.equals("Undefined")){
                                    setShowBookmarkInAppBar(false);
                                    contentSettings(
                                            View.VISIBLE,
                                            toggleTab,
                                            70,
                                            getResources().getColor(R.color.colorPrimary)
                                    );
                                    place_name.setVisibility(View.GONE);
                                }
                                else{
                                    setHeaderText(data[1]);
                                    contentSettings(
                                            View.VISIBLE,
                                            toggleTab,
                                            200,
                                            getResources().getColor(R.color.blackTransparent)
                                    );
                                }

                            }
                        });

                    }
                    else {
                        result = "No result found.";
                    }

                }
                else{
                    result = appScript.getResult();
                }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            /**if(!(result != null && result.equals("Data loaded."))){
                loading_img.setImageResource(android.R.drawable.stat_notify_error);
                if(tries < 5){
                    tries ++;
                    loading_msg.setText(result + " Retrying(" + tries + ")");
                    new LoadSearchData().execute(data);
                }
                else
                    loading_msg.setText(R.string.loading_msg_4);
            }
            else{ **/
                toggleLoadingScreen(View.GONE, AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
            //}
        }
    }
    /** End of AsycTask **/
}