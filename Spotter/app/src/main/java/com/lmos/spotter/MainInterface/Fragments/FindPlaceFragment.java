package com.lmos.spotter.MainInterface.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Kryssel on 6/10/2017.
 */

public class FindPlaceFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    @Nullable

    GoogleApiClient fuseApi;

    LocationManager locationManager;
    LocationListener locationListener;

    TextView txtFindingMessage;

    String []messageFinding = {"Finding places in your city",
                               "Finding places in your city.",
                               "Finding places in your city..",
                               "Finding places in your city..."};

    int messageCount = 0;


    public void findPlaceInCity() {

        if (locationManager == null)
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationListener == null) {

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    String strLatitude = String.valueOf(location.getLatitude());
                    String strLongtitude = String.valueOf(location.getLongitude());

                    Toast.makeText(getContext(), "Latitude: " + strLatitude + "\nLongtitude: " + strLongtitude, Toast.LENGTH_LONG).show();

                    getActivity().getSupportFragmentManager()
                                 .beginTransaction()
                                 .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                 .replace(R.id.findPlaceFragment, FindedPlacesFragment.createObject(location.getLatitude(), location.getLongitude()))
                                 .commit();

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

    }

    public void findPlaceInCityFused () {

        fuseApi = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (fuseApi != null)
            fuseApi.connect();

    }


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_finding, container, false);

        ImageView gpsImageView = (ImageView) view.findViewById(R.id.gpsLoaderGif);

        txtFindingMessage = (TextView)view.findViewById(R.id.findingMessage);

        Timer message = new Timer();

        message.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {

                if (messageCount >= messageFinding.length - 1 )
                    messageCount = 0;

                txtFindingMessage.post(new Runnable() {
                    @Override
                    public void run() {
                        txtFindingMessage.setText(messageFinding[messageCount]);
                    }
                });

                ++messageCount;

            }

        }, 0, 1000);


        Utilities.loadGifImageView(getActivity().getApplicationContext(), gpsImageView, R.drawable.loadingplaces);

        /*
            getActivity().getSupportFragmentManager().beginTransaction()
                                                     .replace(R.id.findPlaceFragment, new FindedPlacesFragment(), "FindedPlaces")
                                                     .commit(); */

        findPlaceInCity();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        findPlaceInCity();

    }

    @Override
    public void onPause() {
        super.onPause();

        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (locationManager != null && locationListener != null)
            locationManager.removeUpdates(locationListener);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {



        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(fuseApi);


        String latitude = String.valueOf(location.getLatitude());
        String longtitude = String.valueOf(location.getLongitude());

        Toast.makeText(getContext(), "latitude: " + latitude + "\nlongtitude: " + longtitude, Toast.LENGTH_LONG).show();


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(getContext(), "connection failed", Toast.LENGTH_LONG).show();

    }


}
