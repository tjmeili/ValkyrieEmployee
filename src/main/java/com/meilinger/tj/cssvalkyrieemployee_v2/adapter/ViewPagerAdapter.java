package com.meilinger.tj.cssvalkyrieemployee_v2.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.meilinger.tj.cssvalkyrieemployee_v2.fragment.HomeFragment;
import com.meilinger.tj.cssvalkyrieemployee_v2.fragment.ScheduleFragment;
import com.meilinger.tj.cssvalkyrieemployee_v2.fragment.TimesheetFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ScheduleFragment.newInstance();
            case 1:
                return HomeFragment.newInstance();
            case 2:
                return TimesheetFragment.newInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

}
