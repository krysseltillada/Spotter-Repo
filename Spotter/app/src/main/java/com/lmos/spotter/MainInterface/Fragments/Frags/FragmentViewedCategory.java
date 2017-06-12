package com.lmos.spotter.MainInterface.Fragments.Frags;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.lmos.spotter.MainInterface.Adapters.ListPlaceAdapter;
import com.lmos.spotter.R;

public class FragmentViewedCategory extends Fragment {


    public FragmentViewedCategory() {
        // Required empty public constructor
    }

    public static FragmentViewedCategory newInstance(String param1, String param2) {
        return new FragmentViewedCategory();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);
        ListView mostViewedList  = (ListView) view.findViewById(R.id.subCategoryList);

        mostViewedList.setAdapter(new ListPlaceAdapter(getContext(), R.layout.place_item_list, new String[1]));

        return view;

    }


}
