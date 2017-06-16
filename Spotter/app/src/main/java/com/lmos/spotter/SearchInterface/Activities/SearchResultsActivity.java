package com.lmos.spotter.SearchInterface.Activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class SearchResultsActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView place_name, place_content_desc;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    SearchReviewsAdapter mAdapter;
    FavoritesDbHelper favoritesDbHelper;
    Activity activity = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        initComp();

        Bundle fetch_intent = getIntent().getExtras();

        switchFragment(fetch_intent.getString("type"), "");

        Snackbar snackbar = Snackbar.make(
                findViewById(R.id.search_view_wrapper),
                "Add to Favorites",
                Snackbar.LENGTH_INDEFINITE
        );
        snackbar.setAction(
                "ADD",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addToFavorites();
                    }
                }
        );
        snackbar.show();

    }

    private void setHeaderText(String name, String description){
        place_name.setText(name);
        place_content_desc.setText(description);
    }

    private void switchFragment(String type, String... params){

        Fragment fragment = null;
        String tag, backStack;

        if(type.equals("General")){ tag = "General"; backStack = ""; }
        else{ tag = "type"; backStack = "General"; }

        switch (type){

            case "Location":
                break;
            case "General":
                setHeaderText("Batangas", "Bayan ng magigiting");
                fragment = new FragmentSearchResultGeneral();
                backStack = null;
                break;
            default:
                setHeaderText("City of Dreams", "Nightmares it is");
                fragment = FragmentSearchResult.newInstance(params);
                backStack = "General";
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.search_content_holder, fragment, type)
                .addToBackStack(backStack)
                .commit();

        Toast.makeText(this, String.valueOf(getSupportFragmentManager().getBackStackEntryCount()), Toast.LENGTH_LONG).show();

    }

    private void attachFragment(Fragment fragment, String tag, int view_id){

        getSupportFragmentManager().beginTransaction()
                .add(view_id, fragment, tag)
                .commit();

    }

    private void addToFavorites(){

        favoritesDbHelper = new FavoritesDbHelper(this);
        Log.d("ADD", "Triggered");
        favoritesDbHelper.addToFavorites();

    }

    private void initComp(){

        toolbar = (Toolbar) findViewById(R.id.action_bar_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        place_name = (TextView) findViewById(R.id.place_name);
        place_content_desc = (TextView) findViewById(R.id.place_content_description);

        //Set collapse & expanded title color
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));

        //Set the title on collapsing toolbar
        collapsingToolbarLayout.setTitle("");

        /** Set RecyclerView for user reviews. **/
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mAdapter = new SearchReviewsAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        /** End setting RecyclerView **/

        // Inflate map into Framelayout
        attachFragment(MapsLayoutFragment.newInstance(12.8797, 121.7740), "Maps", R.id.map_content_holder);

        setSupportActionBar(toolbar);

        //Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        NavUtils.navigateUpFromSameTask(this);

        return super.onOptionsItemSelected(item);
    }

}