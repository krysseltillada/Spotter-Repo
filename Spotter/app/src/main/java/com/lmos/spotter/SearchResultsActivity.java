package com.lmos.spotter;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by linker on 02/06/2017.
 *
 * This class will display the result/s of places
 * and/or specific hotels, restaurants and tourist spots.
 *
 */

public class SearchResultsActivity extends AppCompatActivity implements OnMapReadyCallback{

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView place_name, place_content_desc;
    MapView mapView;

    private final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);


        initComp();

        Bundle fetch_intent = getIntent().getExtras();

        switch (fetch_intent.getString("type")){

            case "General":
                setHeaderText("Batangas", "Bayang ng Magigiting");
                //attachFragment(new FragmentSearchResultGeneral(), "General");
                break;
            case "Hotel":
                setHeaderText("City of Dreams", "Inside Nightmare");
                //attachFragment(new FragmentSearchResult(), "Hotel");
                break;

        }

        /*
         * Set map properties and callback listener
         * MapView class must forward the following activity lifecycle methods
         * to the corresponding methods in MapView class: onCreate(), onStart()
         * onResume(), onPause(), onStop(), onDestroy(), onSaveInstanceState()
         * and onLowMemory().
         */
        Bundle mapViewBundle = null;
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.onResume(); // Immediately show map

        try {
            MapsInitializer.initialize(this);
        } catch (Exception e ){ e.printStackTrace(); }

        mapView.getMapAsync(this);

    }

    private void setHeaderText(String name, String description){
        place_name.setText(name);
        place_content_desc.setText(description);
    }

    /**private void attachFragment(Fragment fragment, String tag){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.search_content_holder, fragment, tag)
                .commit();
    }**/

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

        mapView = (MapView) findViewById(R.id.map_holder);

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(120, 118))
                .title("Testing Map"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.88, 151.21), 15f));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if(mapViewBundle == null){
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
