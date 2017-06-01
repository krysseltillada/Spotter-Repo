package com.lmos.spotter;

/**
 * Created by Kryssel on 6/1/2017.
 */

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class MostViewedTab extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mostViewTabView = inflater.inflate(R.layout.most_viewed_tab, container, false);

        ListView mostViewedListview = (ListView) mostViewTabView.findViewById(R.id.mostViewList);

        ListPlaceAdapter mostViewAdapter = new ListPlaceAdapter(getContext(), R.layout.place_item_list, new String [] {
                "1",
                "2",
                "d",
                "d",
                "f"

        });

        mostViewedListview.setAdapter(mostViewAdapter);

        return mostViewTabView;
    }
}
