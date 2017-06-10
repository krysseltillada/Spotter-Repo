package com.lmos.spotter.MainInterface.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.R;

/**
 * Created by Kryssel on 6/10/2017.
 */

public class FindedPlacesFragment extends Fragment {
    @Nullable

    public static Fragment createObject (double latitude, double longtitude) {

        FindedPlacesFragment findedPlacesFragment = new FindedPlacesFragment();

        Bundle locationData = new Bundle();

        locationData.putDouble("latitude", latitude);
        locationData.putDouble("longtitude", longtitude);

        findedPlacesFragment.setArguments(locationData);

        return findedPlacesFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_finded_places, container, false);

        TextView txtLocationResult = (TextView) view.findViewById(R.id.findedPlaces);

        String messageResult = "Latitude: " + String.valueOf(getArguments().getDouble("latitude")) + "\n" +
                               "Longtitude: " + String.valueOf(getArguments().getDouble("longtitude"));

        txtLocationResult.setText(messageResult);

        return view;
    }
}
