package com.gmail.vanyadubik.managerplus.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.adapter.VisitListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.db.Visit_Element;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import java.util.List;

import javax.inject.Inject;

public class VisitListFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_visit_list;

    @Inject
    DataRepository dataRepository;

    private View view;
    private List<Visit_Element> list;
    private ListView listView;

    public static VisitListFragment getInstance() {

        Bundle args = new Bundle();
        VisitListFragment fragment = new VisitListFragment();
        fragment.setArguments(args);
        return  fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(LAYOUT, container, false);
        ((ManagerPlusAplication) getActivity().getApplication()).getComponent().inject(this);

        listView = (ListView) view.findViewById(R.id.visitlist_listview);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        list = dataRepository.getAllVisit();

        VisitListAdapter adapter = new VisitListAdapter(getActivity(), list);
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
