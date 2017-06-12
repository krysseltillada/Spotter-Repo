package com.lmos.spotter.MainInterface.Fragments.Tabs;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TouristSpotTab extends Fragment {

    public static TouristSpotTab createObject (String sampleData) {

        TouristSpotTab touristSpotTab = new TouristSpotTab();

        Bundle sampleBundle = new Bundle();

        sampleBundle.putString("sample", sampleData);

        touristSpotTab.setArguments(sampleBundle);

        return touristSpotTab;

    }


    public TouristSpotTab() {
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
