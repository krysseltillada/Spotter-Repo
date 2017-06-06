package com.lmos.spotter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by linker on 06/06/2017.
 */

public class FragmentSearchResult extends Fragment  implements OnMapReadyCallback{

    private MapView mapView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View thisView = inflater.inflate(R.layout.search_result_hotel, container, false);

        mapView = (MapView) thisView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        try{
            MapsInitializer.initialize(getContext().getApplicationContext());
        }catch (Exception e){
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        return thisView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //googleMap.setMyLocationEnabled(true);
        googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(120, 118))
            .title("Testing Map"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.88, 151.21), 15f));
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
}
