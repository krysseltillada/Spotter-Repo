package com.lmos.spotter.MainInterface.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

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

        return null;
    }

    @Override
    public int getCount() {
        return tbCount;
    }
}
