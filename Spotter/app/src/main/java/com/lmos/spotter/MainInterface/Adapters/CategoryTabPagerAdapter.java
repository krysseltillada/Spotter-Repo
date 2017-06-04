package com.lmos.spotter.MainInterface.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.lmos.spotter.MainInterface.Fragments.FragmentRatedCategory;
import com.lmos.spotter.MainInterface.Fragments.FragmentRecommendCategory;
import com.lmos.spotter.MainInterface.Fragments.FragmentViewedCategory;

/**
 * Created by Kryssel on 6/3/2017.
 */

public class CategoryTabPagerAdapter extends FragmentStatePagerAdapter {
    int testData;
    int tbLength;

    public CategoryTabPagerAdapter(FragmentManager fragmentManager, int tabLength, int tData) {
        super(fragmentManager);
        tbLength = tabLength;
        testData = tData;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentViewedCategory();
            case 1:
                return FragmentRatedCategory.newInstance(testData);
            case 2:
                return new FragmentRecommendCategory();
            default:
                break;
        }

        return null;
    }

    @Override
    public int getCount() {
        return tbLength;
    }
}
