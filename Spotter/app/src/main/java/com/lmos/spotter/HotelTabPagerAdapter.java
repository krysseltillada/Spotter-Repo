package com.lmos.spotter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Kryssel on 6/3/2017.
 */

public class HotelTabPagerAdapter extends FragmentStatePagerAdapter {

    int tbLength;

    public HotelTabPagerAdapter (FragmentManager fragmentManager, int tabLength) {
        super(fragmentManager);
        tabLength = tabLength;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentViewedCategory();
            case 1:
                return new FragmentRatedCategory();
            case 2:
                break;

            default:
                break;
        }
    }

    @Override
    public int getCount() {
        return tbLength;
    }
}
