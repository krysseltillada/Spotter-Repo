package com.lmos.spotter.SearchInterface.Activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.lmos.spotter.AppScript;
import com.lmos.spotter.DbHelper;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Adapters.SearchReviewsAdapter;
import com.lmos.spotter.SearchInterface.Fragments.FragmentSearchResultGeneral;
import com.lmos.spotter.Utilities.Utilities;

import java.util.ArrayList;
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
    Utilities.OnLocationFoundListener,
    Utilities.OnDbResponseListener{

    /** Initialize views **/
    ViewFlipper viewFlipperManager;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView place_name, place_content_desc;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SearchReviewsAdapter mAdapter;
    View actionBarView;
    DbHelper dbHelper;
    RelativeLayout loading_screen, desc_tab_holder;
    AppBarLayout appBarLayout;
    NestedScrollView nsview;
    ImageView loading_img;
    TextView loading_msg, loading_error_msg;
    TabLayout searchResultsTab;
    Menu toolbarMenu;
    CoordinatorLayout coordinatorLayout;
    Button showAllSearchResults;
    Button navigate;
    /** End of initializing views **/

    boolean isLocationFragment = false;

    Activity activity = this;
    Utilities.LocationHandler locationHandler = new Utilities.LocationHandler(this, this);
    private String fragmentType;

    private void startBackgroundHeaderFadeIn(){

        viewFlipperManager = (ViewFlipper) findViewById(R.id.viewFlipManager);


        viewFlipperManager.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        viewFlipperManager.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
        viewFlipperManager.setFlipInterval(3000);

        viewFlipperManager.startFlipping();

    }

    public void showReviewActivity (View view) {
        Utilities.OpenActivity(activity, ReviewActivity.class, null);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("debug", "onCreate SearchResults");

        setContentView(R.layout.activity_search_results);

        initComp();

        startBackgroundHeaderFadeIn();

        Utilities.setSearchBar(this, actionBarView);

        locationHandler.buildGoogleClient();

        contentSettings(
                View.VISIBLE,
                View.GONE,
                0,
                getResources().getColor(R.color.colorPrimary),
                false
        );

        Bundle fetch_intent = getIntent().getExtras();
        String[] params = fetch_intent.getStringArray("data");

        if (params != null) {
            // First index of params represents the type.
            setHeaderText(params[1], params[2]);
            fragmentType = params[0];
            if(fragmentType.equals("Location")){
                boolean isPlayServicesAvailable = Utilities.checkPlayServices(this, new DialogInterface.OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        onBackPressed();
                    }

                });

                if (isPlayServicesAvailable) {
                    Utilities.loadGifImageView(this, loading_img, R.drawable.loadingplaces);
                    loading_msg.setText("Hi! We're getting your location. Make sure you have a stable internet connection.");
                }

            }
            else{
                Utilities.loadGifImageView(this, loading_img, R.drawable.loadingplaces);
                loading_msg.setText("Getting some information for you.");
            }

            new LoadSearchData().execute(params);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        appBarLayout.setExpanded(true);
        nsview.smoothScrollTo(0, 0);
        recyclerView.smoothScrollToPosition(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        locationHandler.changeApiState("connect");
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(!fragmentType.equals("Location"))
            getMenuInflater().inflate(R.menu.bookmark_info, menu);

        toolbarMenu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    /** Activity methods **/

    private void setHeaderText(String name, String description){
        place_name.setText(name);
        place_content_desc.setText(description);
    }

    private void fadeInView () {
        coordinatorLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                                                    android.R.anim.fade_in));
    }

    public void switchFragment(String type, String cmd, Fragment fragment){

        int view_id = R.id.search_content_holder;

        appBarLayout.setExpanded(true);
        nsview.smoothScrollTo(0, 0);
        recyclerView.smoothScrollToPosition(0);

        if (!type.equals("Map"))
            fadeInView();

        final FragmentManager fragmentManager = getSupportFragmentManager();

        if(cmd.equals("add")){

            Log.d("debug", "add");

            fragmentManager.beginTransaction()
                    .add(view_id, fragment, type)
                    .commit();

        }
        else{

            Log.d("debug", "replace");

            fragmentManager.beginTransaction()
                    .addToBackStack("General")
                    .replace(view_id, fragment, type)
                    .commit();

        }

        Log.d("debug", String.valueOf(fragmentManager.getBackStackEntryCount()));

    }

    public void queryFavorites(String cmd, String args, Place place){

        dbHelper = new DbHelper(this, this);
        dbHelper.addToFavorites(place);
        if(cmd.equals("add"))
            dbHelper.addToFavorites(place);
        else
            dbHelper.deleteBookmark(args);

    }

    private void contentSettings(int loading_visibility, int visibility, int value, int color, boolean prop_value){

        Log.d("debug", "headerSettings");

        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();
        params.bottomMargin = value;

        // visibility
        desc_tab_holder.setVisibility(visibility);
        nsview.setVisibility(visibility);
        loading_screen.setVisibility(loading_visibility);
        actionBarView.setVisibility(View.GONE);

        // parameters
        collapsingToolbarLayout.setContentScrimColor(color);

        if(loading_visibility == View.GONE){
            appBarLayout.setExpanded(prop_value, prop_value);
            loading_screen.startAnimation(AnimationUtils.loadAnimation(
                    this,
                    R.anim.fade_out
            ));

        }
        else
            appBarLayout.setExpanded(prop_value);

        appBarLayout.setActivated(prop_value);
        toolbar.setLayoutParams(params);

    }

    private void initComp(){

        Log.d("debug", "initComp");

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.search_view_wrapper);
        searchResultsTab = (TabLayout)findViewById(R.id.search_tab_layout);
        loading_screen = (RelativeLayout) findViewById(R.id.loading_screen);
        desc_tab_holder = (RelativeLayout) findViewById(R.id.description_tab_holder);
        nsview = (NestedScrollView) findViewById(R.id.search_nsview);

        showAllSearchResults = (Button)findViewById(R.id.showAllSearchResults);

        loading_img = (ImageView) findViewById(R.id.loading_img_holder);
        loading_msg = (TextView) findViewById(R.id.loading_msg);
        loading_error_msg = (TextView) findViewById(R.id.loading_error_msg);

        navigate = (Button)findViewById(R.id.btnNavigate);

        navigate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog wazePromptDialog = new AlertDialog.Builder(
                        SearchResultsActivity.this).setTitle("launch waze?")
                        .setMessage("Be sure that waze is installed if not the app will link to the play store to install it")
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try
                                        {
                                            String url = "waze://?ll=13.7565,121.0583&navigate=yes";
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                            startActivity(intent);
                                        }
                                        catch ( ActivityNotFoundException ex  )
                                        {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                                            startActivity(intent);
                                        }


                                    }
                                }).setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {dialog.dismiss();
                                    }
                                }).create();

                wazePromptDialog.show();

            }

        });

        boolean isPlayServicesAvailable = Utilities.checkPlayServices(this, new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                onBackPressed();
            }

        });

        if (isPlayServicesAvailable) {

            Utilities.loadGifImageView(this, loading_img, R.drawable.loadingplaces);
            loading_msg.setText("Hi! We're getting your location. Make sure you have a stable internet connection.");

        }


        /** Set app bar layout, toolbar and collapsing toolbar for SearchResultHeader **/

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBarView = layoutInflater.inflate(R.layout.searchbar, null);

        toolbar = (Toolbar) findViewById(R.id.action_bar_toolbar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        place_name = (TextView) findViewById(R.id.place_name);
        place_content_desc = (TextView) findViewById(R.id.place_content_description);

        //Set collapse & expanded title color
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));

        //Set the title on collapsing toolbar
        collapsingToolbarLayout.setTitle("");

        /** End of setting header **/

        /** Set RecyclerView for user reviews. **/
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new SearchReviewsAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        /** End setting RecyclerView **/

        // Inflate map into Framelayout
        //switchFragment("Map", "add", );

        setSupportActionBar(toolbar);

        //Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    /** End of activity methods **/

    /** AsyncTask **/

    private class LoadSearchData extends AsyncTask<String, String, List<Place>>{

        @Override
        protected List<Place> doInBackground(String... params) {

            final Map<String, String> map_data = new HashMap<String, String>();

            switch (params[0]){

                case "General":
                    map_data.put("field_ref", "Locality");
                    map_data.put("field_ref_val", params[1]);
                    break;
                case "Location":
                    break;
                default:
                    map_data.put("field_ref", "Name");
                    map_data.put("field_ref_val", params[1]);
                    break;

            }

            AppScript appScript = new AppScript(){{
                setData("get-all-places.php", map_data);
            }};

            String result =  appScript.getResult();
            List<Place> place = null;
            if(result != null && result.equals("Data loaded."))
                place = new ArrayList<Place>(appScript.getPlacesList());

            return place;
        }

        @Override
        protected void onPostExecute(List<Place> places) {
            super.onPostExecute(places);

            loading_screen.setVisibility(View.GONE);
            contentSettings(
                    View.GONE,
                    View.VISIBLE,
                    200,
                    getResources().getColor(R.color.blackTransparent),
                    true
            );
            switchFragment("", "add", FragmentSearchResultGeneral.newInstance(places));

        }
    }

    /** End of AsycTask **/

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case Utilities.REQUEST_CODES.LOCATION_REQUEST_CODE:

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    locationHandler.findLocation();
                }
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case Utilities.REQUEST_CODES.CHECK_SETTING_REQUEST_CODE:
                switch (resultCode){

                    case RESULT_OK:
                        Log.d("LocationHandler", "Permission granted");
                        /*if(!Utilities.checkNetworkState(activity))
                        {
                            Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                            startActivity(intent);
                        }*/
                        contentSettings(
                                View.GONE,
                                View.VISIBLE,
                                200,
                                getResources().getColor(R.color.blackTransparent),
                                true
                        );
                        return;
                    case RESULT_CANCELED:
                        loading_error_msg.setText("Sorry, but we cannot detect your location unless you enable your GPS and either your Mobile Data or Wi-Fi.");
                        loading_error_msg.setVisibility(View.VISIBLE);
                        loading_img.setVisibility(View.GONE);
                        loading_msg.setVisibility(View.GONE);
                        return;
                }
                break;

        }

    }

    @Override
    public void onBackPressed() {

        Log.d("debug", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            if (searchResultsTab.getVisibility() != View.VISIBLE)
                searchResultsTab.setVisibility(View.VISIBLE);

            appBarLayout.setExpanded(true);
            nsview.smoothScrollTo(0, 0);
            recyclerView.smoothScrollToPosition(0);
            showAllSearchResults.setVisibility(View.VISIBLE);
            ((FrameLayout)showAllSearchResults.getParent()).setVisibility(View.VISIBLE);

            fadeInView();

            getSupportFragmentManager().popBackStack();

        } else {
            finish();
        }

    }

    @Override
    public void onLocationFoundCity(String location) {



    }

    @Override
    public void onLocationFoundLatLng(double lat, double lng) {

        /**if (isLocationFragment)
            switchFragment("", "add", null);

        MapsLayoutFragment mapsLayoutFragment = (MapsLayoutFragment)getSupportFragmentManager().findFragmentByTag("Map");

        if (mapsLayoutFragment != null)
            mapsLayoutFragment.setUserPosition(new LatLng(lat, lng));
**/
    }

    @Override
    public void onDbResponse(String response, final String undo_id) {
        Utilities.showSnackBar(
                findViewById(R.id.search_view_wrapper),
                response,
                Snackbar.LENGTH_SHORT,
                "Undo",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        queryFavorites("undo", undo_id, null);
                    }
                }
        );
    }

    /** End of Abstract Methods **/
}