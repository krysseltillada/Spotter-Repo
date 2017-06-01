package com.lmos.spotter;

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

public class MostRated extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View mostRatedTabView = inflater.inflate(R.layout.most_rated_tab, container, false);

        ListView mostRatedListview = (ListView) mostRatedTabView.findViewById(R.id.mostRatedList);

        mostRatedListview.setOnTouchListener(new ListView.OnTouchListener() {
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

        ListPlaceAdapter mostRatedAdapter = new ListPlaceAdapter(getContext(), R.layout.place_item_list, new String[7]);

        mostRatedListview.setAdapter(mostRatedAdapter);

        return mostRatedTabView;
    }
}
