package com.lmos.spotter;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
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

    private double Lat, Lng;

    private int width, height;

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

        final View mainLayout = inflater.inflate(R.layout.map_layout, container, false);

        return mainLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_holder);

        mapFragment.getMapAsync(this);
    }

    LatLng srcPosition;
    GoogleMap googleMap;

    public void setUserPosition (LatLng userPosition) {

        srcPosition = userPosition;

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        final LatLng sourcePosition =  new LatLng(srcPosition.latitude, srcPosition.longitude);
        final LatLng destPosition = new LatLng(14.4845, 120.9921);

        Marker sourceMarker = googleMap.addMarker(new MarkerOptions().position(sourcePosition)
                .title("your here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        Marker destMarker = googleMap.addMarker(new MarkerOptions().position(destPosition).title("destination"));

        new MapDirections(getContext(),
                googleMap,
                sourcePosition,
                destPosition,
                Color.CYAN,
                5
        ).drawDirections()
                .setOnDoneDrawDirectionListener(new MapDirections.OnDoneDrawDirectionListener() {
                    @Override
                    public void onDoneDrawDirection(String duration, String distance) {

                        String directionMessage = "time: " +  duration +
                                " distance: " + distance;

                        Snackbar.make(getView(), directionMessage, Snackbar.LENGTH_INDEFINITE).show();

                    }
                });

        LatLngBounds.Builder zoomBuilder = new LatLngBounds.Builder();

        zoomBuilder.include(sourcePosition);
        zoomBuilder.include(destPosition);

        final LatLngBounds zoomBounds = zoomBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(userPosition));

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBounds, 150));
            }
        });

    }

    @Override
    public void onMapReady(final GoogleMap gMap) {

        googleMap = gMap;

        if (!googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.flat_map_style))))
            Log.d("debug", "style set up success");

        googleMap.setTrafficEnabled(true);
        googleMap.setBuildingsEnabled(true);

        googleMap.getUiSettings().setMapToolbarEnabled(false);


        /*

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        final LatLng sourcePosition =  new LatLng(srcPosition.latitude, srcPosition.longitude);
        final LatLng destPosition = new LatLng(13.7565, 121.0583);

        Marker sourceMarker = googleMap.addMarker(new MarkerOptions().position(sourcePosition)
                                                                     .title("your here")
                                                                     .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        Marker destMarker = googleMap.addMarker(new MarkerOptions().position(destPosition).title("destination"));

        new MapDirections(getContext(),
                          googleMap,
                          sourcePosition,
                          destPosition,
                          Color.CYAN,
                          5
                          ).drawDirections()
                           .setOnDoneDrawDirectionListener(new MapDirections.OnDoneDrawDirectionListener() {
                               @Override
                               public void onDoneDrawDirection(String duration, String distance) {

                                   String directionMessage = "travel time: " +  duration +
                                                             "travel distance: " + distance;

                                   Snackbar.make(getView(), directionMessage, Snackbar.LENGTH_INDEFINITE).show();

                               }
                           });

        LatLngBounds.Builder zoomBuilder = new LatLngBounds.Builder();

        zoomBuilder.include(sourcePosition);
        zoomBuilder.include(destPosition);

        final LatLngBounds zoomBounds = zoomBuilder.build();

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(zoomBounds, 120));
            }
        });

        */


    }
}
