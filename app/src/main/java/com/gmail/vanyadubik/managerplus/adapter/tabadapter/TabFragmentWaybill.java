package com.gmail.vanyadubik.managerplus.adapter.tabadapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.fragment.FuelListFragment;
import com.gmail.vanyadubik.managerplus.fragment.WaybillListFragment;
import com.gmail.vanyadubik.managerplus.fragment.WorkPlaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TabFragmentWaybill extends Fragment {
    private static TabLayout tabLayout;
    private static ViewPager viewPager;
    private static ViewPagerAdapter viewPagerAdapter;
    private int[] icons = {R.drawable.tab_workplase,
            R.drawable.tab_fuel,
            R.drawable.tab_waybill
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View x =  inflater.inflate(R.layout.app_bar_main,null);

        tabLayout = (TabLayout) x.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) x.findViewById(R.id.main_tab_content);

        setupViewPager(viewPager);


        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }

        tabLayout.getTabAt(0).select();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

                if(position == 0) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(R.string.work_plase_name);
                }else if(position == 1){
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.fuel_name);
                }else if(position == 2){
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(R.string.waybill_list_name);
                }else{
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("");
                }

                FragmentBecameVisibleInterface fragment =
                        (FragmentBecameVisibleInterface) viewPagerAdapter.instantiateItem(viewPager, position);
                if (fragment != null) {
                    fragment.onBecameVisible();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return x;

    }


    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFrag(new WorkPlaseFragment().getInstance(),
                getActivity().getResources().getString(R.string.work_plase_name));
        viewPagerAdapter.addFrag(new FuelListFragment().getInstance(),
                getActivity().getResources().getString(R.string.fuel_name));
        viewPagerAdapter.addFrag(new WaybillListFragment().getInstance(),
                getActivity().getResources().getString(R.string.waybill_list_name));
        viewPager.setAdapter(viewPagerAdapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

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

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }



}
