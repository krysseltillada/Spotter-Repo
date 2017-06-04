package com.lmos.spotter.MainInterface.Fragments;

/**
 * Created by Kryssel on 6/1/2017.
 */


import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lmos.spotter.MainInterface.Adapters.ListPlaceAdapter;
import com.lmos.spotter.R;
import com.lmos.spotter.Utilities.Utilities;

public class MostViewedTab extends Fragment {

    int preLast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mostViewTabView = inflater.inflate(R.layout.main_tab, container, false);
        ListView mostViewedListview = (ListView) mostViewTabView.findViewById(R.id.mainTabList);

        ListPlaceAdapter mostViewAdapter = new ListPlaceAdapter(getContext(), R.layout.place_item_list, new String[20]);
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

        mostViewedListview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                switch (view.getId()) {
                    case R.id.mainTabList:
                        if (Utilities.checkIfLastItem(firstVisibleItem, visibleItemCount,
                                totalItemCount)) {

                            if (preLast > 3) {

                                FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

                                if (fab.getVisibility() != View.GONE) {

                                    fab.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
                                    fab.setVisibility(View.GONE);
                                    fab.setClickable(false);

                                }

                                preLast = firstVisibleItem + visibleItemCount;

                            }

                        } else {

                            FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

                            if (fab.getVisibility() != View.VISIBLE) {

                                fab.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_in));
                                fab.setVisibility(View.VISIBLE);
                                fab.setClickable(true);

                            }

                            preLast = firstVisibleItem + visibleItemCount;

                        }



                }
            }
        });

        return mostViewTabView;
    }


}
