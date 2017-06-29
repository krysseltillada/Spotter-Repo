package com.lmos.spotter.MainInterface.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.lmos.spotter.AccountInterface.Activities.LoginActivity;
import com.lmos.spotter.AppScript;
import com.lmos.spotter.MainInterface.Adapters.EndlessRecyclerOnScrollListener;
import com.lmos.spotter.MainInterface.Adapters.MainInterfaceAdapter;
import com.lmos.spotter.MainInterface.Adapters.SampleEndlessRecyclerView;
import com.lmos.spotter.Place;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.Utilities.ActivityType;
import com.lmos.spotter.Utilities.PlaceType;
import com.lmos.spotter.Utilities.TestData;
import com.lmos.spotter.Utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

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
    RecyclerView tabLayoutRecyclerView;
    private int currentIndex;

    String placeType;
    Activity activity = this;
    ActionBarDrawerToggle drawerToggle;

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

            CoordinatorLayout mainLayout = (CoordinatorLayout) findViewById(R.id.homeLayout);

            mainLayout.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.fade_in));

            homeNestedScrollView.smoothScrollTo(0, 0);
            appBarLayout.setExpanded(true);

            txtHome = (TextView) actionBarView.findViewById(R.id.txtHome);
            txtHome.setText(type);

            tabLayoutRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.home_tabLayout);

            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

            final ProgressBar progressBar = (ProgressBar)findViewById(R.id.item_progress_bar);

            new PlaceLoader().execute();

            homeNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener () {

                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if(v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                scrollY > oldScrollY) {

                            progressBar.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    mainInterfaceAdapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }, 1500);


                        }
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

        setContentView(R.layout.activity_home_menu);

        homeNestedScrollView = (NestedScrollView) findViewById(R.id.homeContentScrollView);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.home_collapsing_toolbar);

        collapsingToolbarLayout.setTitleEnabled(false);

        appBarLayout = (AppBarLayout)findViewById(R.id.app_bar_layout);

        toolbar = (Toolbar) findViewById(R.id.action_bar_toolbar);

        setSupportActionBar(toolbar);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBarView = layoutInflater.inflate(R.layout.searchbar, null);
        Utilities.setSearchBar(this, actionBarView);

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


        @Override
        protected AppScript doInBackground(String ...params) {

            final AppScript loadPlaces = new AppScript(){{
                setRequestURL("http://192.168.2.112/projects/spotter/app_scripts/");
                setData("loadPlaces.php", new HashMap<String, Object>() {{
                    put("currentRow", "0");
                    put("rowOffset", "5");
                }});
            }};

            return loadPlaces;
        }

        @Override
        protected void onPostExecute(AppScript placeData) {
            super.onPostExecute(placeData);

            mainInterfaceAdapter = new MainInterfaceAdapter(getApplicationContext(),
                                                            ActivityType.HOME_ACTIVITY,
                                                            PlaceType.NONE,
                                                            placeData.getPlaces());

            tabLayoutRecyclerView.setAdapter(mainInterfaceAdapter);

        }
    }




}