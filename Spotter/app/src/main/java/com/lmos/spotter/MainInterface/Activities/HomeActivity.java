package com.lmos.spotter.MainInterface.Activities;

import android.Manifest;
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
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telecom.PhoneAccount;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.lmos.spotter.R;
import com.lmos.spotter.SearchInterface.Activities.SearchResultsActivity;
import com.lmos.spotter.Utilities.Utilities;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnSuggestionListener,
        SearchView.OnQueryTextListener{

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
    private final int LOCATION_REQUEST_CODE = 1;
    Activity activity = this;

    String[] sampleWords = {"hello", "judy", "sample", "text", "june", "General", "Hotel", "Resto", "Tourist Spot"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initComp();
        startMostPopularFlipping();

    }

    private void initComp(){
       setContentView(R.layout.activity_home_menu);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.home_collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.white));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));


        /** Set TabLayout **/
        TabLayout tabLayout = (TabLayout) findViewById(R.id.home_tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Most Viewed"));
        tabLayout.addTab(tabLayout.newTab().setText("Most Rated"));
        tabLayout.addTab(tabLayout.newTab().setText("Recommend"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
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
                    if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {

                        ActivityCompat.requestPermissions(activity, new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        }, LOCATION_REQUEST_CODE);

                    }

                }
                else{

                    if(Utilities.checkPlayServices(activity)){

                            String location =new Utilities.LocationHandler(activity).findLocation();
                            Toast.makeText(getApplicationContext(), location, Toast.LENGTH_LONG).show();
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

        switch (item.getItemId()){
            case R.id.Hotels:
                Toast.makeText(getApplicationContext(), "Hotels", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Home:
                Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.TouristSpots:
                Toast.makeText(getApplicationContext(), "TouristSpots", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Restaurants:
                Toast.makeText(getApplicationContext(), "Restaurants", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Favorites:
                Toast.makeText(getApplicationContext(), "Favorites", Toast.LENGTH_SHORT).show();
                break;
            case R.id.Settings:
                Utilities.OpenActivity(this,SettingsActivity.class, "");
                Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_SHORT).show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){

            case LOCATION_REQUEST_CODE:

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    if(Utilities.checkPlayServices(activity)){

                        String location =new Utilities.LocationHandler(activity).findLocation();
                        Toast.makeText(getApplicationContext(), location, Toast.LENGTH_LONG).show();
                    }

                }
                break;

        }

    }
}