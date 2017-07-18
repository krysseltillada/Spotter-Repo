package com.lmos.spotter.MainInterface.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.AppScript;
import com.lmos.spotter.MainInterface.Adapters.MainInterfaceAdapter;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.PlaceType;
import com.lmos.spotter.Utilities.Utilities;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public interface OnRespondError {
        void onRespondError(String error);
    }

    private NestedScrollView homeNestedScrollView;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private DrawerLayout drawerLayout;
    private View actionBarView;
    private TextView txtHome;
    private SearchView searchBTN;
    private FloatingActionButton floatingActionButton;
    private AppBarLayout appBarLayout;
    private MainInterfaceAdapter mainInterfaceAdapter;
    private RecyclerView tabLayoutRecyclerView;
    private ProgressBar itemListProgressBar;
    private ProgressBar recycleViewProgressBar;
    private TabLayout tabLayout;
    private TextView mostPopularName;

    private ImageView userProfileImage;
    private TextView userName;
    private TextView userEmail;

    private int startingIndex;
    private int tableCount;

    private String placeType;
    private Activity activity = this;
    private ActionBarDrawerToggle drawerToggle;

    private List<Place> placeDataList;

    private ViewFlipper viewFlipperManager;
    private LinearLayout backgroundMostPopular;
    private ImageView[] mostPopularImages;
    private String[] mostPopularNames;

    private SwipeRefreshLayout pullUpLoadLayout;

    private AdView bannerAdView;

    String pageCount = "10";

    SharedPreferences userData;

    private InterstitialAd interstitialAd;

    private boolean isLoadingItem = false;
    private boolean isLoadingPlace = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        placeDataList = new ArrayList<>();

        mostPopularImages = new ImageView[3];
        mostPopularNames = new String[3];

        initComp();


        getMostPopular("General");

        loadPlacesByType("General");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        displayUserInfo();
    }


    public void userNavDropDown(View view) {

        PopupMenu userNavDropDownMenu = new PopupMenu(HomeActivity.this, view);

        if (userData.getString("status", "").equals("Logged In")) {

            userNavDropDownMenu.getMenuInflater().inflate(R.menu.popupmenu_login, userNavDropDownMenu.getMenu());

        } else {
            userNavDropDownMenu.getMenuInflater().inflate(R.menu.popupmenu_guest, userNavDropDownMenu.getMenu());

        }

        userNavDropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.sign_out:
                        Utilities.OpenActivity(getApplicationContext(), LoginActivity.class, activity);

                        SharedPreferences.Editor userDataEdit = userData.edit();

                        userDataEdit.putString("status", "Logged out");
                        userDataEdit.putString("accountName", "");
                        userDataEdit.putString("accountUsername", "");
                        userDataEdit.putString("accountEmail", "");
                        userDataEdit.putString("accountPassword", "");
                        userDataEdit.putString("accountImage", "");

                        userDataEdit.apply();


                        break;

                    case R.id.user_settings:
                        Utilities.OpenActivity(getApplicationContext(), SettingsActivity.class, null);
                        break;

                    case R.id.sign_in:
                        Utilities.OpenActivity(getApplicationContext(), LoginActivity.class, activity);
                        break;


                }

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                return true;
            }

        });

        userNavDropDownMenu.show();

    }

    PlaceLoader placeLoader;
    PlaceLoader itemLoader;

    private void loadPlacesFromServer(final String pType, final String sType) {

        if (itemLoader != null) {
            Log.d("debug", "item exist");
            itemLoader.cancel(true);
            itemLoader = null;
        }

        if (placeLoader != null) {
            placeLoader.cancel(true);
            placeLoader = null;


        }

        isLoadingPlace = true;

        placeDataList.clear();

        mainInterfaceAdapter.notifyDataSetChanged();

        recycleViewProgressBar.setVisibility(View.VISIBLE);

        placeLoader = new PlaceLoader().setOnRespondError(new OnRespondError() {

            @Override
            public void onRespondError(String error) {

                Log.d("debug", "error trying to get the data again");

                new PlaceLoader().setOnRespondError(this)
                                 .execute("0", pageCount, pType, sType);


               // HomeActivity.this.recycleViewProgressBar.setVisibility(View.GONE);
               // HomeActivity.this.itemListProgressBar.setVisibility(View.GONE);

            }

        });


        placeLoader.execute("0", pageCount, pType, sType);

        homeNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {

                                                           @Override
                                                           public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {


                                                               if (Utilities.checkIfLastScrolledItem(v, scrollX, scrollY, oldScrollX, oldScrollY)) {


                                                                   if (startingIndex < tableCount &&
                                                                           placeDataList.size() > 0 && !isLoadingItem) {

                                                                       placeDataList.add(null);
                                                                       mainInterfaceAdapter.notifyItemInserted(placeDataList.size() - 1);

                                                                       isLoadingItem = true;

                                                                       new Handler().postDelayed(new Runnable() {
                                                                           @Override
                                                                           public void run() {

                                                                               itemLoader = new PlaceLoader().setOnRespondError(new OnRespondError() {

                                                                                   @Override
                                                                                   public void onRespondError(String error) {

                                                                                       Log.d("debug", "error getting items from the server im tryingg");

                                                                                       itemLoader = new PlaceLoader().setOnRespondError(this);
                                                                                       itemLoader.execute(String.valueOf(startingIndex), pageCount, placeType, sType);


                                                                                   }
                                                                               });

                                                                               itemLoader.execute(String.valueOf(startingIndex), pageCount, placeType, sType);


                                                                           }
                                                                       }, 200);

                                                                   }

                                                               }
                                                           }
                                                       }
        );


    }


    private void loadPlacesByType(final String type) {

        if (!type.equals(placeType)) {

            appBarLayout.setExpanded(false);
            homeNestedScrollView.smoothScrollTo(0, 0);

            txtHome.setText((type.equals("General") ? "Home" : type));

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

            mainInterfaceAdapter = new MainInterfaceAdapter(getApplicationContext(),
                    ActivityType.HOME_ACTIVITY,
                    PlaceType.NONE,
                    placeDataList);

            tabLayoutRecyclerView.setAdapter(mainInterfaceAdapter);

            getMostPopular(type);
            loadPlacesFromServer(type, "Views");

            tabLayoutRecyclerView.setNestedScrollingEnabled(false);

            tabLayoutRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_DRAGGING:

                            floatingActionButton.setVisibility(View.GONE);

                            break;
                        case RecyclerView.SCROLL_STATE_IDLE:

                            floatingActionButton.setVisibility(View.VISIBLE);

                            break;
                    }
                }
            });


            tabLayoutRecyclerView.setLayoutManager(linearLayoutManager);

            if (tabLayout.getTabCount() > 0) {

                tabLayoutRecyclerView.removeAllViews();
                tabLayout.clearOnTabSelectedListeners();
                tabLayout.removeAllTabs();
            }

            tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
            tabLayout.addTab(tabLayout.newTab().setText("Most Popular"));
            tabLayout.addTab(tabLayout.newTab().setText("Recommend"));

            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                    homeNestedScrollView.smoothScrollTo(0, 0);
                    searchBTN.setIconified(true);

                    switch (tab.getPosition()) {
                        case 0:
                            loadPlacesFromServer(placeType, "Views");
                            break;
                        case 1:
                            loadPlacesFromServer(placeType, "Rating");
                            break;
                        case 2:
                            loadPlacesFromServer(placeType, "Recommended");
                            break;
                    }

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                    homeNestedScrollView.smoothScrollTo(0, 0);
                    searchBTN.setIconified(true);
                }
            });


            placeType = type;

        }

    }



    private void displayUserInfo () {

        if (userData.getString("status", "").equals("Logged In")) {

            Log.d("debug", "username: " + userData.getString("accountUsername", ""));
            Log.d("debug", "email: " + userData.getString("accountEmail", ""));
            Log.d("debug", "password: " + userData.getString("accountPassword", ""));
            Log.d("debug", "accountid: " + userData.getString("accountID", ""));
            Log.d("debug", "image: " + userData.getString("accountImage", ""));
            Log.d("debug", "name: " + userData.getString("accountName", ""));

            userProfileImage.setImageDrawable(new BitmapDrawable(getResources(), Utilities.BlurImg.stringToBitmap(userData.getString("accountImage", ""))));
            userName.setText(userData.getString("accountName", ""));
            userEmail.setText(userData.getString("accountEmail", ""));

        } else {

            userProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.account));
            userName.setText("Guest");
            userEmail.setVisibility(View.INVISIBLE);

        }


    }

    private void initComp() {

        userData = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        setContentView(R.layout.activity_home_menu);

        interstitialAd = new InterstitialAd(this);

        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        interstitialAd.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        interstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                if (interstitialAd.isLoaded())
                    interstitialAd.show();
            }
        });


        bannerAdView = (AdView)findViewById(R.id.adBanner);

        bannerAdView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());

        pullUpLoadLayout = (SwipeRefreshLayout) findViewById(R.id.pullUpLoadLayout);

        pullUpLoadLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                Log.d("debug", "pulled up");

                pullUpLoadLayout.setRefreshing(false);

                placeDataList.clear();
                mainInterfaceAdapter.notifyDataSetChanged();

                recycleViewProgressBar.setVisibility(View.VISIBLE);
                appBarLayout.setExpanded(false);

                String selectedSortType = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText().toString();

                getMostPopular(placeType);
                loadPlacesFromServer(placeType, (selectedSortType.equals("Most Viewed") ? "Views" :
                        (selectedSortType.equals("Most Popular")) ? "Rating" : "Recommended"));

            }
        });

        pullUpLoadLayout.setProgressViewOffset(false, 0, 180);
        pullUpLoadLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorAccent);

        backgroundMostPopular = (LinearLayout) findViewById(R.id.mostPopularBlackBackground);

        viewFlipperManager = (ViewFlipper) findViewById(R.id.viewFlipManager);

        mostPopularImages[0] = (ImageView) findViewById(R.id.popularImageView1);
        mostPopularImages[1] = (ImageView) findViewById(R.id.popularImageView2);
        mostPopularImages[2] = (ImageView) findViewById(R.id.popularImageView3);

        mostPopularName = (TextView) findViewById(R.id.most_pop_name);

        tabLayoutRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        tabLayout = (TabLayout) findViewById(R.id.home_tabLayout);

        recycleViewProgressBar = (ProgressBar) findViewById(R.id.recycleViewProgressBar);
        itemListProgressBar = (ProgressBar) findViewById(R.id.item_progress_bar);

        homeNestedScrollView = (NestedScrollView) findViewById(R.id.homeContentScrollView);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.home_collapsing_toolbar);

        collapsingToolbarLayout.setTitleEnabled(false);

        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (!isLoadingPlace) {
                    appBarLayout.setExpanded(false);
                    homeNestedScrollView.smoothScrollTo(0, 0);
                }

                if (!pullUpLoadLayout.isRefreshing())
                    pullUpLoadLayout.setEnabled(verticalOffset == 0);
            }
        });

        toolbar = (Toolbar) findViewById(R.id.action_bar_toolbar);

        setSupportActionBar(toolbar);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBarView = layoutInflater.inflate(R.layout.searchbar, null);
        Utilities.setSearchBar(this, actionBarView);

        txtHome = (TextView) actionBarView.findViewById(R.id.txtHome);

        final SearchView searchButton = (SearchView) actionBarView.findViewById(R.id.search_view);
        searchBTN = searchButton;

        /** Set drawer and navigation layout **/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );


        drawerLayout.setDrawerListener(drawerToggle);

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                searchButton.setIconified(true);

            }

            @Override
            public void onDrawerOpened(View drawerView) {

                pullUpLoadLayout.setEnabled(false);
                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                searchButton.setIconified(true);

            }

            @Override
            public void onDrawerClosed(View drawerView) {

                pullUpLoadLayout.setEnabled(true);

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0);

        userName = (TextView)headerLayout.findViewById(R.id.userName);
        userEmail = (TextView)headerLayout.findViewById(R.id.userEmail);
        userProfileImage = (ImageView)headerLayout.findViewById(R.id.userProfileImage);


        displayUserInfo();

        Utilities.setNavTitleStyle(this,
                R.id.nav_view,
                R.id.settingsTitle,
                R.style.navDrawerTitleStyle);

        Utilities.setNavTitleStyle(this,
                R.id.nav_view,
                R.id.menuTitle,
                R.style.navDrawerTitleStyle);


        /** End of setting drawer and navigation drawer **/

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                searchBTN.setIconified(true);
                startActivity(
                        new Intent(getApplicationContext(), SearchResultsActivity.class)
                                .putExtra("data", new String[]{"Location", ""}));
            }

        });

    }

    private void getMostPopular(final String type) {

        viewFlipperManager.stopFlipping();

        viewFlipperManager.setVisibility(View.INVISIBLE);
        backgroundMostPopular.setVisibility(View.INVISIBLE);

        RequestQueue request = Volley.newRequestQueue(getApplicationContext());

        final String ptype = type;

        String requestURL = "http://admin-spotter.000webhostapp.com/app_scripts/mostPopular.php";

        StringRequest mostPopularRequest = new StringRequest(Request.Method.POST, requestURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("debug", response);

                        try {
                            JSONObject jsonPopularData = new JSONObject(response);

                            JSONArray mostPopularDataArr = jsonPopularData.getJSONArray("mostPopularPlaces");

                            for (int i = 0; i != mostPopularDataArr.length(); ++i) {

                                JSONObject mostPopularData = new JSONObject(mostPopularDataArr.getString(i));

                                Log.d("debug", mostPopularData.getString("Name"));

                                String imageLink = mostPopularData.getString("ImageLink");

                                if (imageLink.length() > 0) {

                                    JSONObject placeImage = new JSONObject(imageLink);

                                    JSONObject placeImages = new JSONObject(placeImage.getString("placeImages"));

                                    JSONArray imageLinks = placeImages.getJSONArray("previewImages");

                                    Log.d("debug", imageLinks.getString(0));

                                    Picasso.with(getApplicationContext())
                                            .load(imageLinks.getString(0))
                                            .placeholder(R.drawable.loadingplace)
                                            .fit()
                                            .into(mostPopularImages[i]);


                                } else {

                                    Picasso.with(getApplicationContext())
                                            .load("http://vignette2.wikia.nocookie.net/date-a-live/images/5/57/Yoshino.%28Date.A.Live%29.full.1536435.jpg/revision/latest?cb=20140225221532")
                                            .placeholder(R.drawable.loadingplace)
                                            .fit()
                                            .into(mostPopularImages[i]);

                                }

                                mostPopularNames[i] = mostPopularData.getString("Name");

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        startMostPopularFlipping();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", "most popular trying to get data from server");
                getMostPopular(type);
            }
        }) {
            protected Map<String, String> getParams() {
                return new HashMap<String, String>() {{
                    put("placeType", ptype);
                }};
            }
        };

        request.add(mostPopularRequest);

    }

    private void startMostPopularFlipping() {

        viewFlipperManager.setDisplayedChild(0);
        mostPopularName.setText(mostPopularNames[0]);

        viewFlipperManager.setVisibility(View.VISIBLE);
        backgroundMostPopular.setVisibility(View.VISIBLE);

        appBarLayout.setExpanded(true);

        viewFlipperManager.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchBTN.setIconified(true);
                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                Toast.makeText(getApplicationContext(), mostPopularName.getText(), Toast.LENGTH_LONG).show();
            }

        });

        viewFlipperManager.startFlipping();

        viewFlipperManager.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left));
        viewFlipperManager.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));
        viewFlipperManager.setFlipInterval(3000);


        viewFlipperManager.getInAnimation().setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mostPopularName.setText(mostPopularNames[viewFlipperManager.getDisplayedChild()]);
            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        switch (item.getItemId()) {
            case R.id.Home:
                loadPlacesByType("General");
                break;
            case R.id.Hotels:
                loadPlacesByType("Hotel");
                break;
            case R.id.Restaurants:
                loadPlacesByType("Restaurant");
                break;
            case R.id.TouristSpots:
                loadPlacesByType("Tourist Spot");
                break;
            case R.id.Favorites:
                Utilities.OpenActivity(getApplicationContext(),
                        BookMarksActivity.class, null);
                break;
            case R.id.Settings:

                Utilities.OpenActivity(getApplicationContext(),
                        SettingsActivity.class, null);

                break;

        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!searchBTN.isIconified()) {
            searchBTN.setIconified(true);
        }

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (!searchBTN.isIconified())
                searchBTN.setIconified(true);
            else
                super.onBackPressed();

        }
    }

    private class PlaceLoader extends AsyncTask<String, Void, AppScript> {

        OnRespondError onRespondError;

        public PlaceLoader setOnRespondError(OnRespondError onRespondError) {
            this.onRespondError = onRespondError;
            return this;
        }

        @Override
        protected AppScript doInBackground(final String... params) {

            final AppScript loadPlaces = new AppScript(activity) {{
                setData("loadPlaces.php", new HashMap<String, String>() {{
                    put("currentRow", params[0]);
                    put("rowOffset", params[1]);
                    put("placeType", params[2]);
                    put("sortType", params[3]);
                }});
            }};


            return loadPlaces;
        }


        @Override
        protected void onPostExecute(AppScript loadPlaces) {
            super.onPostExecute(loadPlaces);

            try {

                tableCount = Integer.parseInt(loadPlaces.getTableCount());
                startingIndex = Integer.parseInt(loadPlaces.getOffSet());

                if (isLoadingItem) {

                    placeDataList.remove(placeDataList.size() - 1);
                    mainInterfaceAdapter.notifyItemRemoved(placeDataList.size());

                    isLoadingItem = false;

                }

                List<Place> placeD = loadPlaces.getPlacesList();

                for (Place place : placeD)
                    placeDataList.add(place);

                mainInterfaceAdapter.notifyDataSetChanged();

                recycleViewProgressBar.setVisibility(View.GONE);

                Log.d("debug", "place data size: " + placeDataList.size());

                isLoadingPlace = true;
                placeLoader = null;

            } catch (Exception e) {
                onRespondError.onRespondError(e.getMessage());
            }

        }
    }
}



