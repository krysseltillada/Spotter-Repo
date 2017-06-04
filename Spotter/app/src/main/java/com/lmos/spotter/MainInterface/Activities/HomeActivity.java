package com.lmos.spotter.MainInterface.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.lmos.spotter.MainInterface.Adapters.HomeTabPagerAdapter;
import com.lmos.spotter.Utilities.KeyboardState;
import com.lmos.spotter.SearchResultsActivity;
import com.lmos.spotter.Utilities.Utilities;
import com.lmos.spotter.R;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SearchView searchBtn;


    private void startMostPopularFlipping() {

        ViewFlipper viewFlipperManager = (ViewFlipper) findViewById(R.id.viewFlipManager);

        viewFlipperManager.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left));
        viewFlipperManager.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));
        viewFlipperManager.setFlipInterval(3000);

        viewFlipperManager.startFlipping();

    }

    String[] sampleWords = {"hello", "judy", "sample", "text", "june"};

    private void initializeUI() {
        final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
        tabLayout.addTab(tabLayout.newTab().setText("Most Rated"));
        tabLayout.addTab(tabLayout.newTab().setText("Recommend"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);

        HomeTabPagerAdapter placePagerAdapter = new HomeTabPagerAdapter(getSupportFragmentManager(),
                tabLayout.getTabCount());

        viewPager.setAdapter(placePagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.homeToolbar);
        setSupportActionBar(toolbar);

        final ActionBar homeActionBar = getSupportActionBar();

        homeActionBar.setDisplayHomeAsUpEnabled(true);
        homeActionBar.setDisplayShowCustomEnabled(true);

        final View actionBarView = inflator.inflate(R.layout.searchbar, null);
        final TextView txtHome = (TextView) actionBarView.findViewById(R.id.txtHome);

        txtHome.setText("Spotter");

        final String[] from = new String[]{"judy"};
        final int[] to = new int[]{android.R.id.text1};


        searchBtn = (SearchView) actionBarView.findViewById(R.id.search_view);
        final SimpleCursorAdapter searchAdapter = new SimpleCursorAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        searchBtn.setSuggestionsAdapter(searchAdapter);

        searchBtn.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                startActivity(new Intent(getApplicationContext(), SearchResultsActivity.class));
                return true;
            }
        });


        searchBtn.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                Intent launchSearchResult = new Intent(getApplication(), SearchResultsActivity.class);
                getApplicationContext().startActivity(launchSearchResult);

                return false;

            }

            @Override
            public boolean onQueryTextChange(String searchValue) {
                Utilities.QuerySearchResults(searchValue, searchAdapter, sampleWords);
                return false;
            }
        });


        searchBtn.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtHome.setVisibility(View.GONE);
            }
        });

        searchBtn.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                txtHome.setVisibility(View.VISIBLE);
                return false;
            }
        });

        /**
         *
         * this code detects a backpress event and a soft keyboard down event
         * created a keyboard state object to store a boolean wether the
         * keyboard is up or down gonna make this code a utility to be useful
         *
         */

        final LinearLayout activityRootView = (LinearLayout) findViewById(R.id.home_parent_layout);
        final KeyboardState keyboardState = new KeyboardState();


        activityRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchBtn.isIconified())
                    searchBtn.setIconified(true);
            }
        });

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                    if (heightDiff > Utilities.dpToPx(getApplicationContext(), 200)) {
                        if (!keyboardState.isKeyboardUp)
                            keyboardState.isKeyboardUp = true;
                    } else {
                        if (keyboardState.isKeyboardUp) {
                            searchBtn.setIconified(true);
                            keyboardState.isKeyboardUp = false;
                        }

                    }


            }
        });


        homeActionBar.setCustomView(actionBarView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "GPS is required to detect places", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }

    /**
     * Bitmap getImageResource (int id) {
     * return ((BitmapDrawable)getResources().getDrawable(id)).getBitmap();
     * }
     **/
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        initializeUI();
        startMostPopularFlipping();



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (searchBtn.isIconified()) {

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }

        } else {
            searchBtn.setIconified(true);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.Home:
                Utilities.OpenActivity(getApplicationContext(), HomeActivity.class);
                break;
            case R.id.Hotels:
                Utilities.OpenActivity(getApplicationContext(), HotelActivity.class);
                break;
            case R.id.TouristSpots:
                Utilities.OpenActivity(getApplicationContext(), TouristSpotActivity.class);
                break;
            case R.id.Restaurants:
                break;

            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /// TEST ONLY




}