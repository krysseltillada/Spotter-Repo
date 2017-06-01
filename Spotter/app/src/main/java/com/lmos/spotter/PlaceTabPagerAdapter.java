package com.lmos.spotter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Kryssel on 6/1/2017.
 */

class PlaceTabPagerAdapter extends FragmentStatePagerAdapter {

    int numTabs;

    public PlaceTabPagerAdapter (FragmentManager fm, int numT) {
        super(fm);
        numTabs = numT;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch (position) {
            case 0:

                MostViewedTab mostViewedTab = new MostViewedTab();
                return mostViewedTab;
            case 1:
                MostRated mostRated = new MostRated();
                return mostRated;
            case 2:
                RecommendTab recommendTab = new RecommendTab();
                return recommendTab;
            default:
                return null;

        }
    }


    @Override
    public int getCount() {
        return numTabs;
    }

}
