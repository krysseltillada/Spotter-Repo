package com.lmos.spotter.MainInterface.Adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

<<<<<<< HEAD
import com.lmos.spotter.MainInterface.Fragments.Tabs.MostRatedTab;
import com.lmos.spotter.MainInterface.Fragments.Tabs.MostViewedTab;
import com.lmos.spotter.MainInterface.Fragments.Tabs.RecommendTab;

=======
>>>>>>> im_senpai
/**
 * Created by Kryssel on 6/1/2017.
 */

public class HomeTabPagerAdapter extends FragmentStatePagerAdapter {

    int numTabs;

    public HomeTabPagerAdapter(FragmentManager fm, int numT) {
        super(fm);
        numTabs = numT;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position)    {
        switch (position) {
            case 0:
                //return new MostViewedTab();
            case 1:
                //return new MostRatedTab();
            case 2:
                //return new RecommendTab();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numTabs;
    }

}
