package com.lmos.spotter.MainInterface.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.lmos.spotter.MainInterface.Adapters.ListPlaceAdapter;
import com.lmos.spotter.R;

public class FragmentRecommendCategory extends Fragment {

    public FragmentRecommendCategory() {

    }

    public static FragmentRecommendCategory newInstance() {
        return new FragmentRecommendCategory();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);
        ListView recommendViewList  = (ListView) view.findViewById(R.id.subCategoryList);

        recommendViewList.setAdapter(new ListPlaceAdapter(getContext(), R.layout.place_item_list, new String[2]));

        return view;
    }

}
