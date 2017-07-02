package com.lmos.spotter.MainInterface.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.AppScript;
import com.lmos.spotter.MainInterface.Adapters.MainInterfaceAdapter;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.PlaceType;
import com.lmos.spotter.Utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    public interface OnRespondError {
        void onRespondError (String error);
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
    private CoordinatorLayout mainLayout;
    private TabLayout tabLayout;

    private int startingIndex;
    private int tableCount;

    private String placeType;
    private Activity activity = this;
    private ActionBarDrawerToggle drawerToggle;

    private List <Place> placeDataList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initComp();
        startMostPopularFlipping();
        loadPlacesByType("Home");

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void userNavDropDown (View view) {

        PopupMenu userNavDropDownMenu = new PopupMenu(HomeActivity.this, view);

        userNavDropDownMenu.getMenuInflater().inflate(R.menu.popupmenu, userNavDropDownMenu.getMenu());

        userNavDropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.sign_out:
                        Utilities.OpenActivity(getApplicationContext(), LoginActivity.class, activity);
                        break;

                    case R.id.user_settings:
                        Utilities.OpenActivity(getApplicationContext(), SettingsActivity.class, null);
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

    private void loadPlacesByType (String type) {

        if (!type.equals(placeType)) {

            mainLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.fade_in));

            homeNestedScrollView.smoothScrollTo(0, 0);
            appBarLayout.setExpanded(true);

            txtHome.setText(type);

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

            placeDataList = new ArrayList<>();

            mainInterfaceAdapter = new MainInterfaceAdapter(getApplicationContext(),
                    ActivityType.HOME_ACTIVITY,
                    PlaceType.NONE,
                    placeDataList);

            tabLayoutRecyclerView.setAdapter(mainInterfaceAdapter);
            tabLayoutRecyclerView.setNestedScrollingEnabled(false);

            recycleViewProgressBar.setVisibility(View.VISIBLE);

            new PlaceLoader().setOnRespondError(new OnRespondError() {

                @Override
                public void onRespondError(String error) {

                    Toast.makeText(getApplicationContext(), "error getting data from the server", Toast.LENGTH_LONG).show();
                    HomeActivity.this.recycleViewProgressBar.setVisibility(View.GONE);
                    HomeActivity.this.itemListProgressBar.setVisibility(View.GONE);

                }

            }).execute("0", "2");

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

            homeNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener () {

                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if(Utilities.checkIfLastScrolledItem(v, scrollX, scrollY, oldScrollX, oldScrollY)) {


                            if (startingIndex < tableCount) {

                                itemListProgressBar.setVisibility(View.VISIBLE);


                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        new PlaceLoader().setOnRespondError(new OnRespondError() {

                                            @Override
                                            public void onRespondError(String error) {
                                                Toast.makeText(getApplicationContext(), "error getting data from the server", Toast.LENGTH_LONG).show();
                                                HomeActivity.this.recycleViewProgressBar.setVisibility(View.GONE);
                                                HomeActivity.this.itemListProgressBar.setVisibility(View.GONE);
                                            }
                                        }).execute(String.valueOf(startingIndex), "2");

                                        itemListProgressBar.setVisibility(View.GONE);

                                    }
                                }, 1500);

                            }

                        }
                    }
                }
            );

            tabLayoutRecyclerView.setLayoutManager(linearLayoutManager);

            if (tabLayout.getTabCount() > 0) {

                tabLayoutRecyclerView.removeAllViews();
                tabLayout.clearOnTabSelectedListeners();
                tabLayout.removeAllTabs();
            }


            tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
            tabLayout.addTab(tabLayout.newTab().setText("Most Rated"));
            tabLayout.addTab(tabLayout.newTab().setText("Recommend"));


            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                    homeNestedScrollView.smoothScrollTo(0, 0);
                    searchBTN.setIconified(true);

                    /*

                    switch (tab.getPosition()) {
                        case 0:

                            tabLayoutRecyclerView.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                    ActivityType.HOME_ACTIVITY,
                                    PlaceType.NONE,
                                    TestData.PlaceData.testDataMostViewed));
                            break;
                        case 1:

                            tabLayoutRecyclerView.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                    ActivityType.HOME_ACTIVITY,
                                    PlaceType.NONE,
                                    TestData.PlaceData.testDataMostRated));
                            break;
                        case 2:

                            tabLayoutRecyclerView.setAdapter(new MainInterfaceAdapter(getApplicationContext(),
                                    ActivityType.HOME_ACTIVITY,
                                    PlaceType.NONE,
                                    TestData.PlaceData.testDataRecommend));
                            break;
                    } */

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

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

    private void initComp(){

        SharedPreferences userData = getSharedPreferences(LoginActivity.LOGIN_PREFS, MODE_PRIVATE);

        Log.d("debug", userData.getString("username", ""));
        Log.d("debug", userData.getString("email", ""));
        Log.d("debug", userData.getString("password", ""));
        Log.d("debug", userData.getString("accountID", ""));
        Log.d("debug", userData.getString("name", ""));

        setContentView(R.layout.activity_home_menu);

        tabLayoutRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        tabLayout = (TabLayout) findViewById(R.id.home_tabLayout);

        mainLayout = (CoordinatorLayout) findViewById(R.id.homeLayout);

        recycleViewProgressBar = (ProgressBar)findViewById(R.id.recycleViewProgressBar);
        itemListProgressBar = (ProgressBar)findViewById(R.id.item_progress_bar);

        homeNestedScrollView = (NestedScrollView) findViewById(R.id.homeContentScrollView);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.home_collapsing_toolbar);

        collapsingToolbarLayout.setTitleEnabled(false);

        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar_layout);

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


                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
                searchButton.setIconified(true);

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                                .putExtra("type", "Location"));
            }

        });

    }

    private void startMostPopularFlipping(){

        ViewFlipper viewFlipperManager = (ViewFlipper) findViewById(R.id.viewFlipManager);

        viewFlipperManager.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchBTN.setIconified(true);
                Utilities.hideSoftKeyboard(getCurrentFocus(), HomeActivity.this);
            }

        });

        viewFlipperManager.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left));
        viewFlipperManager.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));
        viewFlipperManager.setFlipInterval(3000);

        viewFlipperManager.startFlipping();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        switch (item.getItemId()) {
            case R.id.Home:
                loadPlacesByType("Home");
                break;
            case R.id.Hotels:
                loadPlacesByType("Hotel");
                break;
            case R.id.Restaurants:
                loadPlacesByType("Restaurants");
                break;
            case R.id.TouristSpots:
                loadPlacesByType("Tourist Spots");
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

    class PlaceLoader extends AsyncTask<String, Void, AppScript> {

        OnRespondError onRespondError;

        public PlaceLoader setOnRespondError (OnRespondError onRespondError) {
            this.onRespondError = onRespondError;
            return this;
        }

        @Override
        protected AppScript doInBackground(final String ...params) {

                final AppScript loadPlaces = new AppScript() {{
                    setRequestURL("http://192.168.1.39/projects/spotter/app_scripts/");
                    setData("loadPlaces.php", new HashMap<String, Object>() {{
                        put("currentRow", params[0]);
                        put("rowOffset", params[1]);
                    }});
                }};


            return loadPlaces;
        }


        @Override
        protected void onPostExecute(AppScript loadPlaces) {
            super.onPostExecute(loadPlaces);

            try {

                tableCount = Integer.parseInt(loadPlaces.getTableCount());
                startingIndex = Integer.parseInt(loadPlaces.getResult()) + 1;

                List<Place> placeD = loadPlaces.getPlaces();

                for (Place place : placeD)
                    placeDataList.add(place);

                for (int i = 0; i != placeDataList.size(); ++i)
                    mainInterfaceAdapter.notifyItemChanged(i);

                recycleViewProgressBar.setVisibility(View.GONE);

            } catch (Exception e) {
                onRespondError.onRespondError(e.getMessage());
            }


        }
    }




}