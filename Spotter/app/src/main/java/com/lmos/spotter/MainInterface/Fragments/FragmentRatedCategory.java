package com.lmos.spotter.MainInterface.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lmos.spotter.MainInterface.Adapters.ListPlaceAdapter;
import com.lmos.spotter.R;


public class FragmentRatedCategory extends Fragment {

    public FragmentRatedCategory() {

    }

    public static FragmentRatedCategory newInstance(String param1, String param2) {
        return new FragmentRatedCategory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rated_category, container, false);
        ListView mostRatedList  = (ListView) view.findViewById(R.id.hotelMostRatedList);

        mostRatedList.setAdapter(new ListPlaceAdapter(getContext(), R.layout.place_item_list, new String[100]));

        return view;
    }

}
