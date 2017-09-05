package com.parsin.bletool.Controller.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

//import com.parsin.bletool.View.Fragment.LearnFragment;
import com.parsin.bletool.Test.LearnFragment;
import com.parsin.bletool.View.Fragment.SettingsFragment;
//import com.parsin.bletool.View.Fragment.TrackFragment;
import com.parsin.bletool.Test.TrackFragment;
//TODO change this to View.Fragment

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new LearnFragment();
            case 1:
                return new TrackFragment();
            case 2:
                return new SettingsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}