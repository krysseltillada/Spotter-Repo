package com.lmos.spotter.MainInterface.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lmos.spotter.R;
import com.lmos.spotter.MainInterface.Fragments.FindedPlacesFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindTabFragment extends Fragment {


    public static FindedPlacesFragment createObject (String sampleData) {

        FindedPlacesFragment findPlaceFragment = new FindedPlacesFragment();

        Bundle sampleBundle = new Bundle();

        sampleBundle.putString("sample", sampleData);

        findPlaceFragment.setArguments(sampleBundle);

        return findPlaceFragment;

    }

    public FindTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View createdView = inflater.inflate(R.layout.fragment_find_tab, container, false);

        TextView textView = (TextView)createdView.findViewById(R.id.txtTitle);

        textView.setText(getArguments().getString("sample"));


        return  createdView;
    }

}
