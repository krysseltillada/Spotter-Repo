package com.lmos.spotter;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by emman on 6/8/2017.
 *
 * This class will handle Google Map view
 * that can be used in multiple activities if desired.
 *
 */

public class MapsLayoutFragment extends Fragment implements OnMapReadyCallback{

    private double Lat, Lng;

    public static MapsLayoutFragment newInstance(double lat, double lng){

        MapsLayoutFragment mapsLayoutFragment = new MapsLayoutFragment();

        Bundle args = new Bundle();
        args.putDouble("Lat", lat);
        args.putDouble("Lang", lng);
        mapsLayoutFragment.setArguments(args);

        return mapsLayoutFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle getArgs = getArguments();
        Lat = getArgs.getDouble("Lat");
        Lng = getArgs.getDouble("Lng");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_holder);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng ph = new LatLng(Lat, Lng);

        googleMap.addMarker(new MarkerOptions().position(ph).title("It's more fun in the Philippines!"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ph));

    }
}
