package com.gmail.vanyadubik.managerplus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.gps.GPSTracker;

import java.text.SimpleDateFormat;

import javax.inject.Inject;

import static android.R.id.tabhost;

public class WaybillFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_waybill;

    @Inject
    GPSTracker gpsTracker;

    private View view;

    private EditText dateStartEdinText, dateEndEdinText;
    private SimpleDateFormat dateFormatter;

    public static WaybillFragment getInstance() {

        Bundle args = new Bundle();
        WaybillFragment fragment = new WaybillFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ((ManagerPlusAplication) getActivity().getApplication()).getComponent().inject(this);

        final TabHost tabHost = (TabHost) view.findViewById(tabhost);
        tabHost.setup();
        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("VisitsToday");
        tabSpec.setIndicator(getResources().getString(R.string.waybill_visits_today_name));
        tabSpec.setContent(R.id.workplace_tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("FuelToday");
        tabSpec.setIndicator(getResources().getString(R.string.fuel_today_name));
        tabSpec.setContent(R.id.workplace_tab2);
        tabHost.addTab(tabSpec);

        final TabWidget tw = (TabWidget)tabHost.findViewById(android.R.id.tabs);
        for (int i = 0; i < tw.getChildCount(); ++i)
        {
            final View tabView = tw.getChildTabViewAt(i);
            final TextView tv = (TextView)tabView.findViewById(android.R.id.title);
            tv.setTextSize(getResources().getDimensionPixelSize(R.dimen.textsize_cap_visitstoday));
            tv.setTextColor(getResources()
                    .getColor(R.color.tab_background));
        }

        setTabColor(tabHost);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            public void onTabChanged(String arg0) {

                setTabColor(tabHost);

            }
        });

        return view;
    }

    public void setTabColor(TabHost tabhost) {

        for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
            tabhost.getTabWidget().getChildAt(i)
                    .setBackgroundColor(getResources()
                            .getColor(R.color.tab_border));
        }
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
                .setBackgroundColor(getResources()
                        .getColor(R.color.colorPrimary));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBecameVisible() {
    }
}
