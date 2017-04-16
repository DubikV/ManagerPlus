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
import com.gmail.vanyadubik.managerplus.activity.VisitDetailActivity;
import com.gmail.vanyadubik.managerplus.adapter.VisitListAdapter;
import com.gmail.vanyadubik.managerplus.adapter.tabadapter.FragmentBecameVisibleInterface;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.db.element.Client_Element;
import com.gmail.vanyadubik.managerplus.model.db.document.Visit_Document;
import com.gmail.vanyadubik.managerplus.model.documents.VisitList;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class VisitListFragment extends Fragment implements FragmentBecameVisibleInterface {
    private static  final int LAYOUT = R.layout.fragment_visit_list;

    @Inject
    DataRepository dataRepository;

    private View view;
    private List<VisitList> list;
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

        FloatingActionButton visitAddBtn = (FloatingActionButton) view.findViewById(R.id.visit_add_bt);
        visitAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), VisitDetailActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        list = new ArrayList<>();

        List<Visit_Document> visits = dataRepository.getAllVisit();

        for (Visit_Document visit : visits) {
            VisitList visitList = new VisitList(visit.getExternalId(), visit.getDate(), visit.getTypeVisit());
            Client_Element client = dataRepository.getClient(visit.getClientExternalId());
            if (client != null) {
                visitList.setClient(client.getName());
            }
            list.add(visitList);
        }

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
