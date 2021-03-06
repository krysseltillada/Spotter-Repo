package com.lmos.spotter.SearchInterface.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.maps.model.LatLng;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.AppScript;
import com.lmos.spotter.DbHelper;
import com.lmos.spotter.Item;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Fragments.FragmentSearchResult;
import com.lmos.spotter.SearchInterface.Fragments.FragmentSearchResultGeneral;
import com.lmos.spotter.SyncService;
import com.lmos.spotter.Utilities.Utilities;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

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

    static final int SHARE_REQUEST_CODE = 28;
    public static boolean isLinkClicked = false;
    /** Initialize views **/
    CallbackManager shareCallback;
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
    Place temp_place;
    static String name;
    LatLng userLocation;
    int tries = 0;
    boolean sync = true;
    private String fragmentType;

    private void startBackgroundHeaderFadeIn(){

        viewFlipperManager = (ViewFlipper) findViewById(R.id.viewFlipManager);

        viewFlipperManager.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in));
        viewFlipperManager.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out));
        viewFlipperManager.setFlipInterval(3000);

        viewFlipperManager.startFlipping();

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

                if (fragmentType.equals("Home")) {
                    loading_msg.setText(R.string.loading_mgs_3);
                    temp_place = fetch_intent.getParcelable("Place");
                }

                runTask(params);
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

        Log.d("debug", "size: " + menu.size());

        if (menu.size() < 2) {

            if (showBookmarkInAppBar)
                getMenuInflater().inflate(R.menu.bookmark_info, menu);

            toolbarMenu = menu;

        }

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
                        .placeholder(R.drawable.landscape_placeholder)
                        .into(prevImages[i]);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void switchFragment(final String type, String cmd, final Fragment fragment){

        Log.d("debug", "type: " + type );

        final int view_id = R.id.search_content_holder;

        final FragmentManager fragmentManager = getSupportFragmentManager();

        if (type.equals(""))
            viewFlipperManager.setVisibility(View.VISIBLE);

        if(cmd.equals("add")){

            fragmentManager.beginTransaction()
                    .add(view_id, fragment, type)
                    .commit();

        }
        else{

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    contentSettings(View.GONE, View.GONE, 0, getResources().getColor(R.color.colorPrimary));
                    toggleLoadingScreen(View.VISIBLE, AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                    fragmentManager.beginTransaction()
                            .addToBackStack("General")
                            .replace(view_id, fragment, type)
                            .commit();

                    contentSettings(View.VISIBLE, View.GONE, 200, getResources().getColor(R.color.blackTransparent));
                    toggleLoadingScreen(View.GONE, AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                    setShowBookmarkInAppBar(true);
                    nsview.smoothScrollTo(0,0);
                }
            });

        }

        Log.d("debug", String.valueOf(fragmentManager.getBackStackEntryCount()));

    }

    public void queryFavorites(String cmd, String args, Place place){

        SharedPreferences login_prefs = getSharedPreferences("LoginSharedPreference", MODE_PRIVATE);
        if(login_prefs.getString("accountID", null) != null){

            dbHelper = new DbHelper(this, this);
            if(cmd.equals("add")) {
                dbHelper.addToFavorites(place);
            }
            else
                dbHelper.deleteBookmark(new String[] { args });

        }
        else{
            Utilities.showDialogActivity(this, Utilities.REQUEST_CODES.LOGIN_REQUEST, R.string.not_sign);
        }

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

        new LoadSearchData().execute(params);

    }

    private void toggleLoadingScreen(final int loadingVisibility, Animation animation){

        final boolean activated = (loadingVisibility == View.GONE);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                /**if(activated) {
                    appBarLayout.setActivated(activated);
                    appBarLayout.setExpanded(activated, activated);
                    appBarLayout.setVerticalScrollBarEnabled(activated);
                }**/
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appBarLayout.setActivated(activated);
                appBarLayout.setExpanded(activated, activated);
                appBarLayout.setVerticalScrollBarEnabled(activated);
                nsview.setVisibility(View.VISIBLE);
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
        actionBarView.setVisibility(visibility);
        searchResultsTab.setVisibility(tab_visibility);
        nsview.smoothScrollTo(0,0);

        if(searchResultsTab.getVisibility() == View.GONE)
            value -= 50;

        // parameters
        collapsingToolbarLayout.setContentScrimColor(color);

        params.bottomMargin = value;
        toolbar.setLayoutParams(params);

    }

    private void initComp(){

        Log.d("debug", "initComp");

        shareCallback = CallbackManager.Factory.create();

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

    public void setTemp_place(Place place){ this.temp_place = place; }

    public void setShowBookmarkInAppBar(boolean val){
        showBookmarkInAppBar = val;
        invalidateOptionsMenu();
    }

    private void tweetPlaceOnTwitter (String link) {

        TweetComposer.Builder composeTweets = new TweetComposer.Builder(this)
                                                               .text(link);

        composeTweets.show();

    }

    private void sharePlaceOnFacebook (String link) {

        if (ShareDialog.canShow(ShareLinkContent.class)) {

            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(link))
                    .build();

            ShareDialog placeShare = new ShareDialog(SearchResultsActivity.this);

            placeShare.registerCallback(shareCallback, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {

                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("debug", error.getMessage());
                    Toast.makeText(getApplicationContext(), "shared failed", Toast.LENGTH_LONG).show();
                }
            }, SHARE_REQUEST_CODE);

            placeShare.show(linkContent);

        }

    }

    private void showShareDialog() {

        final Item[] items = {
                new Item("Facebook", R.drawable.facebook_icon),
                new Item("Twitter", R.drawable.twitter_icon)
        };

        ListAdapter adapter = new ArrayAdapter<Item>(
                this,
                android.R.layout.select_dialog_item,
                android.R.id.text1,
                items){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView)v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                /*
                int dp50 = (int) (50 * getResources().getDisplayMetrics().density + 0.5f);
                Drawable dr = getResources().getDrawable(items[position].icon);
                Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, dp50, dp50, true));
                tv.setCompoundDrawablesWithIntrinsicBounds(d, null, null, null);*/

                tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp5 = (int) (20 * getResources().getDisplayMetrics().density + 0.5f);
                tv.setCompoundDrawablePadding(dp5);

                return v;
            }
        };


        new AlertDialog.Builder(this)
                .setTitle("Share On")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, final int item) {

                        Place placeInfo = FragmentSearchResult.place;

                        String socialType = (item == 0) ? "facebook" : "twitter";

                        Utilities.generateLinkPlace(placeInfo, SearchResultsActivity.this,
                                new Utilities.OnGenerateLinkListener() {

                                    @Override
                                    public void OnGenerateLink(String generatedLink) {
                                        if (!generatedLink.equals("error")) {
                                            switch(item) {
                                                case 0:
                                                    sharePlaceOnFacebook(generatedLink);
                                                    break;
                                                case 1:
                                                    tweetPlaceOnTwitter(generatedLink);
                                                    break;
                                            }
                                        } else {
                                            Toast.makeText(getApplicationContext(), "please check your connection", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }, socialType);

                    }
                }).show();

    }

    /** End of activity methods **/

    /** Abstract Methods **/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:
                onBackPressed();
                setHeaderText(name);
                return true;

            case R.id.add_to_bookmark:
                queryFavorites("add", null, temp_place);
                break;

            case R.id.sharePlace:

                showShareDialog();

                break;


        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Log.d("debug", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
        Log.d("debug", "I AM ON BACKPRESSED");

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

            viewFlipperManager.setVisibility(View.INVISIBLE);

            if (searchResultsTab.getVisibility() != View.VISIBLE)
                searchResultsTab.setVisibility(View.VISIBLE);

            appBarLayout.setExpanded(true);
            nsview.smoothScrollTo(0, 0);
            setHeaderText(name);
            searchResultsTab.getTabAt(0).select();
            setShowBookmarkInAppBar(false);
            getSupportFragmentManager().popBackStack();

        } else {
            finish();
        }

    }

    @Override
    public void onDbResponse(String response, final String undo_id) {

        final String action_msg;

        if(response.equals("Place has been bookmarked. Synchronizing."))
            action_msg = "Undo";
        else
            action_msg = "Ok";

        Utilities.showSnackBar(
                findViewById(R.id.search_view_wrapper),
                response,
                Snackbar.LENGTH_LONG,
                action_msg,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(action_msg.equals("Undo")) {
                            queryFavorites("undo", undo_id, null);
                            sync = false;
                        }
                    }
                },
                new BaseTransientBottomBar.BaseCallback() {
                    @Override
                    public void onDismissed(Object transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                        if(action_msg.equals("Undo") && sync){
                            SharedPreferences login_prefs = getSharedPreferences("LoginSharedPreference", MODE_PRIVATE);
                            Intent bookmarkServer = new Intent(getApplicationContext(), SyncService.class);
                            bookmarkServer.putExtra("action", "save");
                            bookmarkServer.putExtra("accountID", login_prefs.getString("accountID", null));
                            bookmarkServer.putExtra("placeID", undo_id);
                            startService(bookmarkServer);
                            Fragment frag = getSupportFragmentManager().findFragmentByTag("");
                            if(frag != null && frag instanceof FragmentSearchResult)
                                ((FragmentSearchResult) frag).updateBookmark();
                        }

                    }
                }
        );

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("name", name);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        setHeaderText(savedInstanceState.getString("name"));
    }

    @Override
    public void onLocationFoundCity(String city) {

        if(city.equals("Service not Available"))
            loading_msg.setText("Error occurred while getting your location.\nPlease restart your device and try again");
        else
            runTask("General", city);

        locationHandler.changeApiState("disconnect");
    }

    @Override
    public void onLocationFoundLatLng(double lat, double lng, float bearing) {
        locationHandler.getLocality(lat, lng);
        userLocation = new LatLng(lat, lng);
        Log.d("LOCATION", String.valueOf(userLocation.latitude) + " " + String.valueOf(userLocation.longitude));
        locationHandler.stopLocationRequest();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        shareCallback.onActivityResult(requestCode, resultCode, data);

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
            case Utilities.REQUEST_CODES.LOGIN_REQUEST:

                if(resultCode == RESULT_OK)
                    startActivity(new Intent(this, LoginActivity.class));

                break;

        }

    }

    /** End of Abstract Methods **/

    /** AsyncTask **/

    private class LoadSearchData extends AsyncTask<String, String, String>{

        String type;
        String[] data;
        int toggleTab = View.VISIBLE;

        @Override
        protected String doInBackground(final String... params) {

            data = params;
            String result = "";

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
                case "Home":
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switchFragment("", "add", FragmentSearchResult.newInstance(temp_place));
                            setShowBookmarkInAppBar(true);
                            setHeaderText(data[1]);
                            contentSettings(
                                    View.VISIBLE,
                                    View.GONE,
                                    200,
                                    getResources().getColor(R.color.blackTransparent)
                            );
                        }
                    });
                    result = "Data loaded.";
                    break;
                default:
                    map_data.put("field_ref", "Name");
                    map_data.put("field_ref_val", params[1]);
                    break;

            }

            if(!type.equals("Home")){

                final AppScript appScript = new AppScript(activity){{
                    disconnect();
                    setData("searchPlaces.php", map_data);
                }};


                if(appScript.getResult() != null && appScript.getResult().equals("Data loaded.")){

                    final List<Place> placesList = appScript.getPlacesList();

                    if(!placesList.isEmpty()){

                        result = appScript.getResult();

                        if(userLocation != null){

                            for(Place index : placesList){

                                index.setDistance(
                                        String.valueOf(
                                                Utilities.CalculationByDistance(
                                                        userLocation,
                                                        new LatLng(
                                                                Double.parseDouble(index.getPlaceLat()),
                                                                Double.parseDouble(index.getPlaceLng())
                                                        )
                                                )
                                        )
                                );

                            }

                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if(type.equals("General") || type.equals("Undefined"))
                                    switchFragment("General", "add", FragmentSearchResultGeneral.newInstance("Hotel", placesList));
                                else if(type.equals("Hotel") || type.equals("Restaurant") || type.equals("Tourist Spot")){

                                    switchFragment("", "add", FragmentSearchResult.newInstance(placesList.get(0)));
                                    temp_place = placesList.get(0);
                                    setShowBookmarkInAppBar(true);
                                    toggleTab = View.GONE;

                                }
                                if(type.equals("Undefined")){
                                    setShowBookmarkInAppBar(false);
                                    setHeaderText("Results for " + data[1]);
                                }
                                else
                                    setHeaderText(data[1]);

                                contentSettings(
                                        View.VISIBLE,
                                        toggleTab,
                                        (type.equals("General")) ?
                                            150 : (type.equals("Undefined")) ? 100 : 270,
                                        getResources().getColor(R.color.blackTransparent)
                                );

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

            }

            return result;

        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("SEARCH", result);

            switch (result){

                case "Data loaded.":
                    toggleLoadingScreen(View.GONE, AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out));
                    break;
                case "No result found.":
                    loading_msg.setText(result);
                    break;
                default:
                    loading_img.setImageResource(android.R.drawable.stat_notify_error);
                    if(tries < 5){
                        tries ++;
                        loading_msg.setText(result + " Retrying(" + tries + ")");
                        new LoadSearchData().execute(data);
                    }
                    else
                        loading_msg.setText(R.string.loading_msg_4);
                    break;

            }

        }
    }
    /** End of AsycTask **/
}