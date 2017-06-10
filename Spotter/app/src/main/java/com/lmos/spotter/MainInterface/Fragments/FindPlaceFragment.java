package com.lmos.spotter.MainInterface.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;

/**
 * Created by Kryssel on 6/10/2017.
 */

public class FindPlaceFragment extends Fragment {
    @Nullable

    public static Fragment createObject (String cityName) {

        FindedPlacesFragment findedPlacesFragment = new FindedPlacesFragment();

        Bundle locationData = new Bundle();

        locationData.putString("cityName", cityName);

        findedPlacesFragment.setArguments(locationData);

        return findedPlacesFragment;

    }

    public void findPlaceInCity() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Context context = getActivity().getApplicationContext();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        String latitude = String.valueOf(location.getLatitude());
                        String longtitude = String.valueOf(location.getLongitude());


                        Toast.makeText(getActivity().getApplicationContext(), "lat: " + latitude + "\nlong: " + longtitude,
                                      Toast.LENGTH_LONG).show();

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
                });


    }



    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_finding, container, false);

        ImageView gpsImageView = (ImageView) view.findViewById(R.id.gpsLoaderGif);

        Utilities.loadGifImageView(getActivity().getApplicationContext(), gpsImageView, R.drawable.loadingplaces);


        /*
            getActivity().getSupportFragmentManager().beginTransaction()
                                                     .replace(R.id.findPlaceFragment, new FindedPlacesFragment(), "FindedPlaces")
                                                     .commit(); */


        return view;
    }
}
