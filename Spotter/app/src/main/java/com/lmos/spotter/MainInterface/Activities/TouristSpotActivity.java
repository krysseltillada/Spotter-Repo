package com.lmos.spotter.MainInterface.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lmos.spotter.MainInterface.Adapters.CategoryTabPagerAdapter;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchResultsActivity;
import com.lmos.spotter.Utilities.KeyboardState;
import com.lmos.spotter.Utilities.Utilities;

public class TouristSpotActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SearchView searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat);

        TabLayout touristTabLayout = (TabLayout)findViewById(R.id.sub_cat_tab_layout);

        touristTabLayout.addTab(touristTabLayout.newTab().setText("Most Viewed"));

        touristTabLayout.addTab(touristTabLayout.newTab().setText("Most Rated"));

        touristTabLayout.addTab(touristTabLayout.newTab().setText("Recommend"));

        touristTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        CategoryTabPagerAdapter touristTabPagerAdapter = new CategoryTabPagerAdapter(getSupportFragmentManager(),
                touristTabLayout.getTabCount(), 3);

        final ViewPager touristViewPager = (ViewPager)findViewById(R.id.sub_cat_pager);

        touristViewPager.setAdapter(touristTabPagerAdapter);
        touristViewPager.setOffscreenPageLimit(3);
        touristViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener (touristTabLayout));

        touristTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                touristViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.subCatToolbar);

        setSupportActionBar(toolbar);

        final ActionBar touristActionBar = getSupportActionBar();

        touristActionBar.setTitle("Tourist Spots");

        touristActionBar.setDisplayHomeAsUpEnabled(true);
        touristActionBar.setDisplayShowCustomEnabled(true);

        final View actionBarView = inflator.inflate(R.layout.searchbar, null);
        final TextView txtHotel = (TextView) actionBarView.findViewById(R.id.txtHome);

        txtHotel.setText("Tourist Spots");

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
                Utilities.QuerySearchResults(searchValue, searchAdapter, new String[]{"judy"});
                return false;
            }
        });


        searchBtn.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txtHotel.setVisibility(View.GONE);

            }
        });

        searchBtn.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                txtHotel.setVisibility(View.VISIBLE);

                return false;

            }
        });


        ///BUGGGGGGGGGGGGGGGGGGGGGGG

        final LinearLayout activityRootView = (LinearLayout) findViewById(R.id.sub_cat_parent_layout);
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


        touristActionBar.setCustomView(actionBarView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "GPS is required to detect near places", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        Utilities.setNavTitleStyle(this,
                R.id.nav_view,
                R.id.settingsTitle,
                R.style.navDrawerTitleStyle);

        Utilities.setNavTitleStyle(this,
                R.id.nav_view,
                R.id.menuTitle,
                R.style.navDrawerTitleStyle);

        navigationView.setNavigationItemSelectedListener(this);
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
            case R.id.Restaurants:
                Utilities.OpenActivity(getApplicationContext(), RestaurantActivity.class);
                break;
            case R.id.Favorites:
                Utilities.OpenActivity(getApplicationContext(), FavoritesActivity.class);
                break;
            case R.id.Settings:
                Utilities.OpenActivity(getApplicationContext(), SettingsActivity.class);

            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
