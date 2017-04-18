package com.gmail.vanyadubik.managerplus.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.FuelDetailActivity;
import com.gmail.vanyadubik.managerplus.adapter.FuelListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.db.document.Fuel_Document;
import com.gmail.vanyadubik.managerplus.model.documents.FuelList;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FuelListFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_fuel_list;

    @Inject
    DataRepository dataRepository;

    private View view;
    private List<FuelList> list;
    private ListView listView;

    public static FuelListFragment getInstance() {

        Bundle args = new Bundle();
        FuelListFragment fragment = new FuelListFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ((ManagerPlusAplication) getActivity().getApplication()).getComponent().inject(this);

        FloatingActionButton visitAddBtn = (FloatingActionButton) view.findViewById(R.id.fuel_add_bt);
        visitAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FuelDetailActivity.class));
            }
        });

        listView = (ListView) view.findViewById(R.id.fuellist_listview);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        list = new ArrayList<>();

        List<Fuel_Document> fuellist = dataRepository.getAllFuel();

        for (Fuel_Document fuel_document : fuellist) {
            list.add(
                    new FuelList(
                            fuel_document.getExternalId(),
                            fuel_document.getDate(),
                            fuel_document.getTypeFuel(),
                            fuel_document.getLitres()));
        }

        FuelListAdapter adapter = new FuelListAdapter(getActivity(), list);
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
