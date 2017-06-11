package com.lmos.spotter.MainInterface.Fragments;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.R;

import java.io.IOException;
import java.security.Provider;
import java.util.List;
import java.util.Locale;

/**
 * Created by Kryssel on 6/10/2017.
 */

public class FindedPlacesFragment extends Fragment {
    @Nullable

    public static Fragment createObject (String userCityLocation) {

        FindedPlacesFragment findedPlacesFragment = new FindedPlacesFragment();

        Bundle locationData = new Bundle();

        locationData.putString("userCityLocation", userCityLocation);

        findedPlacesFragment.setArguments(locationData);

        return findedPlacesFragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_finded_places, container, false);

        TextView txtLocationResult = (TextView) view.findViewById(R.id.findedPlaces);

        txtLocationResult.setText(getArguments().getString("userCityLocation"));

        return view;
    }

}
