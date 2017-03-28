package com.gmail.vanyadubik.managerplus.adapter.tabadapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.fragment.WaybillFragment;
import com.gmail.vanyadubik.managerplus.fragment.WaybillFragmentMap;

import java.util.ArrayList;
import java.util.List;

public class TabFragmentWaybill extends Fragment {

    private int[] tabIcons = {
            R.drawable.ic_waybill,
            R.drawable.ic_map,
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x =  inflater.inflate(R.layout.activity_main,null);

        int[] icons = {R.drawable.tab_home,
                R.drawable.tab_search,
                R.drawable.tab_home,
                R.drawable.tab_search
        };
        TabLayout tabLayout = (TabLayout) x.findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) x.findViewById(R.id.main_tab_content);

        setupViewPager(viewPager);


        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }
        tabLayout.getTabAt(0).select();

        return x;

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.insertNewFragment(new WaybillFragment());
        adapter.insertNewFragment(new WaybillFragmentMap());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void insertNewFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }



}
