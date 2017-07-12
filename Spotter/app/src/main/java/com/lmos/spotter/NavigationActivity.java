package com.lmos.spotter;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

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
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        setContentView(R.layout.activity_navigation);


        LatLng destination = getIntent().getParcelableExtra("destination");

        getSupportFragmentManager().beginTransaction()
                .add(R.id.nav_map_holder, MapsLayoutFragment.newInstance(destination), "Map")
                .commit();

        locationHandler = new Utilities.LocationHandler(this, this);
        locationHandler.buildGoogleClient();

        /** Setup the toolbar **/
        toolbar = (Toolbar) findViewById(R.id.nav_toolbar);
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

        if(mapFragment != null && mapFragment instanceof MapsLayoutFragment)
            ((MapsLayoutFragment) mapFragment).setUserPosition(
                    new LatLng(lat, lng), "navigate", null);


    }

    /** Activity Lifecycle methods **/
    @Override
    protected void onStart() {
        super.onStart();
        locationHandler.changeApiState("connect");
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHandler.changeApiState("disconnect");
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHandler.changeApiState("reconnect");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHandler.changeApiState("disconnect");
    }
    /** End of activity lifecycle methods **/

}
