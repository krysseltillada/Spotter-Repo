package com.lmos.spotter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lmos.spotter.Utilities.MapDirections;

/**
 * Created by emman on 6/8/2017.
 *
 * This class will handle Google Map view
 * that can be used in multiple activities if desired.
 *
 */

public class MapsLayoutFragment extends Fragment implements OnMapReadyCallback{


    GoogleMap googleMap;
    LatLng destination;
    Marker mMarker;

    public static MapsLayoutFragment newInstance(LatLng destination){

        MapsLayoutFragment mapsLayoutFragment = new MapsLayoutFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("destination", destination);
        mapsLayoutFragment.setArguments(bundle);

        return mapsLayoutFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View mainLayout = inflater.inflate(R.layout.map_layout, container, false);
        return mainLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_holder);
        mapFragment.getMapAsync(this);
        destination = getArguments().getParcelable("destination");
    }

    public void setUserPosition (final LatLng userPosition, final String action, Float bearing) {

        /*int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        Log.d("UserPosLatDis", String.valueOf(userPosition.latitude));*/

        int lwidth = 25;

        if(action.equals("directions"))
            lwidth = 5;

        new MapDirections(getContext(),
                googleMap,
                userPosition,
                destination,
                getResources().getColor(R.color.colorPrimaryDark),
                lwidth
        ).drawDirections()
                .setOnDoneDrawDirectionListener(new MapDirections.OnDoneDrawDirectionListener() {
                    @Override
                    public void onDoneDrawDirection(String duration, String distance) {
                        Log.d("Map", "Drawing Direction");
                        String directionMessage = "time: " +  duration +
                                " distance: " + distance;


                    }
                });

        LatLngBounds.Builder zoomBuilder = new LatLngBounds.Builder();

        zoomBuilder.include(userPosition);
        zoomBuilder.include(destination);

        final LatLngBounds zoomBounds = zoomBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                switch (action){

                    case "navigate":
                        Log.d("LocationHandler", "Updating camera position");
                        cameraPosition = new CameraPosition.Builder()
                                .target(userPosition)
                                .zoom(50)
                                .bearing(0)
                                .tilt(45)
                                .build();

                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        break;
                    default:
                        googleMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(zoomBounds, 150));
                        break;
                }

                if(mMarker == null)
                {
                    mMarker = googleMap.addMarker(new MarkerOptions().position(userPosition)
                            .title("your here")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                }
                else
                    mMarker.setPosition(userPosition);

            }
        });



    }

    @Override
    public void onMapReady(final GoogleMap gMap) {

        googleMap = gMap;

        if (!googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.flat_map_style))))
            Log.d("debug", "style set up success");

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 150));
        googleMap.addMarker(new MarkerOptions().position(destination).title("destination"));
        googleMap.setTrafficEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

    }
}
