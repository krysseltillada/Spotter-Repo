package com.lmos.spotter.MainInterface.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.lmos.spotter.MainInterface.Adapters.MainInterfaceAdapter;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.Utilities.Utilities;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnSuggestionListener,
        SearchView.OnQueryTextListener,
        Utilities.OnLocationFoundListener {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LayoutInflater inflater;
    private ActionBar homeActionBar;
    private View actionBarView;
    private TextView txtHome;
    private SearchView searchBtn;
    private SimpleCursorAdapter searchAdapter;
    private DrawerLayout drawerLayout;
    private FloatingActionButton floatingActionButton;
    Utilities.LocationHandler locationHandler = new Utilities.LocationHandler(this, this);
    Activity activity = this;

    String[] sampleWords = {"hello", "judy", "sample", "text", "june", "General", "Hotel", "Resto", "Tourist Spot"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComp();
        locationHandler.buildGoogleClient();
        startMostPopularFlipping();

    }

    @Override
    protected void onStart() {
        super.onStart();
        locationHandler.changeApiState("connect");
    }

    private void initComp(){
        setContentView(R.layout.activity_home_menu);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.home_collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));

        final RecyclerView tabLayoutRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        tabLayoutRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false));

        tabLayoutRecyclerView.setAdapter(new MainInterfaceAdapter(1, getApplicationContext()));


        /** Set TabLayout **/
        TabLayout tabLayout = (TabLayout) findViewById(R.id.home_tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
        tabLayout.addTab(tabLayout.newTab().setText("Most Rated"));
        tabLayout.addTab(tabLayout.newTab().setText("Recommend"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tab.getPosition()) {
                    case 0:
                        tabLayoutRecyclerView.setAdapter(new MainInterfaceAdapter(1, getApplicationContext()));
                        break;

                    case 1:
                        tabLayoutRecyclerView.setAdapter(new MainInterfaceAdapter(5, getApplicationContext()));
                        break;

                    case 2:
                        tabLayoutRecyclerView.setAdapter(new MainInterfaceAdapter(2, getApplicationContext()));
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        /** End setting of tab layout **/



        /** Set toolbar and action bar **/
        toolbar = (Toolbar) findViewById(R.id.action_bar_toolbar);
        setSupportActionBar(toolbar);
        homeActionBar = getSupportActionBar();

        // Set LayoutInflater to inflate a custom search view in action bar.
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        homeActionBar.setDisplayHomeAsUpEnabled(true);
        homeActionBar.setDisplayShowCustomEnabled(true);

        actionBarView = inflater.inflate(R.layout.searchbar, null);
        txtHome = (TextView) actionBarView.findViewById(R.id.txtHome);

        txtHome.setText(getResources().getString(R.string.app_name));

        // Set search adapter for search view.
        String[] from = new String[]{"Judy"};
        int[] to = new int[]{android.R.id.text1};

        searchBtn = (SearchView) actionBarView.findViewById(R.id.search_view);
        searchAdapter = new SimpleCursorAdapter(
                getApplicationContext(),
                android.R.layout.simple_list_item_2,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        searchBtn.setSuggestionsAdapter(searchAdapter);
        searchBtn.setOnSuggestionListener(this);
        searchBtn.setOnQueryTextListener(this);
        searchBtn.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        homeActionBar.setCustomView(actionBarView);
        /** End of toolbar and action bar settings **/


        /** Set drawer and navigation layout **/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);

        Utilities.setNavTitleStyle(this,
                                   R.id.nav_view,
                                   R.id.settingsTitle,
                                   R.style.navDrawerTitleStyle);

        Utilities.setNavTitleStyle(this,
                                   R.id.nav_view,
                                   R.id.menuTitle,
                                   R.style.navDrawerTitleStyle);


        navigationView.setNavigationItemSelectedListener(this);
        /** End of setting drawer and navigation drawer **/

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    locationHandler.checkPermission();
                }
                else{

                    if(Utilities.checkPlayServices(activity)){
                        Log.d("LocationHandler", locationHandler.findLocation());
                    }

                }

            }

        });

    }

    private String getSuggestionText(int position){

        String selected_item = "";
        Cursor searchCursor = searchAdapter.getCursor();
        if(searchCursor.moveToPosition(position)){
            selected_item = searchCursor.getString(1);
        }
        return selected_item;
    }

    private void startMostPopularFlipping(){

        ViewFlipper viewFlipperManager = (ViewFlipper) findViewById(R.id.viewFlipManager);
        viewFlipperManager.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left));
        viewFlipperManager.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));
        viewFlipperManager.setFlipInterval(3000);

        viewFlipperManager.startFlipping();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Bundle bundle = new Bundle();

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        switch (item.getItemId()){
            case R.id.Hotels:

                bundle.putString("Category", "Hotels");

                Utilities.OpenActivityWithBundle(getApplicationContext(),
                                                 HTRActivity.class,
                                                 "",
                                                 bundle
                                                 );
                break;
            case R.id.Home:

                Utilities.OpenActivity(getApplicationContext(), HomeActivity.class, "");

                break;
            case R.id.TouristSpots:

                bundle.putString("Category", "Tourist Spots");

                Utilities.OpenActivityWithBundle(getApplicationContext(),
                        HTRActivity.class,
                        "",
                        bundle
                );

                break;
            case R.id.Restaurants:

                bundle.putString("Category", "Restaurants");

                Utilities.OpenActivityWithBundle(getApplicationContext(),
                        HTRActivity.class,
                        "",
                        bundle
                );

                break;
            case R.id.Favorites:

                break;
            case R.id.Settings:
                Utilities.OpenActivity(this,SettingsActivity.class, "");
                break;
        }

        return true;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return true;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Intent send_data = new Intent(getApplicationContext(), SearchResultsActivity.class);
        send_data.putExtra("type", getSuggestionText(position));
        startActivity(send_data);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchValue) {
        Utilities.QuerySearchResults(searchValue, searchAdapter, sampleWords);
        return false;
    }

    @Override
    public void onBackPressed() {
        if (searchBtn.isIconified()) {

            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }

        } else {
            searchBtn.setIconified(true);
        }
    }

    @Override
    protected void onDestroy() {
        locationHandler.changeApiState("disconnect");
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case Utilities.REQUEST_CODES.LOCATION_REQUEST_CODE:


                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

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

                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        // Handle system response when the user did not enable gps/network settings.
                        break;

                }
                break;

        }

    }

    @Override
    public void onLocationFound(String location) {

        Snackbar sb = Snackbar.make(
                findViewById(R.id.home_content_wrapper),
                location,
                Snackbar.LENGTH_LONG
        );
        sb.setAction("OK", null);
        sb.show();
    }
}