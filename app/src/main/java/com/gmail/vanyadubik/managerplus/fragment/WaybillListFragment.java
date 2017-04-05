package com.gmail.vanyadubik.managerplus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.WaybillListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.db.Waybill_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import java.util.List;

import javax.inject.Inject;

public class WaybillListFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_waybill_list;

    @Inject
    DataRepository dataRepository;

    private View view;
    private List<Waybill_Element> list;
    private ListView listView;

    public static WaybillListFragment getInstance() {

        Bundle args = new Bundle();
        WaybillListFragment fragment = new WaybillListFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ((ManagerPlusAplication) getActivity().getApplication()).getComponent().inject(this);

        listView = (ListView) view.findViewById(R.id.waybill_listview);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        list = dataRepository.getAllWaybill();

        WaybillListAdapter adapter = new WaybillListAdapter(getActivity(), list);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBecameVisible() {

    }

    @Override
    public void onBecameUnVisible() {

    }
}
