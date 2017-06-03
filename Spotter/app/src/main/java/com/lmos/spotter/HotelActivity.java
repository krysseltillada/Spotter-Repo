package com.lmos.spotter;

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
import android.view.Gravity;
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

public class HotelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SearchView searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        TabLayout hotelTabLayout = (TabLayout)findViewById(R.id.hotel_tab_layout);

        hotelTabLayout.addTab(hotelTabLayout.newTab().setText("Most Viewed"));

        hotelTabLayout.addTab(hotelTabLayout.newTab().setText("Most Rated"));

        hotelTabLayout.addTab(hotelTabLayout.newTab().setText("Recommend"));

        hotelTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        HotelTabPagerAdapter hotelTabPagerAdapter = new HotelTabPagerAdapter(getSupportFragmentManager(),
                                                                             hotelTabLayout.getTabCount());

        final ViewPager hotelViewPager = (ViewPager)findViewById(R.id.hotel_pager);

        hotelViewPager.setAdapter(hotelTabPagerAdapter);
        hotelViewPager.setOffscreenPageLimit(3);
        hotelViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener (hotelTabLayout));

        hotelTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                hotelViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        final LayoutInflater inflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.hotelToolbar);

        setSupportActionBar(toolbar);

        final ActionBar homeActionBar = getSupportActionBar();

        homeActionBar.setTitle("Hotels");

        homeActionBar.setDisplayHomeAsUpEnabled(true);
        homeActionBar.setDisplayShowCustomEnabled(true);

        final View actionBarView = inflator.inflate(R.layout.searchbar, null);
        final TextView txtHotel = (TextView) actionBarView.findViewById(R.id.txtHome);

        txtHotel.setText("Hotels");

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

        final LinearLayout activityRootView = (LinearLayout) findViewById(R.id.hotel_parent_layout);
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

                    Log.d("test", "aa");

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
