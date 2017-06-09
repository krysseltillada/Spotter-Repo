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
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FindPlacesActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private String getNamedLocation(Location location, LocationListener locationListener, LocationManager locationManager) {

        if (location != null) {

            Geocoder geocoder = new Geocoder(FindPlacesActivity.this, Locale.getDefault());

            try {

                double longtitude = location.getLongitude();
                double latitude = location.getLatitude();

                List<Address> cityAddress = geocoder.getFromLocation(latitude,
                        longtitude,
                        1);

                if (cityAddress.size() > 0) {

                    String message = cityAddress.get(0).getAddressLine(1);

                    TextView textView = (TextView) findViewById(R.id.info);

                    textView.setText(message);


                    if (locationListener != null && locationManager != null)
                        locationManager.removeUpdates(locationListener);

                } else {
                    Log.d("notjing", "ad");
                }
            } catch (IOException e) {

                Log.d("exception", e.getMessage());

                e.printStackTrace();
            }

        }

        return "";


    }


    GoogleApiClient googleApiClient;

    private void test2() {
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this);

        builder.addConnectionCallbacks(this);
        builder.addOnConnectionFailedListener(this);
        builder.addApi(LocationServices.API);

        googleApiClient = builder.build();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    private void test() {

        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        final LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                getNamedLocation(location, this, locationManager);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Loc Callback", "onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Loc Callback", "onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Loc Callback", "onProviderDisabled");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);



        // Snackbar.make(view, "GPS is required to detect near places", Snackbar.LENGTH_LONG)
        //       .setAction("Action", null).show();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_places);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ImageView gpsImageView = (ImageView) findViewById(R.id.gpsLoaderGif);

        Utilities.loadGifImageView(this, gpsImageView, R.drawable.loadingplaces);

        test();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
            default:
                break;
        }

        return true;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        getNamedLocation(location, null, null);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }


}
