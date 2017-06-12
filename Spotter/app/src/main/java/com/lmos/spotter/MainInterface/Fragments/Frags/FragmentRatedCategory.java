package com.lmos.spotter.MainInterface.Fragments.Frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lmos.spotter.MainInterface.Adapters.ListPlaceAdapter;
import com.lmos.spotter.R;


public class FragmentRatedCategory extends Fragment {

    int testData;

    public FragmentRatedCategory() {
    }


    public static FragmentRatedCategory newInstance(int td) {

        FragmentRatedCategory fragmentRatedCategory = new FragmentRatedCategory();

        Bundle bundleData = new Bundle();

        bundleData.putInt("testData", td);

        fragmentRatedCategory.setArguments(bundleData);

        return fragmentRatedCategory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);
        ListView mostRatedList  = (ListView) view.findViewById(R.id.subCategoryList);

        Bundle bundleData = getArguments();

        mostRatedList.setAdapter(new ListPlaceAdapter(getContext(), R.layout.place_item_list, new String[bundleData.getInt("testData")]));

        return view;
    }

}
