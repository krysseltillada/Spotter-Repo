package com.lmos.spotter.MainInterface.Fragments;

/**
 * Created by Kryssel on 6/1/2017.
 */


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lmos.spotter.MainInterface.Adapters.ListPlaceAdapter;
import com.lmos.spotter.R;

public class MostViewedTab extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mostViewTabView = inflater.inflate(R.layout.most_viewed_tab, container, false);
        ListView mostViewedListview = (ListView) mostViewTabView.findViewById(R.id.mostViewList);

        ListPlaceAdapter mostViewAdapter = new ListPlaceAdapter(getContext(), R.layout.place_item_list, new String[10]);
        mostViewedListview.setAdapter(mostViewAdapter);

        mostViewedListview.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        return mostViewTabView;
    }


}
