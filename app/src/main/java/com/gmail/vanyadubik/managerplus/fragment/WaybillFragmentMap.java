package com.gmail.vanyadubik.managerplus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.vanyadubik.managerplus.R;

public class WaybillFragmentMap extends Fragment {
    private static  final int LAYOUT = R.layout.fragment_waybill;
    private View view;

    public static WaybillFragmentMap getInstance() {

        Bundle args = new Bundle();
        WaybillFragmentMap fragment = new WaybillFragmentMap();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);

        return view;
    }

}
