package com.lmos.spotter.MainInterface.Activities;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.lmos.spotter.MainInterface.Fragments.FindedPlacesFragment;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import com.lmos.spotter.MainInterface.Fragments.FindPlaceFragment;

public class FindPlacesActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_places);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.findPlaceFragment, new FindPlaceFragment(), "FindPlace")
                                   .commit();*/

        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.findPlaceFragment, new FindedPlacesFragment(), "FindedPlace")
                                   .commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }

        return true;
    }


}
