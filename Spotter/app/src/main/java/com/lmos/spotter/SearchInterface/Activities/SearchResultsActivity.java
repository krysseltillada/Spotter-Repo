package com.lmos.spotter.SearchInterface.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lmos.spotter.FavoritesDbHelper;
import com.lmos.spotter.MapsLayoutFragment;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Adapters.SearchReviewsAdapter;
import com.lmos.spotter.SearchInterface.Fragments.FragmentSearchResult;
import com.lmos.spotter.SearchInterface.Fragments.FragmentSearchResultGeneral;
import com.lmos.spotter.Utilities.Utilities;

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
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView place_name, place_content_desc;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SearchReviewsAdapter mAdapter;
    View actionBarView;
    FavoritesDbHelper favoritesDbHelper;
    RelativeLayout loading_screen, desc_tab_holder;
    AppBarLayout appBarLayout;
    NestedScrollView nsview;
    ImageView loading_img;
    TextView loading_msg, loading_error_msg;
    TabLayout searchResultsTab;
    CoordinatorLayout coordinatorLayout;
    /** End of initializing views **/

    boolean isLastFragment = false;

    Activity activity = this;
    Utilities.LocationHandler locationHandler = new Utilities.LocationHandler(this, this);
    private String type;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);

        initComp();
        Utilities.setSearchBar(this, actionBarView);
        locationHandler.buildGoogleClient();
        headerSettings("default");

        Bundle fetch_intent = getIntent().getExtras();
        type = fetch_intent.getString("type");
        switchFragment(fetch_intent.getString("type"), "add", "");

    }

    @Override
    protected void onStart() {
        super.onStart();
        locationHandler.changeApiState("connect");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(!type.equals("Location"))
            getMenuInflater().inflate(R.menu.bookmark_info, menu);

        return super.onCreateOptionsMenu(menu);

    }

    private void setHeaderText(String name, String description){
        place_name.setText(name);
        place_content_desc.setText(description);
    }

    private void fadeInView () {
        coordinatorLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                                                                    android.R.anim.fade_in));
    }

    public void switchFragment(String type, String cmd, String... params){

        final Fragment fragment;
        int view_id = R.id.search_content_holder;

        if (!type.equals("Map"))
            fadeInView();

        switch (type){

            case "place":
                searchResultsTab.setVisibility(View.GONE);
                setHeaderText("City of Dreams", "Nightmares it is");
                fragment = FragmentSearchResult.newInstance(params);
                loading_screen.setVisibility(View.GONE);

                break;

            case "Map":
                Log.d("debug", "Map");
                fragment = MapsLayoutFragment.newInstance(12.8797, 121.7740);
                view_id = R.id.map_content_holder;
                break;

            default:
                cmd = "general";
                searchResultsTab.setVisibility(View.VISIBLE);
                setHeaderText("Batangas", "Bayan ng magigiting");
                fragment = new FragmentSearchResultGeneral();
                loading_screen.setVisibility(View.GONE);


                break;
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();

        if(cmd.equals("add")){

            fragmentManager.beginTransaction()
                    .add(view_id, fragment, type)
                    .commit();

        }
        else{

            fragmentManager.beginTransaction()
                    .replace(view_id, fragment, type)
                    .addToBackStack("General")
                    .commit();

        }

    }


    private void addToFavorites(){

        favoritesDbHelper = new FavoritesDbHelper(this, this);
        Log.d("ADD", "Triggered");
        favoritesDbHelper.addToFavorites();

    }

    private void headerSettings(String toggle){

        CollapsingToolbarLayout.LayoutParams params = (CollapsingToolbarLayout.LayoutParams) toolbar.getLayoutParams();

        switch (toggle){

            case "hide":
                desc_tab_holder.setVisibility(View.GONE);
                params.bottomMargin = 0;
                collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
                appBarLayout.setExpanded(false);
                appBarLayout.setActivated(false);
                nsview.setVisibility(View.GONE);
                break;
            case "show":
                desc_tab_holder.setVisibility(View.VISIBLE);
                params.bottomMargin = 200;
                collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.blackTransparent));
                appBarLayout.setExpanded(true, true);
                appBarLayout.setActivated(true);
                loading_screen.setVisibility(View.GONE);
                loading_screen.startAnimation(AnimationUtils.loadAnimation(
                        this,
                        R.anim.fade_out
                ));
                break;
            default:
                params.bottomMargin = 200;
                break;

        }

        toolbar.setLayoutParams(params);

    }

    private void initComp(){

        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.search_view_wrapper);
        searchResultsTab = (TabLayout)findViewById(R.id.search_tab_layout);
        loading_screen = (RelativeLayout) findViewById(R.id.loading_screen);
        desc_tab_holder = (RelativeLayout) findViewById(R.id.description_tab_holder);
        nsview = (NestedScrollView) findViewById(R.id.search_nsview);

        loading_img = (ImageView) findViewById(R.id.loading_img_holder);
        loading_msg = (TextView) findViewById(R.id.loading_msg);
        loading_error_msg = (TextView) findViewById(R.id.loading_error_msg);

        /*

        boolean isPlayServicesAvailable = Utilities.checkPlayServices(this, new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                onBackPressed();
            }

        });

        if (isPlayServicesAvailable) {

            Utilities.loadGifImageView(this, loading_img, R.drawable.loadingplaces);
            loading_msg.setText("Hi! We're getting your location. Make sure you have a stable internet connection.");

        } */

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
        switchFragment("Map", "add", "");

        setSupportActionBar(toolbar);

        //Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.add_to_bookmark:
                addToFavorites();
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
                        headerSettings("show");
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

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {

            if (searchResultsTab.getVisibility() != View.VISIBLE)
                searchResultsTab.setVisibility(View.VISIBLE);


            fadeInView();

            getSupportFragmentManager().popBackStack();

        } else {
            finish();
        }

    }

    @Override
    public void onLocationFound(String location) {

        Toast.makeText(getApplicationContext(), location, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDbResponse(String response) {
        Utilities.showSnackBar(
                findViewById(R.id.search_view_wrapper),
                response,
                Snackbar.LENGTH_SHORT,
                "Undo",
                null
        );
    }
}