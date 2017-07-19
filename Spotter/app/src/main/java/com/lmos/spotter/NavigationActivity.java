package com.lmos.spotter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.lmos.spotter.Utilities.Utilities;

/**
 * Created by emman on 7/11/2017.
 */

public class NavigationActivity extends AppCompatActivity
    implements Utilities.OnLocationFoundListener{

    Toolbar toolbar;
    Utilities.LocationHandler locationHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Log.d("Navi", "onCreate");

        locationHandler = new Utilities.LocationHandler(this, this);
        locationHandler.buildGoogleClient();

        LatLng destination = getIntent().getParcelableExtra("destination");

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_map_holder, MapsLayoutFragment.newInstance(destination), "Map")
                .commit();


        /** Setup the toolbar **/
        toolbar = (Toolbar) findViewById(R.id.nav_toolbar);
        toolbar.setTitle("Navigation chu chu");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        /** End of toolbar setting **/
    }

    @Override
    public void onLocationFoundCity(String city) {

    }

    @Override
    public void onLocationFoundLatLng(double lat, double lng) {

        Fragment mapFragment = getSupportFragmentManager().findFragmentByTag("Map");
        Log.d("Nav", "Location Found!");
        if(mapFragment != null && mapFragment instanceof MapsLayoutFragment)
            ((MapsLayoutFragment) mapFragment).setUserPosition(
                    new LatLng(lat, lng), "navigate", null);

        Log.d("NAV-POS", String.valueOf(lat) + " " + String.valueOf(lng));

    }

    /** Activity Lifecycle methods **/
    @Override
    protected void onStart() {
        super.onStart();
        if(locationHandler != null)
            locationHandler.changeApiState("connect");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(locationHandler.checkApiState())
            locationHandler.stopLocationRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(locationHandler.checkApiState())
            locationHandler.findLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHandler.changeApiState("disconnect");
    }

    /** End of activity lifecycle methods **/

}
