package com.lmos.spotter.MainInterface.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lmos.spotter.MainInterface.Fragments.Tabs.FindTabFragment;
import com.lmos.spotter.MainInterface.Fragments.Tabs.HotelTab;
import com.lmos.spotter.MainInterface.Fragments.Tabs.RestaurantTab;
import com.lmos.spotter.MainInterface.Fragments.Tabs.TouristSpotTab;

/**
 * Created by Kryssel on 6/13/2017.
 */

public class FindPlaceTabPagerAdapter extends FragmentStatePagerAdapter {

    int tbCount;

    public FindPlaceTabPagerAdapter (FragmentManager fragmentManager, int tabCount) {
        super(fragmentManager);
        tbCount = tabCount;
    }

    public Fragment getItem(int position) {
        switch (position) {

            case 0:
                return HotelTab.createObject("hotel 1 2 3");
            case 1:
                return TouristSpotTab.createObject("tourist 1 22 3");
            case 2:
                return RestaurantTab.createObject("restaurant 211 01010 1");

            default:
                break;
        }

        return null;
    }

    @Override
    public int getCount() {
        return tbCount;
    }
}
