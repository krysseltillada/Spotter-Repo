package com.lmos.spotter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rated_category, container, false);
    }

}
