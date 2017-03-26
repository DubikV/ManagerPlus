package com.gmail.vanyadubik.managerplus.adapter.tabadapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.fragment.WaybillFragment;
import com.gmail.vanyadubik.managerplus.fragment.WaybillFragmentMap;

public class TabPageWaybillFragmentAdapter extends FragmentPagerAdapter {

    private String[] tabs = { "Головне меню", "Карта"};

    public TabPageWaybillFragmentAdapter(FragmentManager fm) {
        super(fm);
        final int[] ICONS = new int[] {
                R.drawable.ic_waybill,
                R.drawable.ic_map,
        };
    }

    @Override
        public CharSequence getPageTitle(int position) {
           return tabs[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return WaybillFragment.getInstance();
            case 1:
                return WaybillFragmentMap.getInstance();
            }
            return null;
    }

    @Override
    public int getCount() {
        return tabs.length;
    }



}


