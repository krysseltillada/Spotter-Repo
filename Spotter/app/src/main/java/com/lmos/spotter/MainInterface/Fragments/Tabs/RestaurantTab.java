package com.lmos.spotter.MainInterface.Fragments.Tabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lmos.spotter.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantTab extends Fragment {

    public static RestaurantTab createObject (String sampleData) {

        RestaurantTab restaurantTab = new RestaurantTab();

        Bundle sampleBundle = new Bundle();

        sampleBundle.putString("sample", sampleData);

        restaurantTab.setArguments(sampleBundle);

        return restaurantTab;

    }


    public RestaurantTab() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View createdView = inflater.inflate(R.layout.fragment_find_tab, container, false);

        TextView textView = (TextView)createdView.findViewById(R.id.txtTitle);

        textView.setText(getArguments().getString("sample"));


        return  createdView;
    }

}
