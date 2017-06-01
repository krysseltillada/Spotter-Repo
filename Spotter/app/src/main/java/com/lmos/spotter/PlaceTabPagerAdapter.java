package com.lmos.spotter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

/**
 * Created by Kryssel on 6/1/2017.
 */

class PlaceTabPagerAdapter extends FragmentStatePagerAdapter {

    int numTabs;

    public PlaceTabPagerAdapter (FragmentManager fm, int numT) {
        super(fm);
        numTabs = numT;
    }

    MostViewedTab prevMostviewTab;
    MostRated prevRatedTab;
    RecommendTab prevRecommendTab;

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0:

                if (prevMostviewTab == null) {
                    prevMostviewTab = new MostViewedTab();
                    return prevMostviewTab;
                }

                return prevMostviewTab;

            case 1:


                if (prevRatedTab == null) {
                    Log.d("ada", "Ada");
                    prevRatedTab = new MostRated();
                    return prevRatedTab;
                }

                return prevRatedTab;
            case 2:
                if (prevRecommendTab == null) {
                    prevRecommendTab = new RecommendTab();
                    return prevRecommendTab;
                }

                return prevRecommendTab;
            default:
                return null;

        }
    }


    @Override
    public int getCount() {
        return numTabs;
    }

}
